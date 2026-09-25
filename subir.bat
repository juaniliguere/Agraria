@echo off
echo Trayendo cambios de GitHub...
git pull origin main --rebase

echo Guardando cambios locales...
git add .
git commit -m "Actualización automatica"

echo Subiendo a GitHub...
git push origin main
pause