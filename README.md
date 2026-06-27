# BFHL REST API — Spring Boot

## Project Structure
```
src/
├── main/java/com/bfhl/api/
│   ├── BfhlApiApplication.java        ← Entry point
│   ├── controller/BfhlController.java ← REST layer
│   ├── service/
│   │   ├── BfhlService.java           ← Interface
│   │   └── BfhlServiceImpl.java       ← Business logic
│   ├── dto/
│   │   ├── BfhlRequest.java           ← Request DTO
│   │   ├── BfhlResponse.java          ← Response DTO
│   │   └── ErrorResponse.java         ← Error DTO
│   └── exception/
│       └── GlobalExceptionHandler.java
└── test/java/com/bfhl/api/
    └── BfhlTests.java                 ← Unit + Controller tests
```

## Step 1 — Update Your Personal Details
Open `src/main/java/com/bfhl/api/service/BfhlServiceImpl.java` and change:
```java
private static final String FULL_NAME   = "your_name";     // e.g. "rahul_sharma"
private static final String DOB         = "ddmmyyyy";       // e.g. "15081998"
private static final String EMAIL       = "your@email.com";
private static final String ROLL_NUMBER = "YOUR_ROLL_NO";
```

## Step 2 — Build & Test Locally
```bash
# Build
mvn clean package

# Run
java -jar target/bfhl-api-1.0.0.jar

# Test with curl
curl -X POST http://localhost:8080/bfhl \
  -H "Content-Type: application/json" \
  -d '{"data": ["a","1","334","4","R","$"]}'

# Run all tests
mvn test
```

## Step 3 — Deploy to Render (Free)
1. Push your code to a GitHub repo
2. Go to https://render.com → New → Web Service
3. Connect your GitHub repo
4. Set:
   - **Environment**: Java
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/bfhl-api-1.0.0.jar`
5. Click **Deploy**
6. Your URL will be: `https://your-app-name.onrender.com/bfhl`

## Step 4 — Deploy to Railway (Alternative)
1. Push to GitHub
2. Go to https://railway.app → New Project → Deploy from GitHub
3. Railway auto-detects Java + Maven
4. Your URL will be: `https://your-app.up.railway.app/bfhl`

## API Reference

### POST /bfhl
**Request:**
```json
{ "data": ["a", "1", "334", "4", "R", "$"] }
```

**Response (200 OK):**
```json
{
  "is_success": true,
  "user_id": "john_doe_17091999",
  "email": "john@xyz.com",
  "roll_number": "ABCD123",
  "odd_numbers": ["1"],
  "even_numbers": ["334", "4"],
  "alphabets": ["A", "R"],
  "special_characters": ["$"],
  "sum": "339",
  "concat_string": "Ra"
}
```

**Error Response (400):**
```json
{ "is_success": false, "message": "data array cannot be null" }
```
