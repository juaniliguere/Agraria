@echo off
echo 1. Bajando cambios de GitHub...
git pull --rebase origin main

echo.
echo 2. Guardando y subiendo tus cambios...
git add .
git commit -m "Actualizacion automatica"
git push origin main

echo.
echo ---------------------------------------
echo ¡Proceso finalizado con exito!
echo ---------------------------------------
pause