# API de Kina Studio

Referencia de los endpoints del back-end (Spring Boot). Todos están bajo `/acceso`.

- **URL base en desarrollo:** `http://localhost:8080`
- **Formato de entrada:** los `POST` reciben los datos como formulario (`application/x-www-form-urlencoded`), no como JSON.
- **Formato de salida:** los endpoints que devuelven datos responden JSON. Los errores de negocio responden **texto plano** con el mensaje.

## Autenticación y seguridad

### Sesión
El login crea una sesión y el back devuelve una cookie (`JSESSIONID`). El cliente debe enviarla en cada pedido (en el front: `credentials: 'include'`). Sin sesión, los endpoints protegidos responden `401`.

### Token CSRF
Todos los `POST` exigen un token CSRF, también el login, el logout y los endpoints públicos:

1. Pedir `GET /acceso/csrf`. Devuelve el nombre del header y el token.
2. Enviar el token en ese header (`X-CSRF-TOKEN`) en el `POST`.

El token **cambia después de iniciar o cerrar sesión**, así que conviene pedirlo de nuevo antes de cada `POST`. Si falta o es inválido, la respuesta es `403`.

### CORS
El back acepta pedidos del front en `http://localhost:5173` por defecto. Para otro origen se define la variable de entorno `KINA_CORS_ORIGENES`.

### Roles
| Rol | Valor |
|---|---|
| Administrador | `ADMINISTRADOR` |
| Esteticista | `ESTETICISTA` |

## Formato de las respuestas con datos (`Commands`)

Los endpoints que devuelven un resultado envuelven la información en una lista de comandos:

```json
{
  "commands": [
    { "id": "idMensaje", "parametro": "Texto del mensaje" }
  ]
}
```

- `id` identifica qué tipo de comando es (por ejemplo `idMensaje` o `nuevoUsuario`).
- `parametro` es el contenido: un texto o un objeto, según el comando.

## Errores comunes

| Código | Cuándo | Cuerpo |
|---|---|---|
| `400` | Error de negocio (dato inválido, duplicado, etc.) | Texto plano con el mensaje, por ejemplo `La contraseña actual es incorrecta.` |
| `401` | No hay sesión iniciada (o el login es incorrecto) | Vacío, o `Usuario o contraseña incorrectos.` en el login |
| `403` | Falta el token CSRF o el rol no tiene permiso | — |

## Resumen

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| GET | `/acceso/csrf` | Público | Devuelve el token CSRF |
| POST | `/acceso/login` | Público | Inicia sesión |
| POST | `/acceso/logout` | Con sesión | Cierra sesión |
| GET | `/acceso/yo` | Con sesión | Usuario con sesión iniciada |
| GET | `/acceso/perfil` | Con sesión | Datos del usuario con sesión |
| POST | `/acceso/perfil` | Con sesión | Modifica los datos del usuario con sesión |
| POST | `/acceso/cambiarPassword` | Con sesión | Cambia la contraseña del usuario con sesión |
| POST | `/acceso/registroEsteticista` | Administrador | Da de alta una esteticista |
| POST | `/acceso/recuperarPassword` | Público | Envía el enlace para recuperar la contraseña |
| POST | `/acceso/restablecerPassword` | Público | Elige una contraseña nueva con el token del enlace |

---

## GET `/acceso/csrf`

Devuelve el token CSRF. **Público.**

**Respuesta `200`**
```json
{ "headerName": "X-CSRF-TOKEN", "token": "e3b0c442-98fc-..." }
```

---

## POST `/acceso/login`

Inicia sesión. **Público.**

| Parámetro | Tipo | Obligatorio | Descripción |
|---|---|---|---|
| `nombreUsuario` | texto | Sí | No distingue mayúsculas |
| `password` | texto | Sí | Distingue mayúsculas |

**Respuesta `200`:** sin cuerpo. Devuelve la cookie de sesión.

**Errores**
- `401` — `Usuario o contraseña incorrectos.` (es el mismo mensaje si el usuario no existe o la contraseña es incorrecta).
- `403` — falta el token CSRF.

---

## POST `/acceso/logout`

Cierra la sesión actual.

**Respuesta `200`:** sin cuerpo.

---

## GET `/acceso/yo`

Devuelve el usuario con sesión iniciada. Se usa al abrir o recargar el front.

**Respuesta `200`**
```json
{ "nombreUsuario": "ana", "rol": "ESTETICISTA" }
```

**Errores:** `401` si no hay sesión.

---

## GET `/acceso/perfil`

Devuelve los datos del usuario con sesión iniciada. La respuesta **no** va envuelta en `commands`.

**Respuesta `200`**
```json
{
  "id": 10002,
  "email": "ana@mail.com",
  "nombreUsuario": "ana",
  "nombreCompleto": "Ana Pérez",
  "rol": "ESTETICISTA"
}
```
Nunca incluye la contraseña ni su hash.

**Errores:** `401` sin sesión · `400` `Usuario no encontrado.`

---

## POST `/acceso/perfil`

Modifica los datos del usuario con sesión iniciada. El usuario se toma de la sesión, nunca de los parámetros.

| Parámetro | Tipo | Obligatorio | Descripción |
|---|---|---|---|
| `email` | texto | Sí | Máximo 254 caracteres. No puede estar usado por otro usuario |
| `nombreCompleto` | texto | Sí | Máximo 100 caracteres |
| `comentario` | texto | No | |

**Respuesta `200`**
```json
{
  "commands": [
    {
      "id": "usuario modificado.",
      "parametro": {
        "email": "ana@mail.com",
        "nombreUsuario": "ana",
        "nombreCompleto": "Ana Pérez",
        "comentario": "",
        "rol": "ESTETICISTA"
      }
    }
  ]
}
```

**Errores `400`**
- `Usuario no encontrado.`
- `El mail no es válido.`
- `El nombre es obligatorio`
- `Algún dato supera el largo permitido.`
- `Ese mail ya está registrado.`

---

## POST `/acceso/cambiarPassword`

Cambia la contraseña del usuario con sesión iniciada. Pueden usarlo los dos roles.

| Parámetro | Tipo | Obligatorio | Descripción |
|---|---|---|---|
| `passwordActual` | texto | Sí | Contraseña vigente |
| `passwordNueva` | texto | Sí | Máximo 72 bytes. Distinta de la actual |

**Respuesta `200`**
```json
{ "commands": [ { "id": "idMensaje", "parametro": "Contraseña modificada con éxito." } ] }
```

**Errores `400`**
- `Debe completar la contraseña actual y la nueva.`
- `La contraseña es demasiado larga.`
- `Usuario no encontrado.`
- `La contraseña actual es incorrecta.`
- `La contraseña nueva debe ser distinta a la actual.`

---

## POST `/acceso/registroEsteticista`

Da de alta una esteticista. **Solo el administrador.**

| Parámetro | Tipo | Obligatorio | Descripción |
|---|---|---|---|
| `email` | texto | Sí | Máximo 254 caracteres. Único |
| `nombreUsuario` | texto | Sí | Máximo 50 caracteres. Único (sin distinguir mayúsculas) |
| `password` | texto | Sí | Máximo 72 bytes |
| `nombreCompleto` | texto | Sí | Máximo 100 caracteres |

**Respuesta `200`**
```json
{
  "commands": [
    {
      "id": "nuevoUsuario",
      "parametro": {
        "email": "bea@mail.com",
        "nombreUsuario": "bea",
        "nombreCompleto": "Bea Gómez",
        "comentario": "",
        "rol": "ESTETICISTA"
      }
    }
  ]
}
```

**Errores**
- `400` — `Usuario y contraseña son obligatorios.` · `El mail no es válido.` · `El nombre completo es obligatorio.` · `El mail no puede superar los 254 caracteres.` · `El nombre de usuario no puede superar los 50 caracteres.` · `El nombre completo no puede superar los 100 caracteres.` · `La contraseña es demasiado larga.` · `Ese nombre de usuario no está disponible.` · `Ese mail ya está registrado.`
- `401` — sin sesión.
- `403` — el usuario no es administrador.

---

## POST `/acceso/recuperarPassword`

Pide el enlace para recuperar la contraseña. **Público.**

| Parámetro | Tipo | Obligatorio | Descripción |
|---|---|---|---|
| `email` | texto | Sí | Mail de la cuenta. No distingue mayúsculas |

Si el mail está registrado, se envía un mail con un enlace `<URL del front>/?token=...` que vale **30 minutos** y se puede usar **una sola vez**. Pedir un enlace nuevo invalida el anterior.

La respuesta es **igual exista o no el mail**, para no revelar qué mails están registrados.

**Respuesta `200`**
```json
{ "commands": [ { "id": "idMensaje", "parametro": "Si el mail está registrado, te enviamos un enlace para restablecer tu contraseña." } ] }
```

**Errores:** `400` `Debe ingresar el mail.`

> Si el back no tiene configurado el SMTP (`KINA_MAIL_USER` sin definir), no envía mails: escribe el enlace en la consola del back.

---

## POST `/acceso/restablecerPassword`

Elige una contraseña nueva con el token que llegó en el enlace del mail. **Público.**

| Parámetro | Tipo | Obligatorio | Descripción |
|---|---|---|---|
| `token` | texto | Sí | El valor que está después de `?token=` en el enlace |
| `passwordNueva` | texto | Sí | Máximo 72 bytes |

**Respuesta `200`**
```json
{ "commands": [ { "id": "idMensaje", "parametro": "Contraseña restablecida con éxito." } ] }
```

**Errores `400`**
- `Debe completar la contraseña nueva.`
- `La contraseña es demasiado larga.` (el token no se gasta)
- `El enlace no es válido o expiró.` (token inexistente, vencido o ya usado)

---

## Variables de entorno del back

| Variable | Uso |
|---|---|
| `DB_USER`, `DB_PASSWORD` | Acceso a SQL Server |
| `KINA_ADMIN_EMAIL`, `KINA_ADMIN_PASSWORD` | Crean el administrador inicial al arrancar |
| `KINA_CORS_ORIGENES` | Orígenes del front permitidos (por defecto `http://localhost:5173`) |
| `KINA_MAIL_USER`, `KINA_MAIL_PASSWORD`, `KINA_MAIL_REMITENTE` | Envío de mails por SMTP |
| `KINA_FRONT_URL` | URL del front que se usa en el enlace del mail (por defecto `http://localhost:5173`) |
