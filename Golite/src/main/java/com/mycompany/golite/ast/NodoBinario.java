package com.mycompany.golite.ast;

/**
 * Nodo que representa una operación binaria.
 *
 * Operaciones aritméticas : +  -  *  /  %
 * Operaciones de comparación: ==  !=  <  <=  >  >=
 * Operaciones lógicas     : &&  ||
 *
 * Ejemplos: 1 + 2,  x > 5,  a && b,  "ho" + "la"
 */
public class NodoBinario extends Nodo {

    /** Operando izquierdo */
    public Nodo izquierda;

    /** Operador como string: "+", "-", "*", "/", "%", "==", "!=", "<", "<=", ">", ">=", "&&", "||" */
    public String operador;

    /** Operando derecho */
    public Nodo derecha;

    /**
     * @param izquierda operando izquierdo
     * @param operador  operador como string
     * @param derecha   operando derecho
     * @param linea     línea en el código fuente
     * @param columna   columna en el código fuente
     */
    public NodoBinario(Nodo izquierda, String operador, Nodo derecha,
                       int linea, int columna) {
        super(linea, columna);
        this.izquierda = izquierda;
        this.operador  = operador;
        this.derecha   = derecha;
    }

    /**
     * Evalúa ambos operandos y aplica el operador.
     * Maneja conversión implícita int → float64 según el enunciado.
     */
    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        Object valIzq = izquierda.ejecutar(entorno);
        Object valDer = derecha.ejecutar(entorno);

        // Verificar que ninguno sea nil
        if (valIzq == null || valDer == null) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": No se puede operar con valores nil."
            );
        }

        switch (operador) {
            case "+":  return opSuma(valIzq, valDer);
            case "-":  return opResta(valIzq, valDer);
            case "*":  return opMulti(valIzq, valDer);
            case "/":  return opDiv(valIzq, valDer);
            case "%":  return opMod(valIzq, valDer);
            case "==": return opIgualdad(valIzq, valDer, true);
            case "!=": return opIgualdad(valIzq, valDer, false);
            case "<":  return opRelacional(valIzq, valDer, "<");
            case "<=": return opRelacional(valIzq, valDer, "<=");
            case ">":  return opRelacional(valIzq, valDer, ">");
            case ">=": return opRelacional(valIzq, valDer, ">=");
            case "&&": return opAnd(valIzq, valDer);
            case "||": return opOr(valIzq, valDer);
            default:
                throw new RuntimeException(
                    "[Error Interno] Operador desconocido: " + operador
                );
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // SUMA
    // int+int=int  int+float=float  float+float=float  string+string=string
    // ─────────────────────────────────────────────────────────────────
    private Object opSuma(Object izq, Object der) {
        // Concatenación de strings
        if (izq instanceof String && der instanceof String) {
            return (String) izq + (String) der;
        }
        // Numérico
        if (esNumerico(izq) && esNumerico(der)) {
            if (izq instanceof Double || der instanceof Double) {
                return toDouble(izq) + toDouble(der);
            }
            return toInt(izq) + toInt(der);
        }
        throw new RuntimeException(errorTipo("+", izq, der));
    }

    // ─────────────────────────────────────────────────────────────────
    // RESTA
    // ─────────────────────────────────────────────────────────────────
    private Object opResta(Object izq, Object der) {
        if (esNumerico(izq) && esNumerico(der)) {
            if (izq instanceof Double || der instanceof Double) {
                return toDouble(izq) - toDouble(der);
            }
            return toInt(izq) - toInt(der);
        }
        throw new RuntimeException(errorTipo("-", izq, der));
    }

    // ─────────────────────────────────────────────────────────────────
    // MULTIPLICACIÓN
    // ─────────────────────────────────────────────────────────────────
    private Object opMulti(Object izq, Object der) {
        if (esNumerico(izq) && esNumerico(der)) {
            if (izq instanceof Double || der instanceof Double) {
                return toDouble(izq) * toDouble(der);
            }
            return toInt(izq) * toInt(der);
        }
        throw new RuntimeException(errorTipo("*", izq, der));
    }

    // ─────────────────────────────────────────────────────────────────
    // DIVISIÓN
    // ─────────────────────────────────────────────────────────────────
    private Object opDiv(Object izq, Object der) {
        if (esNumerico(izq) && esNumerico(der)) {
            // Verificar división por cero
            if (der instanceof Integer && toInt(der) == 0) {
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": No se puede dividir entre cero."
                );
            }
            if (der instanceof Double && toDouble(der) == 0.0) {
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": No se puede dividir entre cero."
                );
            }
            if (izq instanceof Double || der instanceof Double) {
                return toDouble(izq) / toDouble(der);
            }
            // División entera: trunca hacia cero (igual que Go)
            return toInt(izq) / toInt(der);
        }
        throw new RuntimeException(errorTipo("/", izq, der));
    }

    // ─────────────────────────────────────────────────────────────────
    // MÓDULO — solo int % int
    // ─────────────────────────────────────────────────────────────────
    private Object opMod(Object izq, Object der) {
        if (izq instanceof Integer && der instanceof Integer) {
            if (toInt(der) == 0) {
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": No se puede calcular el módulo con divisor cero."
                );
            }
            return toInt(izq) % toInt(der);
        }
        throw new RuntimeException(
            "[Error Semántico] Línea " + linea + ", Columna " + columna
            + ": El operador '%' solo aplica a tipos int."
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // IGUALDAD / DESIGUALDAD
    // ─────────────────────────────────────────────────────────────────
    private Object opIgualdad(Object izq, Object der, boolean esIgual) {
        boolean resultado;

        // Numérico con conversión implícita
        if (esNumerico(izq) && esNumerico(der)) {
            resultado = toDouble(izq) == toDouble(der);
        }
        // String
        else if (izq instanceof String && der instanceof String) {
            resultado = izq.equals(der);
        }
        // Bool
        else if (izq instanceof Boolean && der instanceof Boolean) {
            resultado = izq.equals(der);
        }
        // Rune (guardado como String de un char)
        else if (esRune(izq) && esRune(der)) {
            resultado = izq.equals(der);
        }
        else {
            throw new RuntimeException(errorTipo(esIgual ? "==" : "!=", izq, der));
        }

        return esIgual ? resultado : !resultado;
    }

    // ─────────────────────────────────────────────────────────────────
    // RELACIONALES:  <  <=  >  >=
    // ─────────────────────────────────────────────────────────────────
    private Object opRelacional(Object izq, Object der, String op) {
        // Numérico
        if (esNumerico(izq) && esNumerico(der)) {
            double a = toDouble(izq);
            double b = toDouble(der);
            switch (op) {
                case "<":  return a < b;
                case "<=": return a <= b;
                case ">":  return a > b;
                case ">=": return a >= b;
            }
        }
        // Rune — comparación por valor ASCII
        if (esRune(izq) && esRune(der)) {
            char a = ((String) izq).charAt(0);
            char b = ((String) der).charAt(0);
            switch (op) {
                case "<":  return a < b;
                case "<=": return a <= b;
                case ">":  return a > b;
                case ">=": return a >= b;
            }
        }
        throw new RuntimeException(errorTipo(op, izq, der));
    }

    // ─────────────────────────────────────────────────────────────────
    // AND / OR — ambos operandos deben ser bool
    // ─────────────────────────────────────────────────────────────────
    private Object opAnd(Object izq, Object der) {
        if (izq instanceof Boolean && der instanceof Boolean) {
            return (Boolean) izq && (Boolean) der;
        }
        throw new RuntimeException(
            "[Error Semántico] Línea " + linea + ", Columna " + columna
            + ": El operador '&&' requiere operandos de tipo bool."
        );
    }

    private Object opOr(Object izq, Object der) {
        if (izq instanceof Boolean && der instanceof Boolean) {
            return (Boolean) izq || (Boolean) der;
        }
        throw new RuntimeException(
            "[Error Semántico] Línea " + linea + ", Columna " + columna
            + ": El operador '||' requiere operandos de tipo bool."
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // UTILIDADES
    // ─────────────────────────────────────────────────────────────────

    /** true si el valor es Integer o Double */
    private boolean esNumerico(Object v) {
        return v instanceof Integer || v instanceof Double;
    }

    /** true si el valor es un rune (String de longitud 1 entre comillas simples) */
    private boolean esRune(Object v) {
        return v instanceof String && ((String) v).startsWith("'")
               && ((String) v).endsWith("'");
    }

    private int toInt(Object v) {
        if (v instanceof Integer) return (Integer) v;
        if (v instanceof Double)  return ((Double) v).intValue();
        throw new RuntimeException("No se puede convertir a int: " + v);
    }

    private double toDouble(Object v) {
        if (v instanceof Double)  return (Double) v;
        if (v instanceof Integer) return ((Integer) v).doubleValue();
        throw new RuntimeException("No se puede convertir a float64: " + v);
    }

    private String errorTipo(String op, Object izq, Object der) {
        return "[Error Semántico] Línea " + linea + ", Columna " + columna
             + ": Operación '" + op + "' no válida entre tipos "
             + nombreTipo(izq) + " y " + nombreTipo(der) + ".";
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
        return "(" + izquierda + " " + operador + " " + derecha + ")";
    }
}