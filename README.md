# backend-castor-youtube-search
Technical test project for Castor – A fullstack web application built with Spring Boot, Angular, and MySQL. It includes user authentication, integration with the YouTube API, and demonstrates the use of AI-assisted development tools like GitHub Copilot.

---

## Versiones de Tecnologías Utilizadas

- **Java:** 21
- **Spring Boot:** 3.5.3
- **MySQL:** 8.0.33

---

## Esquema de la Base de Datos

La aplicación utiliza una base de datos MySQL con las siguientes entidades principales:

### Entidad `User`
| Campo      | Tipo     | Restricciones                |
|------------|----------|------------------------------|
| id         | Long     | PK, auto-increment           |
| username   | String   | Único, no nulo               |
| email      | String   | Único, no nulo, formato email|
| password   | String   | No nulo (encriptado BCrypt)  |
| roles      | Set<String> | EAGER, tabla secundaria `user_roles` |

- La tabla `user_roles` almacena los roles asociados a cada usuario.
- El esquema se genera automáticamente con Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

### Entidad `SearchHistory`
| Campo      | Tipo           | Restricciones                |
|------------|----------------|------------------------------|
| id         | Long           | PK, auto-increment           |
| user_id    | Long (FK)      | No nulo, referencia a User   |
| query      | String         | No nulo                      |
| videoId    | String         | No nulo                      |
| searchedAt | LocalDateTime  | No nulo, fecha de la búsqueda|

- Permite almacenar el historial de búsquedas de YouTube por usuario.

---

## Lógica de Autenticación

La autenticación se basa en JWT (JSON Web Token) y sigue el siguiente flujo:

1. **Registro de usuario**
   - Endpoint: `POST /api/auth/register`
   - Body esperado:
     ```json
     {
       "username": "usuario",
       "email": "usuario@email.com",
       "password": "secreto"
     }
     ```
   - El password se almacena encriptado con BCrypt.
   - Por defecto, el usuario recibe el rol `ROLE_USER`.

2. **Login de usuario**
   - Endpoint: `POST /api/auth/login`
   - Body esperado:
     ```json
     {
       "email": "usuario@email.com",
       "password": "secreto"
     }
     ```
   - Si las credenciales son válidas, se genera y retorna un JWT:
     ```json
     {
       "token": "<jwt>",
       "username": "usuario"
     }
     ```

3. **Protección de endpoints**
   - Todos los endpoints bajo `/api/youtube/**` requieren autenticación JWT.
   - El token debe enviarse en el header:
     ```
     Authorization: Bearer <jwt>
     ```
   - Si el token es inválido o expirado, se retorna HTTP 401.

4. **Validación del token**
   - El filtro `JwtAuthenticationFilter` intercepta las peticiones, valida el JWT y autentica al usuario en el contexto de Spring Security.
   - Si el token es válido, el usuario puede acceder a los recursos protegidos.

---

## Configuración de la Base de Datos (application.properties)

```
spring.datasource.url=jdbc:mysql://localhost:3308/castor
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
```

---

## Resumen de Endpoints de Autenticación

- `POST /api/auth/register` — Registro de usuario
- `POST /api/auth/login` — Login y obtención de JWT
- `GET /api/youtube/search` — Buscar videos (requiere JWT)
- `GET /api/youtube/play/{videoId}` — Reproducir video (requiere JWT)
- `GET /api/youtube/channel/{channelId}` — Detalles de canal (requiere JWT)
- `GET /api/youtube/history` — Historial de búsquedas del usuario (requiere JWT)

---

Para más detalles, consulta el código fuente de los paquetes `entity`, `service`, `controller` y `security`.
