# Atelier Attack — Backend

API REST de una galería online de obras originales: catálogo, carrito y compra (con una factura por artista),
encargos a medida con precio acordado, panel del artista y administración.
TPO de Aplicaciones Interactivas (UADE), grupo 11. El frontend está en
[Gallery-Frontend](https://github.com/Pili-v/Gallery-Frontend).

**Stack:** Java 17, Spring Boot (Web, Security con JWT, Data JPA), MySQL y Cloudinary para las fotos.

## Cómo correrlo

Necesita Java 17 o superior, MySQL 8 y una cuenta de Cloudinary.

1. Crear una base vacía: `CREATE DATABASE gallery;`
2. Copiar `src/main/resources/application.properties.example` a `application.properties` (no se versiona) y completar
   la conexión a MySQL, la clave del JWT y `cloudinary.url`.
3. Levantar la API en `http://localhost:8080`:

```bash
./mvnw spring-boot:run      # en Windows: .\mvnw.cmd spring-boot:run
```

Las tablas se crean solas al arrancar. Para ver datos de ejemplo se importa el export de la demo (`gallery-demo.sql`,
se entrega aparte): `mysql -u <usuario> -p --default-character-set=utf8mb4 -e "source gallery-demo.sql"`. Las fotos de
la demo viven en la cuenta de Cloudinary del equipo.

## Cómo está organizado

`controllers` (HTTP y validación de entrada) → `services` (reglas de negocio) → `repositories` (JPA). Además: `entities`,
`exceptions` (con un manejador global que responde 400, 403, 404 y 409 con un mensaje claro), `security` (JWT y control
de quién es dueño de cada recurso) y `config`.

## Reglas principales

- **Roles:** `CLIENTE`, `ARTISTA`, `ARTISTA_CLIENTE` y `ADMIN`. La identidad sale siempre del token, no del cuerpo del pedido.
- **Compra:** el checkout es una transacción que bloquea el carrito, valida disponibilidad y stock de todo antes de
  tocar nada, congela los precios en la factura y descuenta el stock.
- **Precios:** un descuento rige solo con fecha de fin vigente. El carrito y la compra calculan el mismo precio.
- **Encargos:** el artista propone el precio, el cliente lo acepta (viendo el monto) y recién al pagar se emite la factura.
- **Obras:** se dan de baja de forma lógica. Cada obra viaja con sus variantes e imágenes; la galería se guarda completa o no se guarda.
- **Listas:** paginadas (máximo 100 por página); una lista vacía responde 204.

## Crear un perfil de artista

`POST /api/usuarios/me/perfil-artista` con `Authorization: Bearer <token>` y el cuerpo
`{ "nombre_artistico": "…", "acepta_encargos": true }`. El cliente pasa a `ARTISTA_CLIENTE`.
