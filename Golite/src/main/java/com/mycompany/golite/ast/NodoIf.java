package com.mycompany.golite.ast;

/**
 * Nodo que representa la sentencia if / else if / else.

 */
public class NodoIf extends Nodo {

    /** Condición del if — debe evaluar a bool */
    public Nodo condicion;

    /** Bloque que se ejecuta si la condición es true */
    public Nodo bloqueThen;

    /**
     * Bloque o nodo que se ejecuta si la condición es false.
     * Puede ser:
     *   - null          = no hay else
     *   - NodoBloque    = else { }
     *   - NodoIf        =else if 
     */
    public Nodo bloqueElse;

    /**
     * @param condicion   expresión booleana
     * @param bloqueThen  bloque del if
     * @param bloqueElse  bloque del else / else if, o null
     * @param linea       línea en el código fuente
     * @param columna     columna en el código fuente
     */
    public NodoIf(Nodo condicion, Nodo bloqueThen, Nodo bloqueElse,
                  int linea, int columna) {
        super(linea, columna);
        this.condicion   = condicion;
        this.bloqueThen  = bloqueThen;
        this.bloqueElse  = bloqueElse;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {

        // Evaluar la condición
        Object valorCond = condicion.ejecutar(entorno);

        // Verificar que la condición sea booleana
        if (!(valorCond instanceof Boolean)) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": La condición del 'if' debe ser de tipo bool, "
                + "se recibió: " + nombreTipo(valorCond) + "."
            );
        }

        boolean condBool = (Boolean) valorCond;

        if (condBool) {
            // Ejecutar bloque then
            return bloqueThen.ejecutar(entorno);
        } else if (bloqueElse != null) {
            // Ejecutar bloque else o else if
            return bloqueElse.ejecutar(entorno);
        }

        return null;
    }

    // ─────────────────────────────────────────────────────────────────
    // utiñlidad
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
        String s = "If(" + condicion + ") " + bloqueThen;
        if (bloqueElse != null) s += " Else " + bloqueElse;
        return s;
    }
}