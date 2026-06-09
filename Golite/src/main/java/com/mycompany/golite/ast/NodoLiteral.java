package com.mycompany.golite.ast;

/**
 * Nodo que representa un valor  en el código fuente
 
 * Tipos posibles:
 *   "int"     = valor es Integer
 *   "float64" = valor es Double
 *   "string"  = valor es String
 *   "bool"    = valor es Boolean
 *   "rune"    = valor es String que va en comillas simples
 *   "nil"     = valor es null
 */
public class NodoLiteral extends Nodo {

    /** Valor real del literal */
    public Object valor;

    /** Tipo del literal: "int", "float64", "string", "bool", "rune", "nil" */
    public String tipo;

    /**
     * @param valor   valor del literal enteros, string, bools etc
     * @param tipo    tipo del literal como string
     * @param linea   línea en el código fuente
     * @param columna columna en el código fuente
     */
    public NodoLiteral(Object valor, String tipo, int linea, int columna) {
        super(linea, columna);
        this.valor = valor;
        this.tipo  = tipo;
    }

    /**
     * Evaluar un literal simplemente devuelve su valor.
     * No necesita el entorno porque no accede a variables.
     */
    @Override
   // public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        return valor;
    }

    @Override
    public String toString() {
        if (valor == null) return "nil";
        return "(" + tipo + ") " + valor;
    }
}