package com.mycompany.golite.ast;

/**
 * Nodo que representa la función embebida strconv.Atoi.
 *
 * Convierte una cadena que representa un entero en un valor int.
 * Si la cadena no es un entero válido = error semántico.
 * No acepta decimales
 *
 */
public class NodoAtoi extends Nodo {

    /** Expresión que debe evaluar a string */
    public Nodo expresion;

    /**
     * @param expresion expresion de tipo string
     * @param linea     línea en el código fuente
     * @param columna   columna en el codigo fuente
     */
    public NodoAtoi(Nodo expresion, int linea, int columna) {
        super(linea, columna);
        this.expresion = expresion;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {

        Object valor = expresion.ejecutar(entorno);

        // El argumento debe ser string
        if (!(valor instanceof String)) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": strconv.Atoi requiere un argumento de tipo string, "
                + "se recibió: " + nombreTipo(valor) + "."
            );
        }

        String texto = (String) valor;

        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": strconv.Atoi no puede convertir \"" + texto
                + "\" a int. Asegúrese de que sea un número entero válido."
            );
        }
    }

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
        return "Atoi(" + expresion + ")";
    }
}