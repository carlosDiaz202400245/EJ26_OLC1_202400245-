package com.mycompany.golite.ast;

import java.util.List;

/**
 * strings.Join(s, sep): une los elementos de un slice de strings usando sep
 * como separador y devuelve un solo string.
 */
public class NodoStringsJoin extends Nodo {

    public Nodo slice;
    public Nodo separador;

    public NodoStringsJoin(Nodo slice, Nodo separador, int linea, int columna) {
        super(linea, columna);
        this.slice     = slice;
        this.separador = separador;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        Object s   = slice.ejecutar(entorno);
        Object sep = separador.ejecutar(entorno);

        if (!(s instanceof List)) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": strings.Join requiere un slice de strings.");
        }
        if (!(sep instanceof String)) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": el separador de strings.Join debe ser string.");
        }

        List<?> lista = (List<?>) s;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lista.size(); i++) {
            if (i > 0) sb.append((String) sep);
            Object e = lista.get(i);
            if (!(e instanceof String)) {
                throw new RuntimeException(
                    "[Error Semántico] Línea " + linea + ", Columna " + columna
                    + ": strings.Join solo une slices de strings.");
            }
            sb.append((String) e);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "strings.Join(" + slice + ", " + separador + ")";
    }
}
