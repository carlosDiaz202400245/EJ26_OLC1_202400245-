package com.mycompany.golite.ast;

/**
 * Nodo que representa la función embebida reflect.TypeOf().
 *
 * Devuelve el tipo de un valor en tiempo de ejecución como string.
 */
public class NodoTypeOf extends Nodo {

    /** Expresión qie se quiere conocer */
    public Nodo expresion;

    /**
     * @param expresion expresión a evaluar
     * @param linea     línea en el código fuente
     * @param columna   columna en el código fuente
     */
    public NodoTypeOf(Nodo expresion, int linea, int columna) {
        super(linea, columna);
        this.expresion = expresion;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {

        Object valor = expresion.ejecutar(entorno);
        return nombreTipo(valor);
    }

    // ─────────────────────────────────────────────────────────────────
    // NOMBRE DEL TIPO según el enunciado
    // ─────────────────────────────────────────────────────────────────
    private String nombreTipo(Object v) {
        if (v == null)            return "nil";
        if (v instanceof Integer) return "int";
        if (v instanceof Double)  return "float64";
        if (v instanceof Boolean) return "bool";
        if (v instanceof String)  return "string";
        return v.getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return "TypeOf(" + expresion + ")";
    }
}