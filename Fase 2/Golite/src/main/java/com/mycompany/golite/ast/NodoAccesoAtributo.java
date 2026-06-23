package com.mycompany.golite.ast;

/**
 * Acceso a un atributo de struct: e.Nombre. Como base es una expresión,
 * soporta acceso anidado tipo e.Notas.Nota.
 */
public class NodoAccesoAtributo extends Nodo {

    public Nodo base;
    public String campo;

    public NodoAccesoAtributo(Nodo base, String campo, int linea, int columna) {
        super(linea, columna);
        this.base  = base;
        this.campo = campo;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        Object obj = base.ejecutar(entorno);
        if (!(obj instanceof InstanciaStruct)) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": no se puede acceder a '." + campo + "' porque el valor no es un struct.");
        }
        InstanciaStruct inst = (InstanciaStruct) obj;
        if (!inst.campos.containsKey(campo)) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": el struct '" + inst.tipo + "' no tiene el campo '" + campo + "'.");
        }
        return inst.campos.get(campo);
    }

    @Override
    public String toString() {
        return base + "." + campo;
    }
}
