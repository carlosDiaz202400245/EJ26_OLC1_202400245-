package com.mycompany.golite.ast;

import java.util.List;

/**
 * Un case o default dentro de un switch: los valores con los que compara
 * y el cuerpo de sentencias que ejecuta. Si esDefault es true, no usa valores.
 */
public class NodoCaso {

    public List<Nodo> valores;   // vacío cuando es default
    public List<Nodo> cuerpo;    // sentencias del case
    public boolean esDefault;

    public NodoCaso(List valores, List cuerpo, boolean esDefault) {
        this.valores   = valores;
        this.cuerpo    = cuerpo;
        this.esDefault = esDefault;
    }

    @Override
    public String toString() {
        return (esDefault ? "default" : "case " + valores) + " -> " + cuerpo;
    }
}
