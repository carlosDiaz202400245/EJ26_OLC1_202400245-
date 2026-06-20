package com.mycompany.golite.ast;

/**
 * Clase base abstracta para todos los nodos del AST.
 */
public abstract class Nodo {

    /** Línea en el código fuente  */
    public int linea;

    /** Columna en el código fuente  */
    public int columna;

    /**
     * todos los nodos deben llaamr a aca
     * @param linea   línea donde aparece el nodo
     * @param columna columna donde aparece el nodo
     */
    public Nodo(int linea, int columna) {
        this.linea   = linea;
        this.columna = columna;
    }

    /**
     * Método que el Interprete llamará para ejecutar/evaluar este nodo.
     * Cada subclase implementa su lógica, que para este proyecto salieron muchas
     *
     * @param entorno el entorno de ejecución actual 
     * @return el valor resultado de evaluar este nodo, o null si no retorna nada
     */
    public abstract Object ejecutar(com.mycompany.golite.Entorno entorno);

    /**
     * Representación en texto del nodo, útil para depuración.
     */
    @Override
    public abstract String toString();
}