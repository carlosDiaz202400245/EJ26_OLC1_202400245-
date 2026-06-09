package com.mycompany.golite.ast;

/**
 * Nodo que representa la sentencia for 
 */
public class Nodofor extends Nodo {

    /** Inicialización: var_decl o asignacion */
    public Nodo init;

    /** Condición del loop — debe evaluar a bool */
    public Nodo condicion;

    /** Post-paso: i++, i--, o asignacion */
    public Nodo post;

    /** Cuerpo del loop */
    public Nodo bloque;

    /**
     * @param init      inicialización
     * @param condicion condición del loop
     * @param post      post-paso
     * @param bloque    cuerpo del loop
     * @param linea     línea en el código fuente
     * @param columna   columna en el código fuente
     */
    public Nodofor(Nodo init, Nodo condicion, Nodo post, Nodo bloque,
                   int linea, int columna) {
        super(linea, columna);
        this.init      = init;
        this.condicion = condicion;
        this.post      = post;
        this.bloque    = bloque;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {

        // Scope propio para que el init viva dentro del for
        com.mycompany.golite.Entorno scopeFor =
                new com.mycompany.golite.Entorno(entorno);

        // Ejecutar inicialización
        if (init != null) init.ejecutar(scopeFor);

        // Loop principal
        while (true) {

            // Evaluar condición
            Object valorCond = condicion.ejecutar(scopeFor);

            if (!(valorCond instanceof Boolean)) {
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": La condición del 'for' debe ser bool, "
                    + "se recibió: " + nombreTipo(valorCond) + "."
                );
            }

            // Condición falsa → salir
            if (!(Boolean) valorCond) break;

            // Ejecutar cuerpo
            Object resultado = bloque.ejecutar(scopeFor);

            // Manejar señales de control
            if (resultado instanceof com.mycompany.golite.Entorno.BreakSignal) {
                break;
            }
            if (resultado instanceof com.mycompany.golite.Entorno.ContinueSignal) {
                // continue = ejecutar post y volver a condición
                if (post != null) post.ejecutar(scopeFor);
                continue;
            }
            if (resultado instanceof com.mycompany.golite.Entorno.ReturnSignal) {
                return resultado;
            }

            // Ejecutar post-paso
            if (post != null) post.ejecutar(scopeFor);
        }

        return null;
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
        return "For(" + init + "; " + condicion + "; " + post + ") " + bloque;
    }
}