package com.mycompany.golite.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Literal de slice: []int{1, 2, 3}. En tiempo de ejecución un slice se
 * representa como un ArrayList<Object>.
 */
public class NodoSliceLiteral extends Nodo {

    public String tipoElemento;      // p.ej. "int" o "[]int"
    public List<Nodo> elementos;

    public NodoSliceLiteral(String tipoElemento, List elementos, int linea, int columna) {
        super(linea, columna);
        this.tipoElemento = tipoElemento;
        this.elementos    = elementos;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        List<Object> slice = new ArrayList<>();
        for (Nodo e : elementos) {
            slice.add(e.ejecutar(entorno));
        }
        return slice;
    }

    @Override
    public String toString() {
        return "[]" + tipoElemento + elementos;
    }
}
