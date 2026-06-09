package com.mycompany.golite.ast;

/**
 * Nodo que representa la función embebida strconv.ParseFloat.
 *
 * Convierte una cadena que representa un número decimal o entero
 * en un valor de tipo float64.
 * Si la cadena no es válida = error semantico.
 
 */
public class NodoParseFloat extends Nodo {

    /** Expresion que debe evaluar a string */
    public Nodo expresion;

    /**
     * @param expresion expresión de tipo string
     * @param linea     línea en el código fuente
     * @param columna   columna en el código fuente
     */
    public NodoParseFloat(Nodo expresion, int linea, int columna) {
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
                + ": strconv.ParseFloat requiere un argumento de tipo string, "
                + "se recibió: " + nombreTipo(valor) + "."
            );
        }

        String texto = (String) valor;

        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": strconv.ParseFloat no puede convertir \"" + texto
                + "\" a float64. Asegúrese de que sea un número válido."
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
        return "ParseFloat(" + expresion + ")";
    }
}