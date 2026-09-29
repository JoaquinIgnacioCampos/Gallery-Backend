# Gallery Backend

## Crear el perfil de artista en Insomnia

Enviar `POST http://localhost:8080/api/usuarios/me/perfil-artista` con
`Authorization: Bearer <access_token>` y body JSON:

```json
{
  "nombre_artistico": "Mi nombre artistico",
  "acepta_encargos": true
}
```

La identidad sale del JWT: no se envia un ID de usuario. Un cliente crea su
propio perfil y pasa a `ARTISTA_CLIENTE` (201). Un administrador recibe 403
con el mensaje `ya tiene permisos para hacer todo`, sin crear perfil ni cambiar
su rol. Un perfil duplicado devuelve 409 y los datos invalidos, 400.
El POST anterior `/api/usuarios/{usuario_id}/perfil-artista` fue reemplazado;
el GET de consulta sigue disponible.

Pruebas del flujo con JWT y base H2 en memoria:

```powershell
.\mvnw.cmd -Dtest=PerfilArtistaCreationTests test
```
