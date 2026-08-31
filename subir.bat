@echo off
echo Guardando y subiendo cambios a GitHub...
git add .
git commit -m "Actualizacion automatica"
git push
echo.
echo ---------------------------------------
echo Proceso finalizado.
echo ---------------------------------------
pause