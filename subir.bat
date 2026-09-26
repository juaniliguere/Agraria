@echo off
echo Guardando cambios locales...
git add .
git commit -m "Actualizacion automatica"

echo Subiendo a GitHub (forzado)...
git push origin main --force
pause