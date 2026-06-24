package com.mycompany.golite.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.mycompany.golite.Entorno;

/**
 * Definición de un struct: nombre y lista de campos como NodoDeclVar.
 * Al ejecutarse se registra en el entorno.
 */
public class NodoStruct extends Nodo {

    public String nombre;
    public List<NodoDeclVar> campos;

    public NodoStruct(String nombre, List campos, int linea, int columna) {
        super(linea, columna);
        this.nombre = nombre;
        this.campos = campos;
    }

    /** Registra la definición del struct en el entorno. */
    @Override
    public Object ejecutar(Entorno entorno) {
        entorno.declararStruct(nombre, this);
        return null;
    }

    /**
     * Crea una instancia con todos los campos en su valor cero. Los campos de
     * tipo struct se instancian recursivamente; si hay un ciclo, es decir un
     * struct que se contiene a sí mismo, ese campo queda en nil.
     */
    public InstanciaStruct nuevaInstanciaCero(Entorno entorno, Set<String> enConstruccion) {
        InstanciaStruct inst = new InstanciaStruct(nombre);
        enConstruccion.add(nombre);
        for (NodoDeclVar campo : campos) {
            inst.campos.put(campo.nombre, valorCero(campo.tipo, entorno, enConstruccion));
        }
        enConstruccion.remove(nombre);
        return inst;
    }

    private Object valorCero(String tipo, Entorno entorno, Set<String> enConstruccion) {
        if (tipo == null) return null;
        switch (tipo) {
            case "int":     return 0;
            case "float64": return 0.0;
            case "string":  return "";
            case "bool":    return false;
            case "rune":    return 0;
        }
        if (tipo.startsWith("[")) return new ArrayList<>();   // slice → vacío

        // ¿Es un struct?
        Object def = entorno.obtenerStruct(tipo);
        if (def instanceof NodoStruct) {
            if (enConstruccion.contains(tipo)) return null;   // ciclo → nil
            return ((NodoStruct) def).nuevaInstanciaCero(entorno, enConstruccion);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Struct(" + nombre + ", campos=" + campos + ")";
    }
}
