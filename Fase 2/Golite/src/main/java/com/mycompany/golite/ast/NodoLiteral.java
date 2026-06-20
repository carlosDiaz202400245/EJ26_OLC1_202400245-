package com.mycompany.golite.ast;

/**
 * Nodo de un valor literal del código fuente. El tipo es uno de:
 * "int", "float64", "string", "bool", "rune" o "nil".
 */
public class NodoLiteral extends Nodo {

    public Object valor;

    /** Tipo del literal: "int", "float64", "string", "bool", "rune", "nil". */
    public String tipo;

    public NodoLiteral(Object valor, String tipo, int linea, int columna) {
        super(linea, columna);
        this.valor = valor;
        this.tipo  = tipo;
    }

    /** Evaluar un literal devuelve su valor (no usa el entorno). */
    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        return valor;
    }

    @Override
    public String toString() {
        if (valor == null) return "nil";
        return "(" + tipo + ") " + valor;
    }
}