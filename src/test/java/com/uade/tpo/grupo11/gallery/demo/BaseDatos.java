package com.uade.tpo.grupo11.gallery.demo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// La base de datos, por JDBC. Se usa para leer el estado actual (una sola consulta por tabla) y para las
// correcciones directas (borrados y arreglos que no necesitan pasar por la API).
//
// La conexion sale del application.properties del back (spring.datasource.url, username y password): cada uno usa la
// suya y no hay nada fijo en el codigo. Variables de entorno opcionales:
//   GALLERY_DB_URL, GALLERY_DB_USER, GALLERY_DB_PASS   pisan lo que dice el application.properties
//   GALLERY_DB                                         cambia solo el NOMBRE de la base (para probar contra una copia descartable)
final class BaseDatos implements AutoCloseable {

    // Una fila de una tabla, con accesos que ya convierten el tipo.
    static final class Fila {
        private final Map<String, Object> valores;

        Fila(Map<String, Object> valores) {
            this.valores = valores;
        }

        Object get(String columna) {
            return valores.get(columna);
        }

        Long l(String columna) {
            Object o = valores.get(columna);
            return o == null ? null : ((Number) o).longValue();
        }

        long lp(String columna) {
            Long v = l(columna);
            if (v == null) throw new Falla("Falta el valor de " + columna);
            return v;
        }

        Integer i(String columna) {
            Object o = valores.get(columna);
            return o == null ? null : ((Number) o).intValue();
        }

        String s(String columna) {
            Object o = valores.get(columna);
            return o == null ? null : o.toString();
        }

        boolean b(String columna) {
            Object o = valores.get(columna);
            if (o instanceof Boolean) return (Boolean) o;
            return o instanceof Number && ((Number) o).intValue() != 0;
        }
    }

    // Todas las tablas leidas de una vez.
    record Datos(String destino, Map<String, List<Fila>> tablas) {
        List<Fila> t(String tabla) {
            return tablas.getOrDefault(tabla, List.of());
        }
    }

    // Todas las tablas del sistema, en un orden que respeta las claves foraneas al borrar.
    static final String[] TABLAS = {
            "items_factura", "item_carrito", "factura", "compra",
            "mensajes", "encargos", "imagen",
            "estilo_obra", "estilo_artista",
            "variante", "obra", "artista", "carrito",
            "usuario", "marco", "tamanio_lienzo", "estilos"
    };

    private final String url;
    private final String usuario;
    private final String clave;
    private Connection conexion;

    BaseDatos(Path proyecto) {
        Properties propiedades = new Properties();
        Path archivo = proyecto.resolve("src/main/resources/application.properties");
        if (Files.exists(archivo)) {
            try (Reader in = new InputStreamReader(Files.newInputStream(archivo), StandardCharsets.UTF_8)) {
                propiedades.load(in);
            } catch (IOException e) {
                throw new Falla("No se pudo leer " + archivo + ": " + e.getMessage());
            }
        }
        String u = elegir("GALLERY_DB_URL", propiedades.getProperty("spring.datasource.url"), "jdbc:mysql://localhost:3306/gallery");
        String base = System.getenv("GALLERY_DB");
        if (base != null && !base.isBlank()) {
            u = u.replaceFirst("(jdbc:mysql://[^/]+/)([^?]*)", "$1" + base);
        }
        this.url = u;
        this.usuario = elegir("GALLERY_DB_USER", propiedades.getProperty("spring.datasource.username"), "root");
        this.clave = elegir("GALLERY_DB_PASS", propiedades.getProperty("spring.datasource.password"), "");
    }

    private static String elegir(String variable, String propiedad, String pordefecto) {
        String delEntorno = System.getenv(variable);
        if (delEntorno != null && !delEntorno.isEmpty()) return delEntorno;
        if (propiedad != null && !propiedad.isBlank()) return propiedad.trim();
        return pordefecto;
    }

    // host:puerto/base, sin parametros ni credenciales: para decirle a quien corre el script sobre que base va a trabajar.
    String destino() {
        Matcher m = Pattern.compile("jdbc:mysql://([^/?]+)/([^?]*)").matcher(url);
        return m.find() ? m.group(1) + "/" + m.group(2) : "(desconocida)";
    }

    private Connection con() {
        try {
            if (conexion == null || conexion.isClosed()) conexion = DriverManager.getConnection(url, usuario, clave);
            return conexion;
        } catch (SQLException e) {
            // Sin mostrar la URL completa ni la clave: solo la causa.
            throw new Falla("No se pudo conectar a la base (" + destino() + "): " + e.getMessage()
                    + "\nRevisa spring.datasource.* en el application.properties del back.");
        }
    }

    // ddl-auto=update agrega columnas pero no las borra ni cambia tipos: si alguien levanto una version vieja del back,
    // la tabla queda desfasada del codigo actual. Es idempotente y barato.
    private void arreglarEsquema() throws SQLException {
        try (Statement st = con().createStatement()) {
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=DATABASE() "
                    + "AND TABLE_NAME='encargos' AND COLUMN_NAME='precio_aceptado'")) {
                rs.next();
                if (rs.getInt(1) > 0) st.executeUpdate("ALTER TABLE encargos DROP COLUMN precio_aceptado");
            }
            st.executeUpdate("ALTER TABLE encargos MODIFY estado_encargo "
                    + "ENUM('PENDIENTE','APROBADO','EN_PROCESO','TERMINADO','PAGADO','CANCELADO') NOT NULL");
        }
    }

    // Todas las filas de todas las tablas. Las columnas binarias (restos de cuando las fotos se guardaban como bytes)
    // no se leen.
    Datos estado() {
        try {
            arreglarEsquema();
            Map<String, List<Fila>> tablas = new LinkedHashMap<>();
            for (String tabla : TABLAS) {
                List<Fila> filas = new ArrayList<>();
                try (Statement st = con().createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM " + tabla)) {
                    ResultSetMetaData md = rs.getMetaData();
                    while (rs.next()) {
                        Map<String, Object> fila = new LinkedHashMap<>();
                        for (int c = 1; c <= md.getColumnCount(); c++) {
                            int tipo = md.getColumnType(c);
                            if (tipo == Types.BLOB || tipo == Types.LONGVARBINARY || tipo == Types.VARBINARY || tipo == Types.BINARY) continue;
                            Object v = rs.getObject(c);
                            if (v != null && !(v instanceof Number) && !(v instanceof Boolean)) v = v.toString();
                            fila.put(md.getColumnLabel(c), v);
                        }
                        filas.add(new Fila(fila));
                    }
                }
                tablas.put(tabla, filas);
            }
            return new Datos(destino(), tablas);
        } catch (SQLException e) {
            throw new Falla("No se pudo leer la base: " + e.getMessage());
        }
    }

    // Una sentencia por elemento. Se ejecutan con las claves foraneas apagadas (los borrados de la limpieza no siguen
    // el orden de las dependencias) y se cortan en la primera que falla.
    void sql(List<String> sentencias) {
        List<String> a = sentencias.stream().filter(s -> s != null && !s.isBlank()).toList();
        if (a.isEmpty()) return;
        try (Statement st = con().createStatement()) {
            st.execute("SET FOREIGN_KEY_CHECKS=0");
            int n = 0;
            try {
                for (String s : a) {
                    st.executeUpdate(s);
                    n++;
                }
            } catch (SQLException e) {
                throw new Falla("Error en la sentencia " + (n + 1) + " (" + a.get(n) + "): " + e.getMessage());
            } finally {
                st.execute("SET FOREIGN_KEY_CHECKS=1");
            }
        } catch (SQLException e) {
            throw new Falla("No se pudo ejecutar el SQL: " + e.getMessage());
        }
    }

    // Vacia todas las tablas del sistema.
    void vaciar() {
        List<String> s = new ArrayList<>();
        for (String t : TABLAS) s.add("TRUNCATE TABLE " + t);
        try (Statement st = con().createStatement()) {
            st.execute("SET FOREIGN_KEY_CHECKS=0");
            try {
                for (String x : s) st.executeUpdate(x);
            } finally {
                st.execute("SET FOREIGN_KEY_CHECKS=1");
            }
        } catch (SQLException e) {
            throw new Falla("No se pudo vaciar la base: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            if (conexion != null) conexion.close();
        } catch (SQLException ignorada) {
            // no importa: el proceso termina
        }
    }
}
