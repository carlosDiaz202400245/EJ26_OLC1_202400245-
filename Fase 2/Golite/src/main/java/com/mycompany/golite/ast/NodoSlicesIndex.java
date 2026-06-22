package com.mycompany.golite.ast;

import java.util.List;

/**
 * slices.Index(s, x): devuelve la posición de la primera aparición de x en el
 * slice s, o -1 si no está.
 */
public class NodoSlicesIndex extends Nodo {

    public Nodo slice;
    public Nodo valor;

    public NodoSlicesIndex(Nodo slice, Nodo valor, int linea, int columna) {
        super(linea, columna);
        this.slice = slice;
        this.valor = valor;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        Object s = slice.ejecutar(entorno);
        Object x = valor.ejecutar(entorno);

        if (!(s instanceof List)) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": slices.Index requiere un slice como primer argumento.");
        }
        List<?> lista = (List<?>) s;
        for (int i = 0; i < lista.size(); i++) {
            if (sonIguales(lista.get(i), x)) return i;
        }
        return -1;
    }

    private boolean sonIguales(Object a, Object b) {
        if (a == null || b == null) return a == b;
        if (esNumerico(a) && esNumerico(b)) return toDouble(a) == toDouble(b);
        return a.equals(b);
    }

    private boolean esNumerico(Object v) { return v instanceof Integer || v instanceof Double; }

    private double toDouble(Object v) {
        return (v instanceof Integer) ? ((Integer) v).doubleValue() : (Double) v;
    }

    @Override
    public String toString() {
        return "slices.Index(" + slice + ", " + valor + ")";
    }
}
