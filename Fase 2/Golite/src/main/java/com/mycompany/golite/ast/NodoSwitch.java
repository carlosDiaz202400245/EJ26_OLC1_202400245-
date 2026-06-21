package com.mycompany.golite.ast;

import java.util.List;
import com.mycompany.golite.Entorno;

/**
 * Sentencia switch, con o sin expresión. Sin fallthrough (como en Go):
 * ejecuta solo el primer case que coincide y luego corta.
 *   - Con expresión: cada case compara su(s) valor(es) contra la expresión.
 *   - Sin expresión: cada case es una condición booleana (estilo if-else).
 */
public class NodoSwitch extends Nodo {

    public Nodo expresion;          // null = switch sin expresión
    public List<NodoCaso> casos;

    public NodoSwitch(Nodo expresion, List casos, int linea, int columna) {
        super(linea, columna);
        this.expresion = expresion;
        this.casos     = casos;
    }

    @Override
    public Object ejecutar(Entorno entorno) {
        Entorno scope = new Entorno(entorno);   // scope propio del switch
        NodoCaso porDefecto = null;

        if (expresion != null) {
            // Switch con expresión: comparar cada valor de case contra ella
            Object val = expresion.ejecutar(scope);
            for (NodoCaso c : casos) {
                if (c.esDefault) { porDefecto = c; continue; }
                for (Nodo vexpr : c.valores) {
                    if (sonIguales(val, vexpr.ejecutar(scope))) {
                        return ejecutarCuerpo(c.cuerpo, scope);
                    }
                }
            }
        } else {
            // Switch sin expresión: cada case es una condición booleana
            for (NodoCaso c : casos) {
                if (c.esDefault) { porDefecto = c; continue; }
                for (Nodo cond : c.valores) {
                    Object b = cond.ejecutar(scope);
                    if (b instanceof Boolean && (Boolean) b) {
                        return ejecutarCuerpo(c.cuerpo, scope);
                    }
                }
            }
        }

        // Ningún case coincidió: ejecutar default si existe
        if (porDefecto != null) return ejecutarCuerpo(porDefecto.cuerpo, scope);
        return null;
    }

    /** Ejecuta el cuerpo de un case. break corta el switch; return/continue se propagan. */
    private Object ejecutarCuerpo(List<Nodo> cuerpo, Entorno entorno) {
        Entorno local = new Entorno(entorno);
        for (Nodo s : cuerpo) {
            if (s == null) continue;
            Object r = s.ejecutar(local);
            if (r instanceof Entorno.Senal) {
                if (r instanceof Entorno.BreakSignal) return null;
                return r;
            }
        }
        return null;
    }

    /** Igualdad estilo '==' con conversión numérica implícita. */
    private boolean sonIguales(Object a, Object b) {
        if (a == null || b == null) return a == b;
        if (esNumerico(a) && esNumerico(b)) return toDouble(a) == toDouble(b);
        return a.equals(b);
    }

    private boolean esNumerico(Object v) { return v instanceof Integer || v instanceof Double; }

    private double toDouble(Object v) {
        return (v instanceof Integer) ? ((Integer) v).doubleValue() : (Double) v;
    }

    @Override
    public String toString() {
        return "Switch(" + expresion + ", casos=" + casos.size() + ")";
    }
}
