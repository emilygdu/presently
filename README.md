# Presently Backend

REST API backend for Presently - a wish list web application that allows users to manage and share their wish lists with friends. Users can create wish lists, add items and share them with friends. Friends can mark items as bought without the wish list owner knowing who bought what.

## Tech Stack

- Java 21
- Spring Boot 3.3
- PostgreSQL
- JWT Authentication
- Maven

## Prerequisites

- Java 21
- Maven
- PostgreSQL

## Setup

### 1. Clone the repository

```bash
git clone https://github.com/emilygdu/presently.git
cd presently
```

### 2. Install and start PostgreSQL

```bash
sudo apt install postgresql postgresql-contrib -y
sudo service postgresql start
```

### 3. Create the database

```bash
sudo -u postgres psql
```

```sql
CREATE DATABASE presently;
CREATE USER presently_user WITH PASSWORD 'yourpassword';
GRANT ALL PRIVILEGES ON DATABASE presently TO presently_user;
GRANT ALL ON SCHEMA public TO presently_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO presently_user;
\q
```

### 4. Configure application.properties

Copy the example file and fill in your values:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Edit `application.properties` with your database credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/presently
spring.datasource.username=presently_user
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
server.port=8080
```

### 5. Run the application

```bash
mvn spring-boot:run
```

The backend runs on `http://localhost:8080`.

## API Documentation

After starting the application, the full interactive API documentation is available at:

```
http://localhost:8080/swagger-ui/index.html
```

## Authentication

All protected endpoints require a JWT token in the Authorization header:

```
Authorization: Bearer <token>
```

### Register

```
POST /auth/register
```

Request body:

```json
{
    "username": "name",
    "email": "email@example.com",
    "password": "yourpassword"
}
```

Response:

```json
{
    "token": "eyJhbGci..."
}
```

### Login

```
POST /auth/login
```

Request body:

```json
{
    "username": "name",
    "password": "yourpassword"
}
```

Response:

```json
{
    "token": "eyJhbGci..."
}
```

Use the token from the response in all subsequent requests as a Bearer token in the Authorization header.

## Endpoints

### User

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | /users/me | Get the currently logged in user | Yes |
| GET | /users/{id} | Get a user by ID | Yes |

### Wish List Items

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | /wishlist/items | Get all items of the logged in user | Yes |
| POST | /wishlist/items | Add a new item to the wish list | Yes |
| PUT | /wishlist/items/{id} | Update an existing item | Yes |
| DELETE | /wishlist/items/{id} | Delete an item | Yes |
| PUT | /wishlist/items/{id}/bought | Mark an item as bought | Yes |

#### Add Item — Request Body

```json
{
    "title": "Sony WH-1000XM5",
    "price": 299.99,
    "productUrl": "https://www.amazon.de/...",
    "imageUrl": "https://...",
    "productCategory": "TECHNOLOGY",
    "eventType": "BIRTHDAY",
    "isFavorite": true
}
```

#### Filter & Sort

The `GET /wishlist/items` endpoint supports optional query parameters:

| Parameter | Values | Description |
|-----------|--------|-------------|
| category | TECHNOLOGY, SPORT, FASHION, BEAUTY, HOME, FOOD, TRAVEL, OTHER | Filter by product category |
| eventType | BIRTHDAY, WEDDING, CHRISTMAS, BABY_SHOWER, GRADUATION, OTHER | Filter by event type |

Example:

```
GET /wishlist/items?category=TECHNOLOGY&eventType=BIRTHDAY
```

#### Important — Bought Items

When a **friend** marks an item as bought, the **owner of the wish list cannot see who bought it**. The owner only sees the item without any bought information. Friends can see `isBought: true` or `isBought: false`.

### Friends

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | /friends | Get the friend list of the logged in user | Yes |
| POST | /friends/request/{id} | Send a friend request to a user by ID | Yes |
| PUT | /friends/accept/{id} | Accept a friend request by friendship ID | Yes |
| DELETE | /friends/{id} | Remove a friend by friendship ID | Yes |

#### Friend Request Flow

1. User A sends a friend request to User B: `POST /friends/request/{userB_id}` with User A's token
2. User B accepts the request: `PUT /friends/accept/{friendship_id}` with User B's token
3. Both users can now see each other's wish lists

## Allowed Enum Values

### productCategory

`TECHNOLOGY` `SPORT` `FASHION` `BEAUTY` `HOME` `FOOD` `TRAVEL` `OTHER`

### eventType

`BIRTHDAY` `WEDDING` `CHRISTMAS` `BABY_SHOWER` `GRADUATION` `OTHER`