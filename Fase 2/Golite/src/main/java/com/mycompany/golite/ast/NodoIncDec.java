package com.mycompany.golite.ast;

/**
 * Nodo que representa las operaciones de incremento y decremento.

 */
public class NodoIncDec extends Nodo {

    /** Nombre de la variable */
    public String nombre;

    /** Operador: "++" o "--" */
    public String operador;

    /**
     * @param nombre    nombre de la variable
     * @param operador  "++" o "--"
     * @param linea     línea en el código fuente
     * @param columna   columna en el código fuente
     */
    public NodoIncDec(String nombre, String operador, int linea, int columna) {
        super(linea, columna);
        this.nombre   = nombre;
        this.operador = operador;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {

        // Verificar que la variable existe
        if (!entorno.existe(nombre)) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": La variable '" + nombre + "' no está declarada."
            );
        }

        Object valor = entorno.obtener(nombre);

        // Solo aplica a tipos numéricos
        if (valor instanceof Integer) {
            int resultado = operador.equals("++")
                    ? (Integer) valor + 1
                    : (Integer) valor - 1;
            entorno.asignar(nombre, resultado);

        } else if (valor instanceof Double) {
            double resultado = operador.equals("++")
                    ? (Double) valor + 1.0
                    : (Double) valor - 1.0;
            entorno.asignar(nombre, resultado);

        } else {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": El operador '" + operador + "' solo aplica a tipos "
                + "numéricos (int, float64), se recibió: "
                + nombreTipo(valor) + "."
            );
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
        return "IncDec(" + nombre + operador + ")";
    }
}