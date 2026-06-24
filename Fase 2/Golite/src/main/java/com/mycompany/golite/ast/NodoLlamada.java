package com.mycompany.golite.ast;

import java.util.List;

/**
 * Llamada a una función definida por el usuario: nombre + argumentos.
 * Crea un scope nuevo, enlaza los parámetros con
 * los argumentos evaluados y ejecuta el cuerpo, devolviendo el valor del return.
 */
public class NodoLlamada extends Nodo {

    public String nombre;
    public List<Nodo> argumentos;

    public NodoLlamada(String nombre, List argumentos, int linea, int columna) {
        super(linea, columna);
        this.nombre     = nombre;
        this.argumentos = argumentos;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        Object fObj = entorno.obtenerFuncion(nombre);
        if (fObj == null) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": La función '" + nombre + "' no está declarada.");
        }
        NodoFuncion f = (NodoFuncion) fObj;

        // Verificar cantidad de argumentos
        if (argumentos.size() != f.parametros.size()) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": La función '" + nombre + "' espera " + f.parametros.size()
                + " argumento(s), se recibieron " + argumentos.size() + ".");
        }

        // Scope de la función: hijo del global, estilo Go
        com.mycompany.golite.Entorno global    = entorno.raiz();
        com.mycompany.golite.Entorno scopeFunc = new com.mycompany.golite.Entorno(global);

        // Enlazar cada parámetro con su argumento
        for (int i = 0; i < f.parametros.size(); i++) {
            NodoDeclVar p = f.parametros.get(i);
            Object val    = argumentos.get(i).ejecutar(entorno);
            scopeFunc.declarar(p.nombre, p.tipo, val);
        }

        // Ejecutar el cuerpo; si hubo return, devolver su valor
        Object resultado = f.cuerpo.ejecutar(scopeFunc);
        if (resultado instanceof com.mycompany.golite.Entorno.ReturnSignal) {
            return ((com.mycompany.golite.Entorno.ReturnSignal) resultado).valor;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Llamada(" + nombre + ", " + argumentos + ")";
    }
}
