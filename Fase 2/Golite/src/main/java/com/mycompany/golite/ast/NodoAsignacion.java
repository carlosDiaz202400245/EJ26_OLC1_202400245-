package com.mycompany.golite.ast;

import com.mycompany.golite.Entorno;

/**
 * Nodo de asignación. La variable debe existir, y el nuevo valor debe coincidir
 * con su tipo declarado; única excepción: int → float64.
 */
public class NodoAsignacion extends Nodo {

    public String nombre;

    /** Operador: "=", "+=", "-=" */
    public String operador;

    public Nodo expresion;

    public NodoAsignacion(String nombre, String operador, Nodo expresion,
                          int linea, int columna) {
        super(linea, columna);
        this.nombre    = nombre;
        this.operador  = operador;
        this.expresion = expresion;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {

        // La variable debe existir en algún scope
        if (!entorno.existe(nombre)) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": La variable '" + nombre + "' no está declarada."
            );
        }

        Object valorActual = entorno.obtener(nombre);
        String tipoActual  = entorno.obtenerTipo(nombre);
        Object valorNuevo  = expresion.ejecutar(entorno);

        Object resultado = aplicarOperador(valorActual, valorNuevo, tipoActual);
        resultado = verificarTipo(resultado, tipoActual);   // valida tipo antes de asignar
        entorno.asignar(nombre, resultado);

        return null;
    }

    // ─── APLICAR OPERADOR ──────────────────────────────────────────────
    private Object aplicarOperador(Object actual, Object nuevo, String tipo) {
        switch (operador) {
            case "=":
                return nuevo;   // asignación simple; el tipo se verifica después

            case "+=":
                // variable = variable + expresión
                if (actual instanceof Integer && nuevo instanceof Integer) {
                    return (Integer) actual + (Integer) nuevo;
                }
                if (esNumerico(actual) && esNumerico(nuevo)) {
                    return toDouble(actual) + toDouble(nuevo);
                }
                // Concatenación de strings
                if (actual instanceof String && nuevo instanceof String) {
                    return (String) actual + (String) nuevo;
                }
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": Operación '+=' no válida entre tipos "
                    + nombreTipo(actual) + " y " + nombreTipo(nuevo) + "."
                );

            case "-=":
                // variable = variable - expresión
                if (actual instanceof Integer && nuevo instanceof Integer) {
                    return (Integer) actual - (Integer) nuevo;
                }
                if (esNumerico(actual) && esNumerico(nuevo)) {
                    return toDouble(actual) - toDouble(nuevo);
                }
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": Operación '-=' no válida entre tipos "
                    + nombreTipo(actual) + " y " + nombreTipo(nuevo) + "."
                );

            default:
                throw new RuntimeException(
                    "[Error Interno] Operador de asignación desconocido: " + operador
                );
        }
    }

    // ─── COMPATIBILIDAD DE TIPOS ───────────────────────────────────────
    private Object verificarTipo(Object valor, String tipoDeclarado) {
        switch (tipoDeclarado) {
            case "int":
                if (valor instanceof Integer) return valor;
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": No se puede asignar un valor de tipo "
                    + nombreTipo(valor) + " a la variable '"
                    + nombre + "' de tipo int."
                );

            case "float64":
                if (valor instanceof Double)  return valor;
                // Conversión implícita int → float64
                if (valor instanceof Integer) return ((Integer) valor).doubleValue();
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": No se puede asignar un valor de tipo "
                    + nombreTipo(valor) + " a la variable '"
                    + nombre + "' de tipo float64."
                );

            case "string":
                if (valor instanceof String) return valor;
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": No se puede asignar un valor de tipo "
                    + nombreTipo(valor) + " a la variable '"
                    + nombre + "' de tipo string."
                );

            case "bool":
                if (valor instanceof Boolean) return valor;
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": No se puede asignar un valor de tipo "
                    + nombreTipo(valor) + " a la variable '"
                    + nombre + "' de tipo bool."
                );

            case "rune":
                if (valor instanceof String) return valor;
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": No se puede asignar un valor de tipo "
                    + nombreTipo(valor) + " a la variable '"
                    + nombre + "' de tipo rune."
                );

            default:
                return valor;   // tipo compuesto: se acepta
        }
    }

    // ─── UTILIDADES ────────────────────────────────────────────────────
    private boolean esNumerico(Object v) {
        return v instanceof Integer || v instanceof Double;
    }

    private double toDouble(Object v) {
        if (v instanceof Double)  return (Double) v;
        if (v instanceof Integer) return ((Integer) v).doubleValue();
        throw new RuntimeException("No se puede convertir a float64: " + v);
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
        return "Asign(" + nombre + " " + operador + " " + expresion + ")";
    }
}