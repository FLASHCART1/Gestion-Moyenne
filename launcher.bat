@echo off
setlocal enabledelayedexpansion
title Gestion des Moyennes - Lanceur

:: ============================================================
::  CONFIGURATION
:: ============================================================
set "MIN_JAVA=26"
set "JAR_URL=https://github.com/FLASHCART1/Gestion-Moyenne/releases/download/Release/Gestion.Moyennes.jar"
set "JAR_NAME=Gestion.Moyennes.jar"
set "JAVA_DL_URL=https://api.adoptium.net/v3/installer/latest/26/ga/windows/x64/jdk/hotspot/normal/eclipse"
set "JAVA_INSTALLER=java26_installer.msi"
set "JAVA_EXE=java"
set "LOG_FILE=launcher.log"

echo Lancement : %DATE% %TIME% > "%LOG_FILE%"

:: ============================================================
::  BANNIERE
:: ============================================================
echo.
echo  ================================================
echo   Gestion des Moyennes - Lanceur v1.0
echo  ================================================
echo.

:: ============================================================
::  ETAPE 1 -- Verifier curl
:: ============================================================
curl --version >nul 2>&1
if %errorlevel% neq 0 (
    echo  [ERREUR] curl est introuvable.
    echo  Utilisez Windows 10 version 1803 ou superieure.
    goto :fin_erreur
)

:: ============================================================
::  ETAPE 2 -- Verifier la version de Java
:: ============================================================
echo  [1/3] Verification de Java...
echo.

java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo  [ATTENTION] Java non detecte sur ce systeme.
    goto :install_java
)

:: Extraire la version brute
for /f "tokens=3" %%v in ('java -version 2^>^&1 ^| findstr /i "version"') do set "RAW_VER=%%v"
set "RAW_VER=%RAW_VER:"=%"

:: Format ancien 1.8.x  --> majeure = 2e segment
:: Format moderne 17+   --> majeure = 1er segment
echo %RAW_VER% | findstr /r "^1\." >nul 2>&1
if %errorlevel% equ 0 (
    for /f "tokens=2 delims=." %%a in ("%RAW_VER%") do set "JAVA_MAJOR=%%a"
) else (
    for /f "tokens=1 delims=." %%a in ("%RAW_VER%") do set "JAVA_MAJOR=%%a"
)

echo  Version detectee  : %RAW_VER%
echo  Version majeure   : %JAVA_MAJOR%
echo  Version minimale  : %MIN_JAVA%
echo.

if %JAVA_MAJOR% GEQ %MIN_JAVA% (
    echo  [OK] Java %JAVA_MAJOR% est compatible.
    echo.
    goto :download_jar
)

echo  [ATTENTION] Java %JAVA_MAJOR% est trop ancien ^(minimum : Java %MIN_JAVA%^).

:: ============================================================
::  ETAPE 3 -- Installer Java
:: ============================================================
:install_java
echo.
echo  ------------------------------------------------
echo  [2/3] Installation de Java %MIN_JAVA%
echo  ------------------------------------------------
echo.

:: --- Tentative 1 : winget (essaie plusieurs IDs) ---
winget --version >nul 2>&1
if %errorlevel% equ 0 (
    echo  [INFO] winget disponible. Recherche de Java %MIN_JAVA%...
    echo.

    for %%p in (
        Eclipse.Temurin.%MIN_JAVA%.JDK
        EclipseAdoptium.Temurin.%MIN_JAVA%.JDK
        Oracle.JDK.%MIN_JAVA%
        Microsoft.OpenJDK.%MIN_JAVA%
    ) do (
        echo  [INFO] Essai : %%p
        winget install --id %%p ^
            --silent ^
            --accept-package-agreements ^
            --accept-source-agreements ^
            >nul 2>&1
        if !errorlevel! equ 0 (
            echo  [OK] Java installe via winget ^(%%p^).
            goto :refresh_path
        )
    )
    echo  [INFO] Aucun paquet winget trouve. Telechargement direct...
) else (
    echo  [INFO] winget absent. Telechargement direct...
)

:: --- Tentative 2 : telechargement MSI depuis Adoptium ---
echo.
echo  [INFO] Telechargement de Java %MIN_JAVA% ^(installateur MSI^)...
echo  Source  : %JAVA_DL_URL%
echo  Cible   : %JAVA_INSTALLER%
echo.
echo  Cela peut prendre quelques minutes...
echo.

curl -L --progress-bar -o "%JAVA_INSTALLER%" "%JAVA_DL_URL%"
if %errorlevel% neq 0 (
    echo.
    echo  [INFO] curl a echoue. Tentative via PowerShell...
    powershell -NoProfile -ExecutionPolicy Bypass -Command ^
        "Invoke-WebRequest -Uri '%JAVA_DL_URL%' -OutFile '%JAVA_INSTALLER%' -UseBasicParsing"
    if !errorlevel! neq 0 (
        echo.
        echo  [ERREUR] Telechargement impossible.
        echo  Telechargez manuellement depuis :
        echo  https://adoptium.net/temurin/releases/?version=%MIN_JAVA%
        echo  Puis relancez ce script.
        goto :fin_erreur
    )
)

:: Verifier que le fichier est bien un MSI (taille minimum 1 Mo)
for %%F in ("%JAVA_INSTALLER%") do set "MSI_SIZE=%%~zF"
if %MSI_SIZE% LSS 1000000 (
    echo  [ERREUR] Fichier telecharge invalide ^(%MSI_SIZE% octets^).
    del /q "%JAVA_INSTALLER%" 2>nul
    goto :fin_erreur
)

echo.
echo  [INFO] Installation de Java %MIN_JAVA%...
echo  ^(Une fenetre UAC peut apparaitre -- cliquez sur Oui^)
echo.

msiexec /i "%JAVA_INSTALLER%" /quiet /norestart ^
    ADDLOCAL=FeatureMain,FeatureEnvironment,FeatureJarFileRunWith,FeatureJavaHome
set "MSI_ERR=%errorlevel%"

if exist "%JAVA_INSTALLER%" del /q "%JAVA_INSTALLER%"

if %MSI_ERR% neq 0 (
    echo  [ERREUR] Installation echouee ^(code %MSI_ERR%^).
    echo  Relancez ce script en tant qu'Administrateur.
    goto :fin_erreur
)

echo  [OK] Java %MIN_JAVA% installe avec succes.

:: ============================================================
::  Actualiser le PATH apres installation
:: ============================================================
:refresh_path
echo.
echo  [INFO] Recherche de java.exe...

:: Chercher dans les emplacements standards
for %%d in (
    "%ProgramFiles%\Eclipse Adoptium"
    "%ProgramFiles%\Java"
    "%ProgramFiles%\Microsoft"
) do (
    if exist "%%~d" (
        for /d %%j in ("%%~d\jdk-%MIN_JAVA%*" "%%~d\jdk%MIN_JAVA%*") do (
            if exist "%%j\bin\java.exe" (
                set "JAVA_EXE=%%j\bin\java.exe"
                echo  [OK] java.exe trouve : !JAVA_EXE!
                goto :test_java
            )
        )
    )
)

:: Recharger le PATH systeme depuis le registre
echo  [INFO] Actualisation du PATH depuis le registre...
for /f "skip=2 tokens=3*" %%a in (
    'reg query "HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v Path 2^>nul'
) do set "SYS_PATH=%%a %%b"
if defined SYS_PATH set "PATH=%SYS_PATH%;%PATH%"

:test_java
"%JAVA_EXE%" -version >nul 2>&1
if %errorlevel% neq 0 (
    echo.
    echo  [ERREUR] java.exe toujours introuvable apres installation.
    echo  Redemarrez Windows et relancez ce script.
    goto :fin_erreur
)
echo  [OK] Java operationnel.

:: ============================================================
::  ETAPE 4 -- Telecharger le JAR
:: ============================================================
:download_jar
echo.
echo  ------------------------------------------------
echo  [3/3] Telechargement de l'application
echo  ------------------------------------------------
echo.

if exist "%JAR_NAME%" (
    echo  [INFO] %JAR_NAME% est deja present.
    set /p "REDOWN= Retelecharger ? ^(o/N^) : "
    if /i "!REDOWN!"=="o" (
        del /q "%JAR_NAME%"
    ) else (
        echo  [OK] Utilisation de la version existante.
        echo.
        goto :launch
    )
)

echo  [INFO] Telechargement en cours...
echo.
curl -L --progress-bar -o "%JAR_NAME%" "%JAR_URL%"

if %errorlevel% neq 0 (
    echo.
    echo  [ERREUR] Telechargement echoue.
    echo  Verifiez votre connexion internet et reessayez.
    if exist "%JAR_NAME%" del /q "%JAR_NAME%"
    goto :fin_erreur
)

:: Verifier que le JAR n'est pas vide
for %%F in ("%JAR_NAME%") do set "JAR_SIZE=%%~zF"
if %JAR_SIZE% LSS 1000 (
    echo.
    echo  [ERREUR] Fichier corrompu ^(%JAR_SIZE% octets^).
    del /q "%JAR_NAME%"
    goto :fin_erreur
)

echo.
echo  [OK] Telechargement termine ^(%JAR_SIZE% octets^).

:: ============================================================
::  ETAPE 5 -- Lancer l'application
:: ============================================================
:launch
echo.
echo  ================================================
echo   Lancement de Gestion des Moyennes...
echo  ================================================
echo.

"%JAVA_EXE%" -jar "%JAR_NAME%" 2>>"%LOG_FILE%"
set "JAVA_ERR=%errorlevel%"

if %JAVA_ERR% neq 0 (
    echo.
    echo  [ERREUR] L'application a quitte avec le code %JAVA_ERR%.
    echo.
    echo  Contenu de launcher.log :
    echo  ----------------------------------------
    type "%LOG_FILE%"
    echo  ----------------------------------------
)

goto :fin

:fin_erreur
echo.
echo  Une erreur s'est produite. Consultez launcher.log si disponible.
echo.

:fin
echo.
echo  Appuyez sur une touche pour fermer.
pause >nul
endlocal
exit /b
