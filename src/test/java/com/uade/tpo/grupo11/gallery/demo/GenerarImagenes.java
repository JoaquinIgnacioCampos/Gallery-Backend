package com.uade.tpo.grupo11.gallery.demo;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Genera las imagenes derivadas de la demo a partir de las 7 pinturas originales (demo/imagenes/obras/obra-N.jpg):
 *
 *   - recortes de detalle: segundas y terceras fotos de una obra (la galeria) y los "estudios", que son obras propias
 *     del mismo artista hechas a partir de un detalle de otra,
 *   - los marcos (PNG con el centro transparente).
 *
 * No hace falta correrla para usar la demo: los resultados ya estan en el repositorio. Sirve para regenerarlos si se
 * cambia una pintura original o un recorte. Se corre con el triangulo verde de IntelliJ (sin argumentos).
 */
public final class GenerarImagenes {

    private GenerarImagenes() {
    }

    // nombre de salida, pintura original, y la zona recortada como fraccion de la imagen (izquierda, arriba, derecha, abajo)
    private record Recorte(String salida, String origen, double izq, double arr, double der, double aba) {
    }

    private static final List<Recorte> RECORTES = List.of(
            // galeria de "Ojo de girasol"
            new Recorte("detalle-1-ojo", "obra-1", 0.00, 0.20, 0.60, 0.70),
            new Recorte("detalle-1-pincelada", "obra-1", 0.55, 0.30, 1.00, 0.75),
            // galeria de "Rosa de vitral"
            new Recorte("detalle-2-rosa", "obra-2", 0.08, 0.08, 0.82, 0.58),
            // estudios de Ana Bosch
            new Recorte("estudio-pupila", "obra-1", 0.05, 0.30, 0.65, 0.68),
            new Recorte("estudio-petalos", "obra-1", 0.35, 0.50, 1.00, 1.00),
            new Recorte("estudio-girasol", "obra-1", 0.45, 0.00, 1.00, 0.45),
            new Recorte("estudio-hojas", "obra-2", 0.00, 0.48, 0.95, 1.00),
            new Recorte("estudio-vitral", "obra-2", 0.40, 0.00, 1.00, 0.50),
            new Recorte("estudio-rojo", "obra-6", 0.40, 0.00, 1.00, 0.62),
            new Recorte("estudio-reflejo", "obra-6", 0.00, 0.55, 1.00, 1.00),
            new Recorte("estudio-sol", "obra-6", 0.00, 0.18, 0.62, 0.68),
            // Bruno Vega
            new Recorte("estudio-mirada", "obra-3", 0.15, 0.15, 0.95, 0.58),
            new Recorte("boceto-barba", "obra-3", 0.25, 0.55, 1.00, 1.00),
            // Carla Mendez
            new Recorte("detalle-4-rostro", "obra-4", 0.20, 0.05, 0.90, 0.48),
            new Recorte("detalle-7-ojo", "obra-7", 0.10, 0.15, 0.80, 0.60),
            new Recorte("estudio-manos", "obra-4", 0.05, 0.35, 0.60, 0.85),
            // Diego Rivas
            new Recorte("detalle-5-cabeza", "obra-5", 0.05, 0.00, 0.95, 0.55)
    );

    // Un recorte chico se agranda como mucho 1,6 veces para que el lado corto llegue a LADO_CORTO.
    private static final int LADO_CORTO = 600;
    private static final double AGRANDAR_MAX = 1.6;

    // Marcos: PNG con el centro transparente. El front los usa con border-image y slice 58, asi que todo el dibujo del
    // marco tiene que entrar en los 58 px del borde. Cada banda: desde que distancia del borde y de que color
    // (bandas concentricas, de afuera hacia adentro).
    private record Banda(int desde, int r, int g, int b) {
    }

    private static Banda banda(int desde, int r, int g, int b) {
        return new Banda(desde, r, g, b);
    }

    public static void main(String[] args) throws IOException {
        Path obras = Rutas.proyecto().resolve("demo/imagenes/obras");
        Path marcos = Rutas.proyecto().resolve("demo/imagenes/marcos");
        Files.createDirectories(marcos);

        System.out.println("Recortes en " + obras);
        for (Recorte r : RECORTES) recortar(obras, r);

        System.out.println("Marcos en " + marcos);
        marco(marcos, "marco-negro.png", List.of(banda(0, 70, 70, 70), banda(5, 18, 18, 18), banda(50, 95, 95, 95), banda(54, 10, 10, 10)));
        marco(marcos, "marco-dorado.png", List.of(banda(0, 110, 80, 20), banda(3, 232, 197, 110), banda(13, 201, 162, 74),
                banda(24, 122, 90, 28), banda(30, 226, 190, 100), banda(44, 176, 136, 52), banda(52, 110, 80, 20)));
        marco(marcos, "marco-madera.png", List.of(banda(0, 60, 35, 18), banda(4, 120, 75, 40), banda(14, 94, 56, 28),
                banda(30, 135, 88, 50), banda(44, 80, 48, 24), banda(52, 50, 28, 14)));
        marco(marcos, "marco-blanco.png", List.of(banda(0, 200, 200, 196), banda(4, 250, 250, 247), banda(46, 236, 236, 232), banda(54, 190, 190, 186)));
        marco(marcos, "marco-plata.png", List.of(banda(0, 110, 112, 118), banda(3, 222, 224, 230), banda(14, 170, 174, 182),
                banda(30, 236, 238, 244), banda(44, 150, 154, 162), banda(52, 104, 106, 112)));
    }

    private static void recortar(Path obras, Recorte r) throws IOException {
        BufferedImage original = ImageIO.read(obras.resolve(r.origen() + ".jpg").toFile());
        int ancho = original.getWidth();
        int alto = original.getHeight();
        int x0 = (int) Math.round(r.izq() * ancho);
        int y0 = (int) Math.round(r.arr() * alto);
        int x1 = (int) Math.round(r.der() * ancho);
        int y1 = (int) Math.round(r.aba() * alto);
        BufferedImage parte = original.getSubimage(x0, y0, x1 - x0, y1 - y0);

        int corto = Math.min(parte.getWidth(), parte.getHeight());
        double factor = Math.min(AGRANDAR_MAX, Math.max(1.0, (double) LADO_CORTO / corto));
        BufferedImage salida = new BufferedImage((int) Math.round(parte.getWidth() * factor), (int) Math.round(parte.getHeight() * factor), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = salida.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(parte, 0, 0, salida.getWidth(), salida.getHeight(), null);
        g.dispose();

        guardarJpeg(salida, obras.resolve(r.salida() + ".jpg"));
        System.out.printf("  %-24s %dx%d  (de %s)%n", r.salida() + ".jpg", salida.getWidth(), salida.getHeight(), r.origen());
    }

    private static void guardarJpeg(BufferedImage imagen, Path destino) throws IOException {
        ImageWriter escritor = ImageIO.getImageWritersByFormatName("jpeg").next();
        ImageWriteParam parametros = escritor.getDefaultWriteParam();
        parametros.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        parametros.setCompressionQuality(0.90f);
        try (ImageOutputStream salida = ImageIO.createImageOutputStream(destino.toFile())) {
            escritor.setOutput(salida);
            escritor.write(null, new IIOImage(imagen, null, null), parametros);
        } finally {
            escritor.dispose();
        }
    }

    private static void marco(Path carpeta, String nombre, List<Banda> bandas) throws IOException {
        int ancho = 400;
        int alto = 500;
        int borde = 58;
        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagen.createGraphics();
        for (Banda b : bandas) {
            g.setColor(new Color(b.r(), b.g(), b.b(), 255));
            g.fillRect(b.desde(), b.desde(), ancho - 2 * b.desde(), alto - 2 * b.desde());
        }
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(borde, borde, ancho - 2 * borde, alto - 2 * borde);  // el centro, transparente
        g.dispose();
        ImageIO.write(imagen, "png", carpeta.resolve(nombre).toFile());
        System.out.println("  " + nombre);
    }
}
