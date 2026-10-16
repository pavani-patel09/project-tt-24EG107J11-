@echo off
title MediKiosk AI Launcher (Java Spring Boot + React + MySQL)
echo ========================================================
echo       Launching MediKiosk AI Platform (Java Full-Stack)
echo ========================================================
echo.

echo [1/3] Starting Spring Boot Backend (Java on http://127.0.0.1:8080)...
start "MediKiosk AI - Spring Boot Backend" cmd /k "cd /d "%~dp0backend" && mvnw.cmd spring-boot:run"

echo [2/3] Starting React Frontend (Vite on http://localhost:5173)...
start "MediKiosk AI - React Frontend" cmd /k "cd /d "%~dp0frontend" && npm.cmd run dev"

echo [3/3] Waiting for servers to initialize...
timeout /t 5 /nobreak >nul

echo Opening MediKiosk AI in default web browser...
start http://localhost:5173

echo.
echo ========================================================
echo MediKiosk AI is now running!
echo - Web App:        http://localhost:5173
echo - Spring Boot API: http://127.0.0.1:8080/api
echo - Database:       MySQL / Aiven Cloud (configured via .env)
echo ========================================================
echo.
echo Leave this window or the minimized server windows open while using the app.
pause
