# Quick Start Guide

## Prerequisites
✅ Java 21 installed
✅ Maven installed
✅ Python installed (for frontend server)

## Start Backend (in one terminal)

```bash
cd d:\bookmyshow\backend
mvn clean install
mvn spring-boot:run
```

Or simply double-click: `run-backend.bat`

**Backend URL**: http://localhost:8080

## Start Frontend (in another terminal)

```bash
cd d:\bookmyshow\frontend
python -m http.server 8000
```

Or simply double-click: `run-frontend.bat`

**Frontend URL**: http://localhost:8000

## Demo Credentials

### Regular User
- Username: `john`
- Password: `john123`

### Admin User
- Username: `admin`
- Password: `admin123`

## Test Flow

1. **Open** http://localhost:8000
2. **Browse** movies on home page
3. **Click** any movie to see details
4. **Select** show date and time
5. **Login** (use demo credentials)
6. **Select** seats on seat map
7. **Proceed** to payment
8. **Pay** (simulated)
9. **View** confirmation and booking reference

## Admin Features

1. **Login** as admin
2. **Navigate** to http://localhost:8000/admin.html
3. **Add Movies**: Add new movies
4. **Add Theaters**: Add theaters in different cities
5. **Add Shows**: Create shows (movie + theater + time)
6. **View Bookings**: See all user bookings

## Architecture Highlights

✅ **JWT Authentication** - Secure token-based auth
✅ **Spring Security** - Role-based access control
✅ **Spring Cache** - Movies, theaters, shows, seats caching
✅ **Pessimistic Locking** - Prevents double booking
✅ **REST API** - Clean RESTful endpoints
✅ **Responsive UI** - BookMyShow-style design
✅ **Sample Data** - Pre-loaded movies, theaters, shows

## Caching Explained

### Movies Cache
- **Cached**: Movie list, movie details
- **Evicted**: When admin adds/updates/deletes movies
- **Benefit**: Fast browsing without DB hits

### Seats Cache
- **Cached**: Seat availability per show
- **Evicted**: After every booking
- **Benefit**: Real-time seat map rendering
- **Critical**: Must evict to prevent showing wrong availability

## Concurrency Safety

**Problem**: Two users booking same seat simultaneously

**Solution**: Pessimistic locking
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
```

**Result**: Database locks selected seats during transaction, preventing double booking

## Database Access

**H2 Console**: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:bookmyshow`
- Username: `sa`
- Password: (empty)

## Troubleshooting

**Port 8080 in use?**
Edit `backend/src/main/resources/application.properties`:
```properties
server.port=8081
```

**CORS errors?**
Use local server (Python/Live Server), not file:// protocol

**Build fails?**
- Check Java version: `java -version` (should be 21+)
- Check Maven: `mvn -version`
- Clean Maven cache: `mvn clean`

## API Testing

### Login API
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"john123"}'
```

### Get Movies (with token)
```bash
curl http://localhost:8080/api/movies \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

**Enjoy your BookMyShow clone! 🎬🍿**
