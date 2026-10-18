package com.uade.tpo.grupo11.gallery.demo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * LA SEMILLA: como tiene que quedar la base despues de correr ResetDemo.
 *
 * Solo datos, nada de logica: para agregar una obra, un usuario o una compra se agrega una entrada aca.
 * ResetDemo compara la base con esto y corrige solo lo que difiere.
 *
 * Las imagenes salen de demo/imagenes/obras (fotos) y demo/imagenes/marcos (marcos). Cada archivo se sube UNA vez a
 * Cloudinary y lo usa una sola obra: asi no hay archivos repetidos.
 *
 * Que cubre (para mostrar o probar):
 *   PAGINADO   Catalogo (3 por pagina, 15 obras publicadas), Artistas (3 por pagina, 5 artistas), perfil de Ana
 *              (6 por pagina, 11 obras), Mis obras de Ana (10 por pagina), Mis ventas de Ana (10 por pagina, 12 ventas)
 *              y Usuarios del admin (10 por pagina, 14 usuarios).
 *   VACIOS     artista sin obras ni ventas (Elena), cliente sin compras ni carrito (cliente7).
 *   ERRORES    stock 0, ultima unidad pedida por dos clientes, obra despublicada o eliminada con items en un carrito,
 *              estilo/tamano/marco en uso (no se pueden borrar), artista que no acepta encargos, descuento vencido.
 *   ROLES      admin, artistas que tambien compran, clientes y un cliente que todavia no publica (para el boton
 *              "Queres publicar?").
 *   ENCARGOS   uno en cada estado: PENDIENTE (con y sin precio), APROBADO, EN_PROCESO, TERMINADO, PAGADO y CANCELADO.
 */
public final class Semilla {

    private Semilla() {
    }

    // ------------------------------------------------------------------------------------------ tipos
    public record Perfil(String nombreArtistico, boolean aceptaEncargos) {
    }

    // rol: ADMIN, ARTISTA_CLIENTE (tiene perfil de artista y tambien compra) o CLIENTE.
    // direccion: para el envio a domicilio (null = no tiene).
    public record Usuario(String email, String usuario, String nombre, String apellido, String clave, String rol,
                          String direccion, Perfil perfil, String telefono) {
    }

    public record Tamanio(String nombre, double ancho, double largo) {
    }

    public record Marco(String nombre, String color, int precio, String archivo) {
    }

    // tipo: "vigente" (vence dentro de tantos dias) o "vencido" (vencio hace tantos dias).
    public record Vence(String tipo, int dias) {
    }

    // stock: el FINAL, despues de las compras del historial.
    public record Variante(String tam, int precio, int stock, Integer pct, Vence hasta) {
    }

    // clave: nombre corto para nombrarla en las compras y carritos. fotos: archivos de demo/imagenes/obras, en orden
    // (la primera es la portada). Sin fotos o sin variantes la obra no aparece en el catalogo. enVenta=false:
    // despublicada (solo la ve su artista). eliminada=true: borrada.
    public record Obra(String clave, String nombre, String artista, List<String> estilos, String desc, List<String> fotos,
                       List<Variante> variantes, boolean enVenta, boolean eliminada) {
    }

    public record ItemCompra(String obra, String tam, String marco, int cant) {
    }

    // entrega: ENVIO_DOMICILIO o RETIRO_LOCAL.
    public record Compra(String cliente, String entrega, List<ItemCompra> items) {
    }

    public record Mensaje(String quien, String texto) {
    }

    // estado final: PENDIENTE, APROBADO, EN_PROCESO, TERMINADO, PAGADO o CANCELADO. precio: lo que propone el artista
    // (en PENDIENTE queda propuesto; del APROBADO en adelante, aceptado).
    public record Encargo(String cliente, String artista, String estado, String desc, String tamanio, String marco,
                          String pintura, String lienzo, Integer precio, List<Mensaje> mensajes) {
    }

    // ------------------------------------------------------------------------------------------ atajos para escribir la semilla
    private static Usuario usuario(String email, String nombre, String apellido, String clave, String rol, String direccion, Perfil perfil) {
        long suma = 0;
        for (char c : email.toCharArray()) suma += c;
        return new Usuario(email, email.split("@")[0], nombre, apellido, clave, rol, direccion, perfil,
                "11" + String.format("%08d", suma * 7919 % 100000000));
    }

    private static Variante v(String tamanio, int precio, int stock, Integer descuento, Vence vence) {
        return new Variante(tamanio, precio, stock, descuento, vence);
    }

    private static Obra obra(String clave, String nombre, String artista, List<String> estilos, String descripcion,
                             List<String> fotos, List<Variante> variantes, boolean enVenta, boolean eliminada) {
        return new Obra(clave, nombre, artista, estilos, descripcion, fotos, variantes, enVenta, eliminada);
    }

    private static ItemCompra item(String obra, String tamanio, String marco, int cantidad) {
        return new ItemCompra(obra, tamanio, marco, cantidad);
    }

    private static Compra compra(String cliente, String entrega, ItemCompra... items) {
        return new Compra(cliente, entrega, List.of(items));
    }

    private static Encargo encargo(String cliente, String artista, String estado, String desc, String tamanio, String marco,
                                   String pintura, String lienzo, Integer precio, List<Mensaje> mensajes) {
        return new Encargo(cliente, artista, estado, desc, tamanio, marco, pintura, lienzo, precio, mensajes);
    }

    // clave, lista, clave, lista...
    @SafeVarargs
    private static Map<String, List<ItemCompra>> carritos(Object... pares) {
        Map<String, List<ItemCompra>> m = new LinkedHashMap<>();
        for (int i = 0; i < pares.length; i += 2) {
            @SuppressWarnings("unchecked")
            List<ItemCompra> items = (List<ItemCompra>) pares[i + 1];
            m.put((String) pares[i], items);
        }
        return m;
    }

    // ------------------------------------------------------------------------------------------ usuarios
    public static final String CLAVE_ADMIN = "Admin123!";
    public static final String CLAVE_ARTISTA = "Artista123!";
    public static final String CLAVE_CLIENTE = "Cliente123!";

    public static final String ANA = "artista1@gallery.com";
    public static final String BRUNO = "artista2@gallery.com";
    public static final String CARLA = "artista3@gallery.com";
    public static final String DIEGO = "artista4@gallery.com";
    public static final String ELENA = "artista5@gallery.com";

    public static final String C1 = "cliente1@gallery.com";
    public static final String C2 = "cliente2@gallery.com";
    public static final String C3 = "cliente3@gallery.com";
    public static final String C4 = "cliente4@gallery.com";
    public static final String C5 = "cliente5@gallery.com";
    public static final String C6 = "cliente6@gallery.com";
    public static final String C8 = "cliente8@gallery.com";

    public static final String ADMIN = "admin@gallery.com";

    public static final List<Usuario> USUARIOS = List.of(
            usuario("admin@gallery.com", "Admin", "Gallery", CLAVE_ADMIN, "ADMIN", null, null),
            usuario("artista1@gallery.com", "Ana", "Bosch", CLAVE_ARTISTA, "ARTISTA_CLIENTE", null, new Perfil("Ana Bosch", true)),
            usuario("artista2@gallery.com", "Bruno", "Vega", CLAVE_ARTISTA, "ARTISTA_CLIENTE", null, new Perfil("Bruno Vega", false)),
            usuario("artista3@gallery.com", "Carla", "Mendez", CLAVE_ARTISTA, "ARTISTA_CLIENTE", null, new Perfil("Carla Mendez", true)),
            usuario("artista4@gallery.com", "Diego", "Rivas", CLAVE_ARTISTA, "ARTISTA_CLIENTE", null, new Perfil("Diego Rivas", true)),
            usuario("artista5@gallery.com", "Elena", "Ruiz", CLAVE_ARTISTA, "ARTISTA_CLIENTE", null, new Perfil("Elena Ruiz", true)),
            usuario("cliente1@gallery.com", "Lucia", "Fernandez", CLAVE_CLIENTE, "CLIENTE", "Av. Siempre Viva 742", null),
            usuario("cliente2@gallery.com", "Martin", "Gomez", CLAVE_CLIENTE, "CLIENTE", "Calle Falsa 123", null),
            usuario("cliente3@gallery.com", "Sofia", "Paz", CLAVE_CLIENTE, "CLIENTE", "Av. Libertador 2450", null),
            usuario("cliente4@gallery.com", "Tomas", "Rey", CLAVE_CLIENTE, "CLIENTE", null, null),
            usuario("cliente5@gallery.com", "Valentina", "Diaz", CLAVE_CLIENTE, "CLIENTE", "Mitre 88", null),
            usuario("cliente6@gallery.com", "Julian", "Castro", CLAVE_CLIENTE, "CLIENTE", "San Martin 1500", null),
            usuario("cliente7@gallery.com", "Camila", "Lopez", CLAVE_CLIENTE, "CLIENTE", null, null),
            usuario("cliente8@gallery.com", "Nicolas", "Silva", CLAVE_CLIENTE, "CLIENTE", "Belgrano 321", null)
    );

    // ------------------------------------------------------------------------------------------ catalogos
    // Lo que se usa en alguna obra, compra o encargo NO se puede borrar (muestra el error de "en uso"); lo ultimo de
    // cada lista (Minimalismo, Mural, Marco Plata) no se usa en nada y si se puede borrar.
    public static final List<String> ESTILOS = List.of("Impresionismo", "Abstracto", "Realismo", "Expresionismo", "Surrealismo", "Pop Art", "Minimalismo");

    public static final List<Tamanio> TAMANIOS = List.of(
            new Tamanio("A4", 21.0, 29.7),
            new Tamanio("A3", 29.7, 42.0),
            new Tamanio("A2", 42.0, 59.4),
            new Tamanio("50 x 70 cm", 50.0, 70.0),
            new Tamanio("Mural", 100.0, 150.0)
    );

    public static final List<Marco> MARCOS = List.of(
            new Marco("Marco Negro Simple", "Negro", 1500, "marco-negro.png"),
            new Marco("Marco Dorado Clasico", "Dorado", 2500, "marco-dorado.png"),
            new Marco("Marco Madera Nogal", "Marron", 2200, "marco-madera.png"),
            new Marco("Marco Blanco Galeria", "Blanco", 1800, "marco-blanco.png"),
            new Marco("Marco Plata", "Plata", 2000, "marco-plata.png")
    );

    public static final String NEGRO = MARCOS.get(0).nombre();
    public static final String DORADO = MARCOS.get(1).nombre();
    public static final String MADERA = MARCOS.get(2).nombre();
    public static final String BLANCO = MARCOS.get(3).nombre();

    // ------------------------------------------------------------------------------------------ obras
    // Variantes: v(tamano, precio, stock FINAL, descuento %, vencimiento).
    public static final List<Obra> OBRAS = List.of(
            // ---- Ana Bosch: 11 obras (con eso se pagina Mis obras y su perfil publico) ----
            obra("ojo", "Ojo de girasol", ANA,
                    List.of("Surrealismo", "Realismo"),
                    "Oleo sobre lienzo con espatula. Un ojo que se funde con un girasol: la mitad de la cara se vuelve flor.",
                    List.of("obra-1.jpg", "detalle-1-ojo.jpg", "detalle-1-pincelada.jpg"),
                    List.of(v("A4", 18000, 6, null, null), v("A3", 30000, 4, 20, new Vence("vigente", 60)), v("A2", 52000, 2, null, null)),
                    true, false),
            obra("rosa", "Rosa de vitral", ANA,
                    List.of("Abstracto"),
                    "Oleo con relieve. Una rosa armada con planos de color, como un vitral, sobre un fondo dorado.",
                    List.of("obra-2.jpg", "detalle-2-rosa.jpg"),
                    List.of(v("A4", 14000, 8, null, null), v("A3", 24000, 5, null, null), v("50 x 70 cm", 45000, 3, null, null)),
                    true, false),
            obra("ciudad", "Ciudad reflejada", ANA,
                    List.of("Abstracto", "Impresionismo"),
                    "Acrilico con espatula. Una ciudad rojiza que se refleja en el agua, con un sol rojo a la izquierda.",
                    List.of("obra-6.jpg"),
                    List.of(v("A2", 48000, 3, null, null), v("50 x 70 cm", 42000, 4, 10, new Vence("vigente", 30))),
                    true, false),
            obra("est-pupila", "Estudio de pupila", ANA,
                    List.of("Realismo"),
                    "Estudio a partir de Ojo de girasol: la pupila y las pestañas, trabajadas en detalle.",
                    List.of("estudio-pupila.jpg"),
                    List.of(v("A4", 8500, 9, null, null), v("A3", 14500, 5, null, null)),
                    true, false),
            obra("est-petalos", "Estudio de petalos dorados", ANA,
                    List.of("Surrealismo"),
                    "Los petalos inferiores del girasol, con la espatula marcada y los ocres quemados.",
                    List.of("estudio-petalos.jpg"),
                    List.of(v("A4", 8000, 7, null, null)),
                    true, false),
            obra("est-girasol", "Girasol en sombra", ANA,
                    List.of("Surrealismo", "Expresionismo"),
                    "El corazon del girasol sobre fondo negro, en una pincelada ancha y gestual.",
                    List.of("estudio-girasol.jpg"),
                    List.of(v("A4", 8200, 10, null, null)),
                    true, false),
            obra("est-hojas", "Estudio de hojas", ANA,
                    List.of("Abstracto"),
                    "Las hojas de la rosa de vitral como un estudio aparte: verdes planos separados por lineas negras.",
                    List.of("estudio-hojas.jpg"),
                    List.of(v("A4", 7800, 6, null, null)),
                    true, false),
            obra("est-vitral", "Estudio de vitral", ANA,
                    List.of("Abstracto"),
                    "Un recorte de la rosa: los rojos y los amarillos del vitral en primer plano.",
                    List.of("estudio-vitral.jpg"),
                    List.of(v("A4", 8800, 8, 15, new Vence("vigente", 15)), v("A3", 15000, 4, null, null)),
                    true, false),
            obra("est-rojo", "Estudio de rojo urbano", ANA,
                    List.of("Abstracto"),
                    "La franja roja de Ciudad reflejada, aislada como una pieza vertical.",
                    List.of("estudio-rojo.jpg"),
                    List.of(v("A4", 7500, 9, null, null), v("A3", 13000, 3, null, null)),
                    true, false),
            obra("est-reflejo", "Estudio de reflejo", ANA,
                    List.of("Impresionismo"),
                    "La parte baja de la ciudad: los azules y los ocres del reflejo en el agua.",
                    List.of("estudio-reflejo.jpg"),
                    List.of(v("A4", 7600, 7, null, null)),
                    true, false),
            obra("est-sol", "Estudio de sol rojo", ANA,
                    List.of("Abstracto"),
                    "El sol rojo sobre blanco y amarillo, reducido a lo esencial.",
                    List.of("estudio-sol.jpg"),
                    List.of(v("A4", 8100, 8, null, null)),
                    true, false),
            // ---- Bruno Vega (no acepta encargos): una publicada, una despublicada, una eliminada ----
            obra("mirada", "Mirada", BRUNO,
                    List.of("Expresionismo"),
                    "Acrilico sobre tela. Un rostro cortado por el encuadre, con la mirada como unico punto de calma.",
                    List.of("obra-3.jpg"),
                    List.of(v("A3", 26000, 3, 15, new Vence("vencido", 10)), v("A2", 50000, 1, null, null)),
                    true, false),
            obra("est-mirada", "Estudio de la mirada", BRUNO,
                    List.of("Expresionismo"),
                    "Estudio previo de Mirada. Despublicada: solo la ve su artista, en Mis obras.",
                    List.of("estudio-mirada.jpg"),
                    List.of(v("A4", 9000, 5, null, null)),
                    false, false),
            obra("boceto", "Boceto descartado", BRUNO,
                    List.of("Expresionismo"),
                    "Un boceto que el artista dio de baja. Ya esta eliminada: no se ve en ningun listado, pero queda en el historial de quien la compro.",
                    List.of("boceto-barba.jpg"),
                    List.of(v("A4", 6000, 3, null, null)),
                    true, true),
            // ---- Carla Mendez: una con dos fotos y descuento, una sin variantes, una sin fotos ----
            obra("retrato", "Retrato dorado", CARLA,
                    List.of("Expresionismo", "Abstracto"),
                    "Tecnica mixta con hoja de oro. Un retrato armado con planos y telas, entre el cubismo y el collage.",
                    List.of("obra-4.jpg", "detalle-4-rostro.jpg"),
                    List.of(v("A3", 28000, 4, null, null), v("A2", 55000, 2, null, null)),
                    true, false),
            obra("jaguar", "Jaguar rosa", CARLA,
                    List.of("Pop Art"),
                    "Acrilico en colores saturados. Un jaguar de manchas negras sobre rosas, azules y naranjas.",
                    List.of("obra-7.jpg", "detalle-7-ojo.jpg"),
                    List.of(v("A4", 16000, 5, null, null), v("A3", 27000, 3, 25, new Vence("vigente", 45))),
                    true, false),
            obra("retrato-proceso", "Retrato en proceso", CARLA,
                    List.of("Expresionismo"),
                    "Todavia sin variantes (tamanos y precios): no aparece en el catalogo hasta que se carguen.",
                    List.of("estudio-manos.jpg"),
                    List.of(),
                    true, false),
            obra("borrador", "Borrador sin fotos", CARLA,
                    List.of("Realismo"),
                    "Tiene variantes pero todavia no tiene fotos: no aparece en el catalogo hasta que se suban.",
                    List.of(),
                    List.of(v("A4", 5000, 2, null, null)),
                    true, false),
            // ---- Diego Rivas: un caballo sin stock en un tamano y con la ultima unidad en el otro ----
            obra("caballo", "Caballo pardo", DIEGO,
                    List.of("Realismo"),
                    "Oleo sobre lienzo. Un caballo pardo de frente, con la crin al viento y la mirada atenta.",
                    List.of("obra-5.jpg", "detalle-5-cabeza.jpg"),
                    List.of(v("A4", 12000, 0, null, null), v("A3", 21000, 1, null, null)),
                    true, false)
            // Elena Ruiz no tiene obras: sirve para ver Mis obras, Mis ventas y el perfil publico vacios.
    );

    // ------------------------------------------------------------------------------------------ compras del historial
    // Cada una sale por el checkout, asi que descuenta stock y emite una factura por artista. Ana participa en 12 de
    // estas compras: de ahi sus 12 ventas en Mis ventas.
    public static final String ENVIO = "ENVIO_DOMICILIO";
    public static final String RETIRO = "RETIRO_LOCAL";

    public static final List<Compra> COMPRAS = List.of(
            compra(C1, ENVIO, item("ojo", "A4", NEGRO, 1)),
            compra(C1, RETIRO, item("rosa", "A3", DORADO, 1), item("jaguar", "A4", MADERA, 1)),
            compra(C1, ENVIO, item("boceto", "A4", BLANCO, 1), item("est-pupila", "A4", NEGRO, 1)),
            compra(C2, RETIRO, item("ciudad", "A2", DORADO, 1)),
            compra(C3, ENVIO, item("est-vitral", "A4", MADERA, 2), item("caballo", "A4", NEGRO, 1)),
            compra(C3, RETIRO, item("est-petalos", "A4", BLANCO, 1)),
            compra(C5, ENVIO, item("est-hojas", "A4", NEGRO, 1), item("retrato", "A3", DORADO, 1)),
            compra(C6, RETIRO, item("est-rojo", "A4", MADERA, 1)),
            compra(C8, ENVIO, item("est-reflejo", "A4", BLANCO, 1), item("caballo", "A3", DORADO, 1)),
            compra(C8, RETIRO, item("ojo", "A3", NEGRO, 1)),
            compra(C1, ENVIO, item("est-sol", "A4", DORADO, 1)),
            compra(C2, ENVIO, item("est-girasol", "A4", MADERA, 1), item("mirada", "A3", NEGRO, 1)),
            compra(C5, RETIRO, item("jaguar", "A3", BLANCO, 1))
    );

    // ------------------------------------------------------------------------------------------ carritos
    // Como tienen que quedar (se arman despues de las compras). Un cliente con lista vacia = carrito vacio.
    //   cliente2: listo para hacer el checkout en vivo
    //   cliente3: una obra despublicada y una eliminada (el checkout las rechaza; la otra es valida)
    //   cliente4: sin direccion cargada (al elegir envio a domicilio tiene que escribirla)
    //   cliente5 y cliente6: los dos tienen la ULTIMA unidad del Caballo A3 (el que compre segundo recibe el error de stock)
    //   cliente8: cantidades mayores a uno y obras de dos artistas
    //   cliente1 y cliente7: carritos vacios
    public static final Map<String, List<ItemCompra>> CARRITOS = carritos(
            C2, List.of(item("rosa", "A4", BLANCO, 2), item("jaguar", "A3", NEGRO, 1)),
            C3, List.of(item("est-mirada", "A4", BLANCO, 1), item("boceto", "A4", NEGRO, 1), item("ojo", "A4", DORADO, 1)),
            C4, List.of(item("rosa", "A3", MADERA, 1)),
            C5, List.of(item("caballo", "A3", NEGRO, 1)),
            C6, List.of(item("caballo", "A3", MADERA, 1)),
            C8, List.of(item("ciudad", "50 x 70 cm", NEGRO, 1), item("retrato", "A3", BLANCO, 2)),
            C1, List.of(),
            "cliente7@gallery.com", List.of()
    );

    // ------------------------------------------------------------------------------------------ encargos
    // Uno por cada estado. TERMINADO y PAGADO emiten una factura al artista (Diego).
    public static final List<Encargo> ENCARGOS = List.of(
            encargo(C8, ANA, "PENDIENTE",
                    "Retrato de mi perro en estilo realista", "A3", NEGRO, "OLEO", "LIENZO",
                    null, List.of(new Mensaje(C8, "Hola Ana! Queria encargarte un retrato de mi perro. Tengo varias fotos."))),
            encargo(C8, ANA, "PENDIENTE",
                    "Vista de la costa al amanecer", "A2", DORADO, "ACRILICO", "LIENZO",
                    18000, List.of(new Mensaje(C8, "Me encantaria algo con colores calidos."), new Mensaje(ANA, "Perfecto, te propongo $18.000 y lo tengo en tres semanas."))),
            encargo(C1, CARLA, "APROBADO",
                    "Retrato familiar en tecnica mixta", "A3", MADERA, "ACRILICO", "TELA",
                    25000, List.of(new Mensaje(C1, "Hola Carla! Son cuatro personas, te mando las fotos por aca."), new Mensaje(CARLA, "Genial, queda en $25.000. Empiezo cuando aceptes el precio."))),
            encargo(C2, ANA, "EN_PROCESO",
                    "Retrato familiar para regalo de cumpleaños", "A3", NEGRO, "OLEO", "LIENZO",
                    32000, List.of(new Mensaje(C2, "Hola! Queria consultar el tiempo estimado de entrega."), new Mensaje(ANA, "Hola! Puedo tenerlo listo en 2 semanas."))),
            encargo(C3, DIEGO, "TERMINADO",
                    "Caballo blanco en el campo", "A3", BLANCO, "OLEO", "LIENZO",
                    22000, List.of(new Mensaje(C3, "Quiero un caballo blanco corriendo en un campo."), new Mensaje(DIEGO, "Listo, lo termine. Ya podes pagarlo desde el encargo."))),
            encargo(C5, DIEGO, "PAGADO",
                    "Paisaje de montaña en acuarela", "A4", DORADO, "ACUARELA", "PAPEL",
                    15000, List.of(new Mensaje(C5, "Un paisaje de montaña con nieve, por favor."), new Mensaje(DIEGO, "Entregado! Gracias por tu compra."))),
            encargo(C6, ELENA, "CANCELADO",
                    "Mural pequeño para la cocina", "A3", MADERA, "GRAFITI", "MADERA",
                    null, List.of(new Mensaje(C6, "Hola Elena, queria un mural chico para mi cocina."), new Mensaje(ELENA, "Hola! Ahora no tengo lugar en mi agenda, lo siento."))),
            encargo(C4, ELENA, "PENDIENTE",
                    "Ilustracion para la tapa de un libro", "A4", NEGRO, "ACUARELA", "PAPEL",
                    null, List.of()),
            encargo(C1, ANA, "PENDIENTE",
                    "Paisaje de montaña en acuarela", "A4", DORADO, "ACUARELA", "PAPEL",
                    9500, List.of())
    );
}
