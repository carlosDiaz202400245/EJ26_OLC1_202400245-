package com.mycompany.golite.ast;

import com.mycompany.golite.Entorno;

/**
 * Reglas del enunciado:
 *   - La variable debe existir previamente sin o error semantico
 *   - El nuevo valor debe ser del mismo tipo que el declarado
 *   - Única excepcion: se puede asignar int a una variable float64
 */
public class NodoAsignacion extends Nodo {

    /** Nombre de la variable a asignar */
    public String nombre;

    /** Operador: "=", "+=", "-=" */
    public String operador;

    /** Expresion con el nuevo valor */
    public Nodo expresion;

    /**
     * @param nombre    nombre de la variable
     * @param operador  =, += o -=
     * @param expresion expresion del nuevo valor
     * @param linea     lonea en el código fuente
     * @param columna   columna en el código fuente
     */
    public NodoAsignacion(String nombre, String operador, Nodo expresion,
                          int linea, int columna) {
        super(linea, columna);
        this.nombre    = nombre;
        this.operador  = operador;
        this.expresion = expresion;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {

        // Verificar que la variable existe en algún scope
        if (!entorno.existe(nombre)) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": La variable '" + nombre + "' no está declarada."
            );
        }

        // Obtener valor y tipo actuales de la variable
        Object valorActual = entorno.obtener(nombre);
        String tipoActual  = entorno.obtenerTipo(nombre);

        // Evaluar la expresión del lado derecho
        Object valorNuevo = expresion.ejecutar(entorno);

        // Aplicar el operador
        Object resultado = aplicarOperador(valorActual, valorNuevo, tipoActual);

        // Verificar compatibilidad de tipos antes de asignar
        resultado = verificarTipo(resultado, tipoActual);

        // Actualizar el valor en el entorno
        entorno.asignar(nombre, resultado);

        return null; 
    }

    // ─────────────────────────────────────────────────────────────────
    // aplicar operador
    // ─────────────────────────────────────────────────────────────────
    private Object aplicarOperador(Object actual, Object nuevo, String tipo) {
        switch (operador) {
            case "=":
                // Asignación simple, el nuevo valor se verifica después
                return nuevo;

            case "+=":
                // Suma implícita: variable = variable + expresión
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
                // Resta implícita: variable = variable - expresión
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

    // ─────────────────────────────────────────────────────────────────
    // compatibilidad de tipos
    // ─────────────────────────────────────────────────────────────────
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
                // Tipo compuesto aceptadp
                return valor;
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // utilidades
    // ─────────────────────────────────────────────────────────────────
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