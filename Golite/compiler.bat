@echo off
chcp 65001 >nul

echo         fase 1 en compilacion          



:: ─── RUTAS DE DEPENDENCIAS: lo puse mejor asi por que no tengo bien configuradp ─────────
set CUP_JAR=C:\Users\charl\Desktop\Dependencias\java-cup-11b-20160615.jar
set CUP_RT=C:\Users\charl\Desktop\Dependencias\java-cup-runtime-11b-20160615.jar
set JFLEX_JAR=C:\Users\charl\Desktop\Dependencias\jflex-1.9.1\lib\jflex-full-1.9.1.jar

:: ─── RUTAS DEL PROYECTO  ────────
set SRC=src\main\java
set CUP_SRC=src\main\cup\golite.cup
set FLEX_SRC=src\main\jflex\golite.flex
set PARSER_DIR=src\main\java\com\mycompany\golite\parser
set LEXER_DIR=src\main\java\com\mycompany\golite
set OUT=target\classes
set MAIN=com.mycompany.golite.Golite

:: ─────────────────────────────────────────────────────────────────────
:: paso 1, limpiando por si hay una compilacion anterior
:: ─────────────────────────────────────────────────────────────────────
echo [1/5] Limpiando compilacion anterior...
if exist %OUT% rmdir /s /q %OUT%
mkdir %OUT%

:: Borrar archivos generados anteriormente para evitar conflictos
if exist %PARSER_DIR%\parser.java del /q %PARSER_DIR%\parser.java
if exist %PARSER_DIR%\sym.java    del /q %PARSER_DIR%\sym.java
if exist %LEXER_DIR%\GoliteLexer.java del /q %LEXER_DIR%\GoliteLexer.java
echo     OK
echo.


:: ─────────────────────────────────────────────────────────────────────
::  Crear carpeta del parser si no existe
:: ─────────────────────────────────────────────────────────────────────
if not exist %PARSER_DIR% (
    mkdir %PARSER_DIR%
    echo [2/5] Carpeta parser creada.
) else (
    echo [2/5] Carpeta parser ya existe.
)
echo.

:: ─────────────────────────────────────────────────────────────────────
::  Compilar CUP 
:: ─────────────────────────────────────────────────────────────────────
echo [3/5] Compilando CUP (golite.cup)...
java -cp "%CUP_JAR%" java_cup.Main ^
    -package com.mycompany.golite.parser ^
    -parser parser ^
    -symbols sym ^
    -destdir %PARSER_DIR% ^
    %CUP_SRC%

if errorlevel 1 (
    echo.
    echo *** ERROR en CUP. Revisa golite.cup y vuelve a intentarlo. ***
    pause
    exit /b 1
)
echo     OK - parser.java y sym.java generados.
echo.

:: ─────────────────────────────────────────────────────────────────────
::  Compilar JFlex 
:: ─────────────────────────────────────────────────────────────────────
echo [4/5] Compilando JFlex (golite.flex)...
java -jar "%JFLEX_JAR%" ^
    -d %LEXER_DIR% ^
    %FLEX_SRC%

if errorlevel 1 (
    echo.
    echo *** ERROR en JFlex. Revisa golite.flex y vuelve a intentarlo. ***
    pause
    exit /b 1
)
echo     OK - GoliteLexer.java generado.
echo.

:: ─────────────────────────────────────────────────────────────────────
:: Compilar todo el Java
:: ─────────────────────────────────────────────────────────────────────
echo [5/5] Compilando Java...
javac -encoding UTF-8 ^
    -cp "%CUP_RT%" ^
    -d %OUT% ^
    %SRC%\com\mycompany\golite\parser\*.java ^
    %SRC%\com\mycompany\golite\*.java ^
    %SRC%\com\mycompany\golite\gui\*.java

if errorlevel 1 (
    echo.
    echo *** ERROR en javac. Revisa los archivos .java ***
    pause
    exit /b 1
)
echo     OK - Compilacion exitosa.
echo.



echo           INICIANDO GOLITE IDE...            

java -cp "%OUT%;%CUP_RT%" %MAIN%

pause