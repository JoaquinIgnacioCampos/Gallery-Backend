package com.uade.tpo.grupo11.gallery.demo;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

// Funciones chicas que usan todas las demas.
final class Util {

    private Util() {
    }

    static final LocalDate HOY = LocalDate.now();

    static String sha1(Path archivo) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-1").digest(Files.readAllBytes(archivo));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (IOException | java.security.NoSuchAlgorithmException e) {
            throw new Falla("No se pudo leer " + archivo + ": " + e.getMessage());
        }
    }

    // Valor como literal SQL (en una sola linea).
    static String lit(Object v) {
        if (v == null) return "NULL";
        if (v instanceof Boolean) return ((Boolean) v) ? "1" : "0";
        if (v instanceof BigDecimal) return ((BigDecimal) v).toPlainString();
        if (v instanceof Number) return v.toString();
        return "'" + v.toString().replace("\\", "\\\\").replace("'", "''").replace("\n", "\\n") + "'";
    }

    // Dos numeros iguales (o los dos vacios).
    static boolean numIgual(Object a, Object b) {
        if (a == null || b == null) return a == null && b == null;
        return Math.abs(new BigDecimal(a.toString()).subtract(new BigDecimal(b.toString())).doubleValue()) < 0.005;
    }

    static String listaIds(Collection<Long> ids) {
        return ids.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    // vence: ("vigente", 60) -> dentro de 60 dias; ("vencido", 10) -> hace 10 dias.
    static String fechaDe(Semilla.Vence vence) {
        if (vence == null) return null;
        return HOY.plusDays(vence.tipo().equals("vigente") ? vence.dias() : -vence.dias()).toString();
    }

    // Un descuento esta vigente o vencido; la fecha exacta cambia todos los dias, asi que se compara esto.
    static String categoria(String fechaIso) {
        if (fechaIso == null || fechaIso.isEmpty()) return null;
        return LocalDate.parse(fechaIso.substring(0, 10)).isBefore(HOY) ? "vencido" : "vigente";
    }

    static String clave(String... partes) {
        return String.join("|", partes);
    }

    // Corre f sobre cada elemento con hasta "hilos" a la vez y devuelve los resultados en el mismo orden.
    // Si algo falla, relanza esa falla (no un ExecutionException).
    static <T, R> List<R> paralelo(int hilos, List<T> items, Function<T, R> f) {
        ExecutorService ex = Executors.newFixedThreadPool(Math.max(1, Math.min(hilos, Math.max(1, items.size()))));
        try {
            List<Future<R>> futuros = new ArrayList<>();
            for (T item : items) futuros.add(ex.submit(() -> f.apply(item)));
            List<R> resultados = new ArrayList<>();
            for (Future<R> futuro : futuros) {
                try {
                    resultados.add(futuro.get());
                } catch (ExecutionException e) {
                    if (e.getCause() instanceof RuntimeException) throw (RuntimeException) e.getCause();
                    throw new Falla(String.valueOf(e.getCause()));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new Falla("Interrumpido");
                }
            }
            return resultados;
        } finally {
            ex.shutdown();
        }
    }
}
