# GoLite

Intérprete y mini-IDE para **GoLite**, un subconjunto del lenguaje Go.
El proyecto incluye análisis léxico (JFlex), análisis sintáctico (CUP),
construcción de AST e interpretación, todo dentro de una interfaz gráfica Swing.

## Requisitos

- **JDK 21** o superior
- **Apache Maven** o Cualquier editor para Java
- Dependencias usadas: `java-cup` y `java-cup-runtime` (11b) y `jflex` (1.9.1)

## Estructura del proyecto

```
src/main/cup/golite.cup      Gramática del parser (CUP)
src/main/jflex/golite.flex   Definición del lexer (JFlex)
src/main/java/...            Código fuente Java (AST, intérprete, GUI)
compiler.bat                 Script de compilación y ejecución (Windows)
pom.xml                      Configuración de Maven
```

> Los archivos `parser.java`, `sym.java` y `GoliteLexer.java` se **generan
> automáticamente** a partir de `golite.cup` y `golite.flex`.

## Cómo compilar y ejecutar

Hay dos formas de construir el proyecto.

### Camino 1 — Maven (genera el `.jar` ejecutable)

Desde la raíz del proyecto:

```
mvn clean package
```

Esto genera el lexer y el parser, compila todo y produce un **JAR ejecutable
con todas las dependencias incluidas** en:

```
target/golite-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Para abrir la aplicación desde el ejecutable:

```
java -jar target/golite-1.0-SNAPSHOT-jar-with-dependencies.jar
```

(o simplemente doble clic sobre el `.jar`).

> Usa el JAR que termina en `-jar-with-dependencies`. El otro
> (`golite-1.0-SNAPSHOT.jar`) no incluye el runtime de CUP y fallaría al
> ejecutarse.

### Camino 2 — Script `.bat` (Windows)

Si no tienes Maven configurado, usa el script incluido. Genera el parser con
CUP, el lexer con JFlex, compila todo el Java y **abre la aplicación**:

```
compiler.bat
```

> Antes de usarlo, revisa que las rutas a las dependencias al inicio del
> archivo (`CUP_JAR`, `CUP_RT`, `JFLEX_JAR`) apunten a la ubicación correcta
> en tu máquina.

## Uso

Al iniciar se abre el IDE de GoLite, donde puedes escribir o cargar un archivo
`.glt`, ejecutarlo y ver la salida junto con el reporte de errores léxicos y
sintácticos.
