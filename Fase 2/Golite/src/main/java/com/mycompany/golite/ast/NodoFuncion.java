package com.mycompany.golite.ast;

import java.util.List;

/**
 * Declaración de una función: nombre, parámetros, tipo de retorno (o null) y cuerpo.
 * Al ejecutarse solo se registra en el entorno; su cuerpo corre cuando se la llama.
 */
public class NodoFuncion extends Nodo {

    public String nombre;
    public List<NodoDeclVar> parametros;
    public String tipoRetorno;        // null si la función no retorna valor
    public NodoBloque cuerpo;

    public NodoFuncion(String nombre, List parametros, String tipoRetorno,
                       NodoBloque cuerpo, int linea, int columna) {
        super(linea, columna);
        this.nombre      = nombre;
        this.parametros  = parametros;
        this.tipoRetorno = tipoRetorno;
        this.cuerpo      = cuerpo;
    }

    /** Registra la función en el entorno; no ejecuta su cuerpo. */
    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        entorno.declararFuncion(nombre, this);
        return null;
    }

    @Override
    public String toString() {
        return "Funcion(" + nombre + ", params=" + parametros
             + (tipoRetorno != null ? " : " + tipoRetorno : "") + ")";
    }
}
