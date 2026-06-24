package com.mycompany.golite.ast;

import java.util.List;

/**
 * Declaración de una función: nombre, parámetros, tipo de retorno y cuerpo.
 * Al ejecutarse solo se registra en el entorno; su cuerpo corre cuando se la llama.
 */
public class NodoFuncion extends Nodo {

    public String nombre;
    public List<NodoDeclVar> parametros;
    public String tipoRetorno;        // null si la función no retorna valor
    public NodoBloque cuerpo;

    // Receptor; solo lo usan los métodos de struct
    public String receptorNombre;     // null si es función normal
    public String receptorTipo;       // null si es función normal

    /** Constructor para funciones normales (sin receptor). */
    public NodoFuncion(String nombre, List parametros, String tipoRetorno,
                       NodoBloque cuerpo, int linea, int columna) {
        this(null, null, nombre, parametros, tipoRetorno, cuerpo, linea, columna);
    }

    /** Constructor general; con receptor != null es un método de struct. */
    public NodoFuncion(String receptorNombre, String receptorTipo, String nombre,
                       List parametros, String tipoRetorno, NodoBloque cuerpo,
                       int linea, int columna) {
        super(linea, columna);
        this.receptorNombre = receptorNombre;
        this.receptorTipo   = receptorTipo;
        this.nombre         = nombre;
        this.parametros     = parametros;
        this.tipoRetorno    = tipoRetorno;
        this.cuerpo         = cuerpo;
    }

    /** Se registra como método (si tiene receptor) o como función normal. */
    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        if (receptorTipo != null) {
            entorno.declararMetodo(receptorTipo, nombre, this);
        } else {
            entorno.declararFuncion(nombre, this);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Funcion(" + nombre + ", params=" + parametros
             + (tipoRetorno != null ? " : " + tipoRetorno : "") + ")";
    }
}
