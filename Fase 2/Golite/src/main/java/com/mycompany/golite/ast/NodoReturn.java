package com.mycompany.golite.ast;

/**
 * Sentencia return. Evalúa su expresión y emite una ReturnSignal,
 * que el bloque y la llamada propagan hacia arriba para terminar la función.
 */
public class NodoReturn extends Nodo {

    public Nodo expresion;   // null si el return no lleva valor

    public NodoReturn(Nodo expresion, int linea, int columna) {
        super(linea, columna);
        this.expresion = expresion;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        Object valor = (expresion != null) ? expresion.ejecutar(entorno) : null;
        return new com.mycompany.golite.Entorno.ReturnSignal(valor);
    }

    @Override
    public String toString() {
        return "Return(" + expresion + ")";
    }
}
