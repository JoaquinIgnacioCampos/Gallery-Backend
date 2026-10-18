# Demo: los mismos datos de ejemplo para todo el equipo

Un programa Java que deja **tu base local** con los datos de la demo (usuarios, obras con fotos, ventas, encargos…), así
todos trabajamos y probamos sobre los mismos ejemplos. Está pensado para mostrar **todas las funcionalidades de la
aplicación**: el paginado, los estados vacíos y los errores.

Es **incremental**: compara la base con la semilla y corrige solo lo que difiere. Si ya está como la demo, no toca nada
(unos segundos). Reparar lo que se ensució tarda alrededor de 20 segundos, y armar todo desde cero, menos de un minuto.

## Qué hace falta

- El back corriendo (desde IntelliJ). Tiene que haber arrancado al menos una vez, para que existan las tablas, y tener
  cargado su `application.properties` con MySQL y `cloudinary.url`.
- El JDK del proyecto. **No hace falta instalar nada más** (es todo Java).
- Internet: las fotos se suben a Cloudinary.

La conexión a MySQL sale del `application.properties` del back: **no hay usuarios ni contraseñas en el código**.

## Cómo correrlo

**Desde IntelliJ (lo más fácil).** Con el back levantado, en el selector de configuraciones de arriba a la derecha
aparecen tres, y se corren con el triángulo verde:

| Configuración | Qué hace |
|---|---|
| `Reset demo` | deja la base como la demo |
| `Reset demo (solo ver)` | muestra qué cambiaría, sin tocar nada |
| `Reset demo (completo)` | vacía la base y la arma de cero |

Si no aparecen, abrí `src/test/java/com/uade/tpo/grupo11/gallery/demo/ResetDemo.java` y tocá el triángulo verde que está al
lado de `main`. Para pasarle opciones: *Edit Configurations → Program arguments*. Antes de borrar algo pregunta
`¿Continuar? [s/N]`: se responde escribiendo `s` en la consola de IntelliJ. Para que no pregunte, agregá `--si`.

| Opción | Efecto |
|---|---|
| `--ver` | solo muestra qué cambiaría |
| `--completo` | vacía todas las tablas y arma todo de cero |
| `--si` | no pide confirmación |
| `-v` | lista también lo que ya estaba bien |

**Por terminal** (alternativa; desde la carpeta del back, con Git Bash, Mac o Linux; en PowerShell hay que entrecomillar
cada `-D...` y usar `.\mvnw.cmd`):

```bash
./mvnw -q test-compile org.codehaus.mojo:exec-maven-plugin:3.5.0:java -Dexec.mainClass=com.uade.tpo.grupo11.gallery.demo.ResetDemo -Dexec.classpathScope=test -Dexec.args="--ver"
```

Después de probar cosas (comprar, borrar, crear obras…), corré el reset de nuevo y la base vuelve a la demo.

> **Ojo:** borra de la base que figura en tu `application.properties` todo lo que **no** es de la demo (tus obras de
> prueba, usuarios que registraste…). Antes de cambiar nada muestra sobre qué base va a trabajar y qué va a sacar, y
> pide confirmación. Con `--completo` vacía todas las tablas. Si tenés datos propios que querés conservar, usá otra base.

## Cuentas

| Rol | Cuentas (`@gallery.com`) | Clave |
|---|---|---|
| Admin | `admin` | `Admin123!` |
| Artistas (también compran) | `artista1` Ana Bosch · `artista2` Bruno Vega · `artista3` Carla Mendez · `artista4` Diego Rivas · `artista5` Elena Ruiz | `Artista123!` |
| Clientes | `cliente1` a `cliente8` | `Cliente123!` |

## Qué hay para mostrar

**Paginado**

| Pantalla | Cómo se ve |
|---|---|
| Catálogo (3 por página) | 15 obras publicadas → 5 páginas |
| Artistas (3 por página) | 5 artistas → 2 páginas |
| Perfil de Ana (6 por página) | 11 obras → 2 páginas |
| Mis obras de `artista1` (10 por página) | 11 obras → 2 páginas |
| Mis ventas de `artista1` (10 por página) | 12 ventas → 2 páginas |
| Usuarios, como `admin` (10 por página) | 14 usuarios → 2 páginas |

**Estados vacíos:** `artista5` (Elena) no tiene obras ni ventas; `cliente7` no tiene compras ni carrito y todavía no
publica (es el que ve el botón **¿Querés publicar?**); `cliente1` tiene el carrito vacío.

**Errores que se pueden provocar** (con los datos ya cargados)

| Qué hacer | Qué pasa |
|---|---|
| `cliente3`: ir a comprar su carrito | rechaza la obra despublicada y la eliminada que tiene adentro |
| `cliente5` y `cliente6` tienen la última unidad del Caballo pardo A3: que compre uno y después el otro | el segundo recibe "stock insuficiente" |
| Abrir el Caballo pardo en tamaño A4 | tiene stock 0: figura "Sin stock" y el botón de compra está deshabilitado |
| `artista1`: agregar al carrito una obra propia | "es de tu propia obra" |
| `cliente1`: hacer un encargo a Bruno | Bruno no acepta encargos |
| `artista1`: hacerse un encargo a sí misma | no se permite |
| `cliente4`: elegir envío a domicilio | no tiene dirección cargada: tiene que escribirla |
| `admin`: borrar el estilo Abstracto, el tamaño A4 o un marco usado | "está siendo utilizado" |
| `admin`: borrar Minimalismo, Mural o Marco Plata | se pueden borrar (no los usa nada); el próximo reset los vuelve a crear |
| `cliente3`: mover su encargo terminado a otro estado; `cliente6`: escribir en el encargo cancelado | rechazados |
| Un cliente entra a Mis obras o a Usuarios | pantalla de "sin permiso" |

**Obras con situaciones distintas**

- **Ojo de girasol** (Ana): tres fotos para la galería, tres tamaños y un descuento vigente.
- **Mirada** (Bruno): descuento **vencido**. **Estudio de la mirada** está despublicada (solo la ve su artista).
  **Boceto descartado** está eliminada: no aparece en ningún listado, pero sigue en el historial de quien la compró.
- **Retrato en proceso** (Carla) no tiene variantes y **Borrador sin fotos** no tiene fotos: ninguna aparece en el catálogo.
- **Caballo pardo** (Diego): un tamaño sin stock y el otro con una sola unidad.

**Encargos** (uno en cada estado): pendiente sin precio, pendiente con precio propuesto (el cliente puede aceptarlo),
aprobado, en proceso, terminado (el cliente puede pagarlo), pagado y cancelado. Los terminados y pagados emiten factura
al artista.

**Ventas y compras:** 13 compras del historial, con envío a domicilio y retiro, de uno o de varios artistas (una factura
por artista). `cliente2` tiene el carrito listo para hacer el checkout en vivo.

## Las fotos y Cloudinary

Las 7 pinturas están en `demo/imagenes/obras/` (`obra-1.jpg` a `obra-7.jpg`) y el resto son recortes de detalle de esas
mismas pinturas (los "estudios" y las segundas fotos de cada obra). Los marcos son PNG con el centro transparente.
Cada archivo se sube **una sola vez** y lo usa una sola obra, así que no hay imágenes repetidas.

El programa **nunca borra fotos ni marcos con SQL**. Cuando hay que sacar o cambiar uno le pide al back (`DELETE` o
`PUT`), que lo destruye en Cloudinary. Por eso no se acumulan archivos huérfanos ni duplicados: al volver a correrlo, lo
que sobra se borra de Cloudinary y lo que falta se sube. Si en algún caso no pudo (por ejemplo, el admin de la demo ya no
tiene su clave), lo avisa y deja los `public_id` en `demo/.cache/huerfanas-cloudinary.txt` para borrarlos a mano.

Cada base tiene sus propias copias de las fotos: si dos personas usan la misma cuenta de Cloudinary, cada una sube las suyas.

## Cambiar la demo

Todo el código está en `src/test/java/com/uade/tpo/grupo11/gallery/demo/` (en la carpeta de tests para que no viaje dentro
de la aplicación; no son tests y no se corren con `mvn test`).

- **Datos** (usuarios, obras, precios, stock, compras, carritos, encargos): todo está en `Semilla.java`, solo datos y
  comentarios. Agregar una obra es agregar una entrada. Después, `Reset demo (solo ver)` muestra qué cambia.
- **Fotos:** poné el archivo en `demo/imagenes/obras/` (o `demo/imagenes/marcos/`) y nombralo en la semilla. Si cambia el
  contenido de uno que ya está, el programa lo detecta y lo reemplaza. `GenerarImagenes.java` (con su triángulo verde)
  regenera los recortes y los marcos a partir de las pinturas originales.
- Los descuentos se definen como "vigente" o "vencido" respecto de hoy: si alguno caducó con el tiempo, el próximo reset lo renueva.

## Si algo falla

| Síntoma | Qué mirar |
|---|---|
| "No responde http://localhost:8080" | Falta levantar el back. Si usa otro puerto, definí `server.port` en el `application.properties` o la variable `BASE=http://localhost:PUERTO`. |
| "No encuentro la carpeta del back" | La carpeta de trabajo de la configuración tiene que ser la del proyecto (donde está `pom.xml`), o definí `GALLERY_BACKEND_DIR`. |
| "No se pudo conectar a la base" | Revisá `spring.datasource.*` en el `application.properties` (o las variables `GALLERY_DB_URL`, `GALLERY_DB_USER`, `GALLERY_DB_PASS`). |
| Cortó a la mitad | Volvé a correrlo: solo corrige lo que falte. |
| Querés probarlo sin tocar tu base | `GALLERY_DB=otra_base` apunta el programa a otra base (en Edit Configurations → Environment variables), junto con un back arrancado contra esa misma base. |

`demo/.cache/` (registro de las fotos ya subidas y la lista de huérfanas) es local y no se versiona.
