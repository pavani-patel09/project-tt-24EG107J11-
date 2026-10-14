@echo off
title MediKiosk AI Launcher (Java Spring Boot with H2 In-Memory + React)
echo ========================================================
echo   Launching MediKiosk AI Platform (Java + H2 In-Memory)
echo ========================================================
echo.
echo Note: This launcher runs with H2 in-memory DB (no MySQL required).
echo.

echo [1/3] Starting Spring Boot Backend with H2 profile (http://127.0.0.1:8080)...
start "MediKiosk AI - Spring Boot (H2)" cmd /k "cd /d "%~dp0backend" && mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=h2"

echo [2/3] Starting React Frontend (Vite on http://localhost:5173)...
start "MediKiosk AI - React Frontend" cmd /k "cd /d "%~dp0frontend" && npm.cmd run dev"

echo [3/3] Waiting for servers to initialize...
timeout /t 5 /nobreak >nul

echo Opening MediKiosk AI in default web browser...
start http://localhost:5173

echo.
echo ========================================================
echo MediKiosk AI is now running with H2 In-Memory Database!
echo - Web App:        http://localhost:5173
echo - Spring Boot API: http://127.0.0.1:8080/api
echo - H2 Web Console: http://127.0.0.1:8080/h2-console
echo ========================================================
echo.
echo Leave this window or the minimized server windows open while using the app.
pause
