package com.mycompany.golite.ast;

/**
 Nodo que representa una operación unaria.
 */
public class NodoUnario extends Nodo {

    /** Operador:- para negación aritmetica, ! para negacion logica */
    public String operador;

    /** Expresión sobre la que se aplica el operador */
    public Nodo expresion;

    /**
     * @param operador  - o !
     * @param expresion nodo de la expresión a negar
     * @param linea     línea en el código fuente
     * @param columna   columna en el código fuente
     */
    public NodoUnario(String operador, Nodo expresion, int linea, int columna) {
        super(linea, columna);
        this.operador  = operador;
        this.expresion = expresion;
    }

    /**
     * Evalúa la expresión y aplica el operador unario.
     */
    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        Object valor = expresion.ejecutar(entorno);

        // Verificar que no sea nil
        if (valor == null) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": No se puede aplicar '" + operador + "' a un valor nil."
            );
        }

        switch (operador) {
            case "-": return negarAritmetico(valor);
            case "!": return negarLogico(valor);
            default:
                throw new RuntimeException(
                    "[Error Interno] Operador unario desconocido: " + operador
                );
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // negacion aritmetica 
    // ─────────────────────────────────────────────────────────────────
    private Object negarAritmetico(Object valor) {
        if (valor instanceof Integer) {
            return -(Integer) valor;
        }
        if (valor instanceof Double) {
            return -(Double) valor;
        }
        throw new RuntimeException(
            "[Error Semántico] Línea " + linea + ", Columna " + columna
            + ": El operador '-' solo aplica a tipos numéricos (int, float64), "
            + "se recibió: " + nombreTipo(valor) + "."
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // negacion logica
    // ─────────────────────────────────────────────────────────────────
    private Object negarLogico(Object valor) {
        if (valor instanceof Boolean) {
            return !(Boolean) valor;
        }
        throw new RuntimeException(
            "[Error Semántico] Línea " + linea + ", Columna " + columna
            + ": El operador '!' solo aplica a tipo bool, "
            + "se recibió: " + nombreTipo(valor) + "."
        );
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
        return "(" + operador + expresion + ")";
    }
}