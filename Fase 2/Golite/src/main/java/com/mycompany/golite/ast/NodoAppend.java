package com.mycompany.golite.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Función append: append(slice, e1, e2, ...). Devuelve un slice NUEVO con los
 * elementos agregados al final; el original no se modifica, como en Go.
 */
public class NodoAppend extends Nodo {

    public List<Nodo> argumentos;   // [0] = slice; resto = elementos a agregar

    public NodoAppend(List argumentos, int linea, int columna) {
        super(linea, columna);
        this.argumentos = argumentos;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        if (argumentos.size() < 2) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": append requiere un slice y al menos un elemento.");
        }

        Object base = argumentos.get(0).ejecutar(entorno);
        List<Object> nuevo = new ArrayList<>();

        if (base instanceof List) {
            nuevo.addAll((List<?>) base);
        } else if (base != null) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": el primer argumento de append debe ser un slice.");
        }
        // un slice nil se trata como vacío

        for (int i = 1; i < argumentos.size(); i++) {
            nuevo.add(argumentos.get(i).ejecutar(entorno));
        }
        return nuevo;
    }

    @Override
    public String toString() {
        return "append(" + argumentos + ")";
    }
}
