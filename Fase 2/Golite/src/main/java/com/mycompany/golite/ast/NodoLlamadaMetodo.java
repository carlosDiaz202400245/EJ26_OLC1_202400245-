package com.mycompany.golite.ast;

import java.util.List;

/**
 * Llamada a un método de struct: instancia.Metodo(args). Enlaza el receptor
 * (por referencia, para que el método pueda mutar la instancia) y los
 * parámetros, ejecuta el cuerpo y devuelve el valor del return.
 */
public class NodoLlamadaMetodo extends Nodo {

    public Nodo base;            // expresión que da la instancia receptora
    public String nombre;        // nombre del método
    public List<Nodo> argumentos;

    public NodoLlamadaMetodo(Nodo base, String nombre, List argumentos, int linea, int columna) {
        super(linea, columna);
        this.base       = base;
        this.nombre     = nombre;
        this.argumentos = argumentos;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        Object recv = base.ejecutar(entorno);
        if (!(recv instanceof InstanciaStruct)) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": no se puede llamar al método '" + nombre + "' porque el valor no es un struct.");
        }
        InstanciaStruct inst = (InstanciaStruct) recv;

        Object mObj = entorno.obtenerMetodo(inst.tipo, nombre);
        if (mObj == null) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": el struct '" + inst.tipo + "' no tiene el método '" + nombre + "'.");
        }
        NodoFuncion m = (NodoFuncion) mObj;

        if (argumentos.size() != m.parametros.size()) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": el método '" + nombre + "' espera " + m.parametros.size()
                + " argumento(s), se recibieron " + argumentos.size() + ".");
        }

        // Scope del método: hijo del global. El receptor se enlaza por referencia.
        com.mycompany.golite.Entorno global    = entorno.raiz();
        com.mycompany.golite.Entorno scopeMetodo = new com.mycompany.golite.Entorno(global);
        scopeMetodo.declarar(m.receptorNombre, inst.tipo, inst);

        for (int i = 0; i < m.parametros.size(); i++) {
            NodoDeclVar p = m.parametros.get(i);
            Object val    = argumentos.get(i).ejecutar(entorno);
            scopeMetodo.declarar(p.nombre, p.tipo, val);
        }

        Object resultado = m.cuerpo.ejecutar(scopeMetodo);
        if (resultado instanceof com.mycompany.golite.Entorno.ReturnSignal) {
            return ((com.mycompany.golite.Entorno.ReturnSignal) resultado).valor;
        }
        return null;
    }

    @Override
    public String toString() {
        return base + "." + nombre + "(" + argumentos + ")";
    }
}
