package com.mycompany.golite.ast;

import java.util.List;

/**
 * Acceso por índice: s[i]. Como base es una expresión, también soporta
 * accesos encadenados tipo m[i][j] (matrices) en pasos posteriores.
 */
public class NodoAccesoIndice extends Nodo {

    public Nodo base;
    public Nodo indice;

    public NodoAccesoIndice(Nodo base, Nodo indice, int linea, int columna) {
        super(linea, columna);
        this.base   = base;
        this.indice = indice;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        Object col = base.ejecutar(entorno);
        Object idxObj = indice.ejecutar(entorno);

        if (!(idxObj instanceof Integer)) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": el índice debe ser int.");
        }
        int idx = (Integer) idxObj;

        if (col instanceof List) {
            List<?> lista = (List<?>) col;
            if (idx < 0 || idx >= lista.size()) {
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": índice " + idx + " fuera de rango (tamaño " + lista.size() + ").");
            }
            return lista.get(idx);
        }

        if (col instanceof String) {
            String s = (String) col;
            if (idx < 0 || idx >= s.length()) {
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": índice " + idx + " fuera de rango (longitud " + s.length() + ").");
            }
            return String.valueOf(s.charAt(idx));
        }

        throw new RuntimeException(
            "[Error Semántico] Línea " + linea + ", Columna " + columna
            + ": el valor no es indexable (slice o string).");
    }

    @Override
    public String toString() {
        return base + "[" + indice + "]";
    }
}
