package com.mycompany.golite.ast;

/**
 * Nodo que representa la declaración de una variable.
 * Valores por defecto: int=0, float64=0.0, string="", bool=false, rune=0, otros=nil.
 */
public class NodoDeclVar extends Nodo {

    public String nombre;
    public String tipo;

    /** Expresión del valor inicial; null si no se asigna. */
    public Nodo expresion;

    public NodoDeclVar(String nombre, String tipo, Nodo expresion,
                       int linea, int columna) {
        super(linea, columna);
        this.nombre    = nombre;
        this.tipo      = tipo;
        this.expresion = expresion;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {

        // No permitir redeclaración en el mismo scope
        if (entorno.existeEnScopeActual(nombre)) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": La variable '" + nombre + "' ya fue declarada en este ámbito."
            );
        }

        Object valor;

        if (expresion != null) {
            valor = expresion.ejecutar(entorno);
            // Con tipo explícito se valida; sin él se infiere del valor
            if (tipo != null) {
                valor = verificarYConvertir(valor);
            } else {
                tipo = inferirTipo(valor);
            }
        } else {
            // Si el tipo es un struct, crear una instancia con campos en valor cero
            Object def = (tipo != null) ? entorno.obtenerStruct(tipo) : null;
            if (def instanceof NodoStruct) {
                valor = ((NodoStruct) def).nuevaInstanciaCero(entorno, new java.util.HashSet<>());
            } else {
                valor = valorPorDefecto();   // sin expresión → valor por defecto
            }
        }

        entorno.declarar(nombre, tipo, valor);
        return null;   // las declaraciones no retornan valor
    }

    // ─── VERIFICAR COMPATIBILIDAD DE TIPOS ─────────────────────────────
    private Object verificarYConvertir(Object valor) {
        switch (tipo) {
            case "int":
                if (valor instanceof Integer) return valor;
                // No se permite asignar float64 a int
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": No se puede asignar un valor de tipo "
                    + nombreTipo(valor) + " a una variable de tipo int."
                );

            case "float64":
                if (valor instanceof Double)  return valor;
                // Conversión implícita int = float64
                if (valor instanceof Integer) return ((Integer) valor).doubleValue();
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": No se puede asignar un valor de tipo "
                    + nombreTipo(valor) + " a una variable de tipo float64."
                );

            case "string":
                if (valor instanceof String) return valor;
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": No se puede asignar un valor de tipo "
                    + nombreTipo(valor) + " a una variable de tipo string."
                );

            case "bool":
                if (valor instanceof Boolean) return valor;
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": No se puede asignar un valor de tipo "
                    + nombreTipo(valor) + " a una variable de tipo bool."
                );

            case "rune":
                if (valor instanceof String) return valor;
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": No se puede asignar un valor de tipo "
                    + nombreTipo(valor) + " a una variable de tipo rune."
                );

            default:
                // Tipo compuesto — se acepta nil o el valor directo
                return valor;
        }
    }

    // ─── INFERIR TIPO DEL VALOR ────────────────────────────────────────
    private String inferirTipo(Object valor) {
        if (valor instanceof Integer) return "int";
        if (valor instanceof Double)  return "float64";
        if (valor instanceof String)  return "string";
        if (valor instanceof Boolean) return "bool";
        if (valor == null)            return "nil";
        return valor.getClass().getSimpleName();
    }

    // ─── VALOR POR DEFECTO SEGÚN EL TIPO ───────────────────────────────
    private Object valorPorDefecto() {
        if (tipo == null) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": Se requiere un tipo o un valor inicial para declarar '"
                + nombre + "'."
            );
        }
        // Un slice ([]T) arranca como un slice vacío para que len/append funcionen
        if (tipo.startsWith("[")) {
            return new java.util.ArrayList<>();
        }
        switch (tipo) {
            case "int":     return 0;
            case "float64": return 0.0;
            case "string":  return "";
            case "bool":    return false;
            case "rune":    return 0;
            default:        return null; // tipos compuestos → nil
        }
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
        return "DeclVar(" + tipo + " " + nombre
             + (expresion != null ? " = " + expresion : "") + ")";
    }
}