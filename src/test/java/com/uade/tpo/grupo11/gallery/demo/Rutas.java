package com.uade.tpo.grupo11.gallery.demo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Donde estan las cosas: la carpeta del proyecto del back, las fotos y la cache local.
// Se busca desde la carpeta de trabajo hacia arriba, asi anda igual corrido desde IntelliJ (donde la carpeta de trabajo
// es la del proyecto) que desde la terminal. Si hace falta, GALLERY_BACKEND_DIR dice cual es.
final class Rutas {

    private Rutas() {
    }

    private static Path proyecto;

    static Path proyecto() {
        if (proyecto != null) return proyecto;
        String indicada = System.getenv("GALLERY_BACKEND_DIR");
        if (indicada != null && !indicada.isBlank()) return proyecto = Paths.get(indicada).toAbsolutePath();
        Path actual = Paths.get("").toAbsolutePath();
        for (int nivel = 0; actual != null && nivel < 5; nivel++, actual = actual.getParent()) {
            if (Files.isDirectory(actual.resolve("demo/imagenes/obras")) && Files.exists(actual.resolve("pom.xml"))) {
                return proyecto = actual;
            }
        }
        throw new Falla("No encuentro la carpeta del back (donde estan pom.xml y demo/imagenes). "
                + "Corre desde la carpeta del proyecto o define GALLERY_BACKEND_DIR.");
    }

    static Path foto(String archivo) {
        return proyecto().resolve("demo/imagenes/obras").resolve(archivo);
    }

    static Path marco(String archivo) {
        return proyecto().resolve("demo/imagenes/marcos").resolve(archivo);
    }

    static Path cacheDir() {
        return proyecto().resolve("demo/.cache");
    }

    static Path cache() {
        return cacheDir().resolve("semilla.json");
    }

    static Path huerfanas() {
        return cacheDir().resolve("huerfanas-cloudinary.txt");
    }
}
