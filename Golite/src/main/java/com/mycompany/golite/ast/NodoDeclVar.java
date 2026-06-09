package com.mycompany.golite.ast;

/**
 * Nodo que representa la declaración de una variable.
 *
 * Valores por defecto según el enunciado
 *   int     = 0
 *   float64 = 0.0
 *   string  = ""
 *   bool    = false
 *   rune    = 0
 *   otros   = nil que es null va
 */
public class NodoDeclVar extends Nodo {

    /** Nombre de la variable */
    public String nombre;

    public String tipo;

    /**
     * Expresión con el valor inicial.
     * null si no se asigna valor 
     */
    public Nodo expresion;

    /**
     * @param nombre     nombre de la variable
     * @param tipo       tipo explícito o null si se infiere
     * @param expresion  expresión del valor inicial o null si no hay
     * @param linea      línea en el código fuente
     * @param columna    columna en el código fuente
     */
    public NodoDeclVar(String nombre, String tipo, Nodo expresion,
                       int linea, int columna) {
        super(linea, columna);
        this.nombre    = nombre;
        this.tipo      = tipo;
        this.expresion = expresion;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {

        // Verificar que la variable no exista en el scope actual osea el alcanse 
        if (entorno.existeEnScopeActual(nombre)) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": La variable '" + nombre + "' ya fue declarada en este ámbito."
            );
        }

        Object valor;

        if (expresion != null) {
            // Evaluar la expresión para obtener el valor
            valor = expresion.ejecutar(entorno);

            // Si hay tipo explícito, verificar compatibilidad
            if (tipo != null) {
                valor = verificarYConvertir(valor);
            }
            // Si no hay tipo explícito, inferir del valor obtenido
            // y guardar el tipo inferido
            else {
                tipo = inferirTipo(valor);
            }
        } else {
            // Sin expresión → tomar el valor por defecto del tipo
            valor = valorPorDefecto();
        }

        // Registrar la variable en el entorno con su tipo y valor
        entorno.declarar(nombre, tipo, valor);

        return null; // Las declaraciones no retornan valor
    }

    // ─────────────────────────────────────────────────────────────────
    // mirar compativilidad
    // ─────────────────────────────────────────────────────────────────
    private Object verificarYConvertir(Object valor) {
        switch (tipo) {
            case "int":
                if (valor instanceof Integer) return valor;
                // No se permite asignar float64 a int
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": No se puede asignar un valor de tipo "
                    + nombreTipo(valor) + " a una variable de tipo int."
                );

            case "float64":
                if (valor instanceof Double)  return valor;
                // Conversión implícita int = float64
                if (valor instanceof Integer) return ((Integer) valor).doubleValue();
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": No se puede asignar un valor de tipo "
                    + nombreTipo(valor) + " a una variable de tipo float64."
                );

            case "string":
                if (valor instanceof String) return valor;
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": No se puede asignar un valor de tipo "
                    + nombreTipo(valor) + " a una variable de tipo string."
                );

            case "bool":
                if (valor instanceof Boolean) return valor;
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": No se puede asignar un valor de tipo "
                    + nombreTipo(valor) + " a una variable de tipo bool."
                );

            case "rune":
                if (valor instanceof String) return valor;
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": No se puede asignar un valor de tipo "
                    + nombreTipo(valor) + " a una variable de tipo rune."
                );

            default:
                // Tipo compuesto — se acepta nil o el valor directo
                return valor;
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // inferir el valor
    // ─────────────────────────────────────────────────────────────────
    private String inferirTipo(Object valor) {
        if (valor instanceof Integer) return "int";
        if (valor instanceof Double)  return "float64";
        if (valor instanceof String)  return "string";
        if (valor instanceof Boolean) return "bool";
        if (valor == null)            return "nil";
        return valor.getClass().getSimpleName();
    }

    // ─────────────────────────────────────────────────────────────────
    // valor por defecto segun el tipo declarado
    // ─────────────────────────────────────────────────────────────────
    private Object valorPorDefecto() {
        if (tipo == null) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": Se requiere un tipo o un valor inicial para declarar '"
                + nombre + "'."
            );
        }
        switch (tipo) {
            case "int":     return 0;
            case "float64": return 0.0;
            case "string":  return "";
            case "bool":    return false;
            case "rune":    return 0;
            default:        return null; // tipos compuestos → nil
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // utilidad
    // ─────────────────────────────────────────────────────────────────
    private String nombreTipo(Object v) {
        if (v instanceof Integer) return "int";
        if (v instanceof Double)  return "float64";
        if (v instanceof String)  return "string";
        if (v instanceof Boolean) return "bool";
        if (v == null)            return "nil";
        return v.getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return "DeclVar(" + tipo + " " + nombre
             + (expresion != null ? " = " + expresion : "") + ")";
    }
}