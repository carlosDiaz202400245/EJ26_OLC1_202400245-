package com.mycompany.golite.ast;

/** Asignación a un atributo de struct: e.Nombre = valor (soporta e.Notas.Nota = x). */
public class NodoAsignacionAtributo extends Nodo {

    public Nodo base;
    public String campo;
    public Nodo valor;

    public NodoAsignacionAtributo(Nodo base, String campo, Nodo valor, int linea, int columna) {
        super(linea, columna);
        this.base  = base;
        this.campo = campo;
        this.valor = valor;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        Object obj = base.ejecutar(entorno);
        if (!(obj instanceof InstanciaStruct)) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": no se puede asignar a '." + campo + "' porque el valor no es un struct.");
        }
        InstanciaStruct inst = (InstanciaStruct) obj;
        if (!inst.campos.containsKey(campo)) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": el struct '" + inst.tipo + "' no tiene el campo '" + campo + "'.");
        }
        inst.campos.put(campo, valor.ejecutar(entorno));
        return null;
    }

    @Override
    public String toString() {
        return base + "." + campo + " = " + valor;
    }
}
