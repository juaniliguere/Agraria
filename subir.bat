@echo off
echo 1. Guardando tus cambios locales...
git add .
git commit -m "Actualizacion automatica"

echo.
echo 2. Trayendo cambios nuevos de GitHub...
git pull --rebase origin main

echo.
echo 3. Subiendo todo a GitHub...
git push origin main

echo.
echo ---------------------------------------
echo ¡Listo! Codigo sincronizado y subido.
echo ---------------------------------------
pause