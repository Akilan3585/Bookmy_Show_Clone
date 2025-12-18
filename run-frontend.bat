@echo off
echo ============================================
echo BookMyShow Frontend - Starting Web Server
echo ============================================
echo.

cd frontend

echo Starting HTTP server...
echo Frontend will be available at: http://localhost:8000
echo.
echo Make sure backend is running at: http://localhost:8080
echo.
echo Press Ctrl+C to stop the server
echo.

python -m http.server 8000
