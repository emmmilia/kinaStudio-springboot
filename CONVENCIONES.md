# Convenciones del proyecto

Estas reglas valen para todo el equipo. Se definieron en el Sprint 2 y se actualizan con acuerdo de las dos integrantes.

## 1. Nombres en el código

| Elemento | Formato | Ejemplo |
|---|---|---|
| Clases (Java) | PascalCase | `PagoService`, `TurnoController` |
| Métodos y variables | camelCase | `calcularSaldoRestante` |
| Constantes | MAYÚSCULAS_CON_GUIONES | `PLAZO_VENCIMIENTO_HORAS` |
| Paquetes (Java) | minúsculas, uno por módulo | `usuarios`, `agenda`, `pagos`, `stock` |
| Componentes React | PascalCase | `AgendaSemanal.jsx` |
| Archivos y carpetas del front | camelCase o kebab-case, uno solo | `agendaSemanal.js` |
| Tablas y columnas de la base | snake_case | `turno`, `clienta_id` |

Idioma: los términos del dominio van en español (`Turno`, `Clienta`, `Servicio`, `Comision`) y los términos técnicos en inglés (`Service`, `Controller`, `Repository`). Se respeta en todo el código.

## 2. Comentarios y documentación

- Se documenta con Javadoc la API pública de los servicios y los endpoints REST.
- Los comentarios en línea explican el porqué de una decisión, no qué hace la línea. Se usan sobre todo en las reglas de negocio (saldo restante, saldo a favor, comisiones, sueldos).
- No se deja código comentado ni `TODO` sin tarea asociada en Jira.

## 3. Formato automático

- Front-end: ESLint y Prettier.
- Back-end: un formateador para Java (Spotless o Checkstyle), a definir al crear el proyecto.
- El formato se revisa antes de abrir un Pull Request, para no discutir estilo en la revisión.

## 4. Ramas

Se trabaja con una rama de vida corta por funcionalidad, que sale de `main`.

| Tipo | Formato | Ejemplo |
|---|---|---|
| Funcionalidad | `feature/rfXX-descripcion-corta` | `feature/rf14-agendar-turno` |
| Corrección | `fix/descripcion-corta` | `fix/calculo-saldo-restante` |
| Documentación | `docs/descripcion-corta` | `docs/readme-inicial` |

Reglas:

- `main` siempre está estable. No se hace push directo.
- Una rama cubre uno o pocos requerimientos relacionados, no un sprint entero.
- Se actualiza con `main` antes de abrir el Pull Request, para resolver conflictos en la rama y no en `main`.
- Después del merge, la rama se elimina.
- No se usan ramas permanentes por integrante ni ramas `develop`, `release` o `hotfix`, por el tamaño del equipo.

## 5. Commits

Formato: `RF.XX: descripción en presente`

Ejemplos:

- `RF.14: valida solapamiento de turnos por esteticista`
- `RF.2: agrega bloqueo de cuenta tras 5 intentos fallidos`
- `Docs: completa README con estructura del repositorio`

Reglas:

- Un commit, un cambio con sentido. No se mezclan temas distintos.
- Si el cambio no corresponde a un requerimiento, se usa `Docs:`, `Fix:`, `Config:` o `Test:`.
- Si hay una tarea de Jira, se menciona en el cuerpo del commit.

## 6. Pull Requests

1. Se abre hacia `main` con un título que sigue el formato de los commits.
2. La descripción indica qué requerimiento cubre, qué se cambió y cómo se probó.
3. La otra integrante lo revisa y lo aprueba antes del merge. Nadie aprueba su propio Pull Request.
4. Antes de pedir revisión se verifica que:
   - el código compila,
   - pasan las pruebas,
   - el formato está aplicado,
   - no hay credenciales ni datos reales en el cambio.
5. Se mergea con *squash* para que `main` quede con un commit por funcionalidad.

## 7. Pruebas

- Las reglas de negocio llevan pruebas unitarias: saldo restante, saldo a favor, comisiones, sueldos y estados del turno.
- Toda corrección de un defecto incluye una prueba que lo reproduce.

## 8. Credenciales y datos sensibles

- Las credenciales (Mercado Pago, Azure, base de datos, servicio de mail) no se suben nunca al repositorio.
- Se guardan en variables de entorno y, en GitHub, en *Secrets*.
- Los archivos `.env` están en el `.gitignore`.
- Para pruebas se usan datos ficticios, nunca datos reales de clientas.

## 9. Estructura del repositorio

```
backend/     API REST en Spring Boot, un paquete por módulo
frontend/    Aplicación web en React
docs/        Documentación
```

## 10. Jira

- Cada tarea del backlog corresponde a uno o más requerimientos (`RF.XX`).
- Las horas se registran en Jira al terminar cada tarea.
- Los defectos se registran como incidencia de tipo *Bug*, vinculada al requerimiento afectado.
