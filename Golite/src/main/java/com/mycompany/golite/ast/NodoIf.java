package com.mycompany.golite.ast;

/** Nodo que representa la sentencia if / else if / else. */
public class NodoIf extends Nodo {

    /** Condición del if  */
    public Nodo condicion;

    /** Bloque que se ejecuta si la condición es true. */
    public Nodo bloqueThen;


    public Nodo bloqueElse;

    public NodoIf(Nodo condicion, Nodo bloqueThen, Nodo bloqueElse,
                  int linea, int columna) {
        super(linea, columna);
        this.condicion   = condicion;
        this.bloqueThen  = bloqueThen;
        this.bloqueElse  = bloqueElse;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {

        // La condición debe ser booleana
        Object valorCond = condicion.ejecutar(entorno);
        if (!(valorCond instanceof Boolean)) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": La condición del 'if' debe ser de tipo bool, "
                + "se recibió: " + nombreTipo(valorCond) + "."
            );
        }

        boolean condBool = (Boolean) valorCond;

        if (condBool) {
            return bloqueThen.ejecutar(entorno);
        } else if (bloqueElse != null) {
            return bloqueElse.ejecutar(entorno);   // else o else if
        }

        return null;
    }

    // ─── UTILIDAD ──────────────────────────────────────────────────────
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
        String s = "If(" + condicion + ") " + bloqueThen;
        if (bloqueElse != null) s += " Else " + bloqueElse;
        return s;
    }
}