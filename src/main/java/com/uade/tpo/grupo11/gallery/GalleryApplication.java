package com.uade.tpo.grupo11.gallery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GalleryApplication {

    // Arranca la aplicacion. Spring Boot levanta el servidor y crea todos los beans.
    public static void main(String[] args) {
        SpringApplication.run(GalleryApplication.class, args);
    }

}
