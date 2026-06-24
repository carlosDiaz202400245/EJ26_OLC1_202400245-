package com.mycompany.golite.ast;

import java.util.List;

/** Recorre el AST y genera su representación en formato DOT. */
public class GeneradorAST {

    private final StringBuilder sb = new StringBuilder();
    private int contador = 0;

    /** Devuelve el código DOT del árbol a partir de la lista de nodos raíz. */
    public String generar(List<Nodo> raices) {
        sb.setLength(0);
        contador = 0;
        sb.append("digraph AST {\n");
        sb.append("  node [shape=box, fontname=\"Consolas\", fontsize=10];\n");

        int raiz = nuevo("Programa");
        for (Nodo n : raices) {
            if (n != null) arista(raiz, nodo(n));
        }
        sb.append("}\n");
        return sb.toString();
    }

    /** Emite un nodo Nodo y sus hijos; devuelve su id. */
    private int nodo(Nodo n) {
        if (n instanceof NodoLiteral) {
            NodoLiteral x = (NodoLiteral) n;
            return nuevo(x.valor == null ? "nil" : x.tipo + ": " + x.valor);
        }
        if (n instanceof NodoIdentificador) {
            return nuevo("id: " + ((NodoIdentificador) n).nombre);
        }
        if (n instanceof NodoIncDec) {
            NodoIncDec x = (NodoIncDec) n;
            return nuevo(x.nombre + x.operador);
        }
        if (n instanceof NodoBinario) {
            NodoBinario x = (NodoBinario) n;
            int id = nuevo(x.operador);
            arista(id, nodo(x.izquierda));
            arista(id, nodo(x.derecha));
            return id;
        }
        if (n instanceof NodoUnario) {
            NodoUnario x = (NodoUnario) n;
            int id = nuevo("unario " + x.operador);
            arista(id, nodo(x.expresion));
            return id;
        }
        if (n instanceof NodoDeclVar) {
            NodoDeclVar x = (NodoDeclVar) n;
            int id = nuevo("var " + x.nombre + (x.tipo != null ? " : " + x.tipo : ""));
            if (x.expresion != null) arista(id, nodo(x.expresion));
            return id;
        }
        if (n instanceof NodoAsignacion) {
            NodoAsignacion x = (NodoAsignacion) n;
            int id = nuevo(x.nombre + " " + x.operador);
            arista(id, nodo(x.expresion));
            return id;
        }
        if (n instanceof NodoAsignacionIndice) {
            NodoAsignacionIndice x = (NodoAsignacionIndice) n;
            int id = nuevo("[]=");
            arista(id, nodo(x.base));
            arista(id, nodo(x.indice));
            arista(id, nodo(x.valor));
            return id;
        }
        if (n instanceof NodoAsignacionAtributo) {
            NodoAsignacionAtributo x = (NodoAsignacionAtributo) n;
            int id = nuevo("." + x.campo + " =");
            arista(id, nodo(x.base));
            arista(id, nodo(x.valor));
            return id;
        }
        if (n instanceof NodoIf) {
            NodoIf x = (NodoIf) n;
            int id = nuevo("if");
            arista(id, nodo(x.condicion));
            arista(id, nodo(x.bloqueThen));
            if (x.bloqueElse != null) arista(id, nodo(x.bloqueElse));
            return id;
        }
        if (n instanceof NodoSwitch) {
            NodoSwitch x = (NodoSwitch) n;
            int id = nuevo("switch");
            if (x.expresion != null) arista(id, nodo(x.expresion));
            for (NodoCaso c : x.casos) arista(id, caso(c));
            return id;
        }
        if (n instanceof Nodofor) {
            Nodofor x = (Nodofor) n;
            int id = nuevo("for");
            if (x.init != null)      arista(id, nodo(x.init));
            if (x.condicion != null) arista(id, nodo(x.condicion));
            if (x.post != null)      arista(id, nodo(x.post));
            arista(id, nodo(x.bloque));
            return id;
        }
        if (n instanceof NodoBloque) {
            int id = nuevo("bloque");
            for (Nodo s : ((NodoBloque) n).sentencias) {
                if (s != null) arista(id, nodo(s));
            }
            return id;
        }
        if (n instanceof NodoReturn) {
            NodoReturn x = (NodoReturn) n;
            int id = nuevo("return");
            if (x.expresion != null) arista(id, nodo(x.expresion));
            return id;
        }
        if (n instanceof NodoBreak)    return nuevo("break");
        if (n instanceof NodoContinue) return nuevo("continue");
        if (n instanceof NodoPrintln) {
            int id = nuevo("fmt.Println");
            for (Nodo a : ((NodoPrintln) n).argumentos) arista(id, nodo(a));
            return id;
        }
        if (n instanceof NodoAtoi) {
            int id = nuevo("strconv.Atoi");
            arista(id, nodo(((NodoAtoi) n).expresion));
            return id;
        }
        if (n instanceof NodoParseFloat) {
            int id = nuevo("strconv.ParseFloat");
            arista(id, nodo(((NodoParseFloat) n).expresion));
            return id;
        }
        if (n instanceof NodoTypeOf) {
            int id = nuevo("reflect.TypeOf");
            arista(id, nodo(((NodoTypeOf) n).expresion));
            return id;
        }
        if (n instanceof NodoLen) {
            int id = nuevo("len");
            arista(id, nodo(((NodoLen) n).expresion));
            return id;
        }
        if (n instanceof NodoAppend) {
            int id = nuevo("append");
            for (Nodo a : ((NodoAppend) n).argumentos) arista(id, nodo(a));
            return id;
        }
        if (n instanceof NodoSlicesIndex) {
            NodoSlicesIndex x = (NodoSlicesIndex) n;
            int id = nuevo("slices.Index");
            arista(id, nodo(x.slice));
            arista(id, nodo(x.valor));
            return id;
        }
        if (n instanceof NodoStringsJoin) {
            NodoStringsJoin x = (NodoStringsJoin) n;
            int id = nuevo("strings.Join");
            arista(id, nodo(x.slice));
            arista(id, nodo(x.separador));
            return id;
        }
        if (n instanceof NodoLlamada) {
            NodoLlamada x = (NodoLlamada) n;
            int id = nuevo("call " + x.nombre);
            for (Nodo a : x.argumentos) arista(id, nodo(a));
            return id;
        }
        if (n instanceof NodoLlamadaMetodo) {
            NodoLlamadaMetodo x = (NodoLlamadaMetodo) n;
            int id = nuevo("call ." + x.nombre + "()");
            arista(id, nodo(x.base));
            for (Nodo a : x.argumentos) arista(id, nodo(a));
            return id;
        }
        if (n instanceof NodoFuncion) {
            NodoFuncion x = (NodoFuncion) n;
            String etq = (x.receptorTipo != null ? "metodo (" + x.receptorTipo + ") " : "func ") + x.nombre;
            int id = nuevo(etq);
            for (NodoDeclVar p : x.parametros) arista(id, nodo(p));
            arista(id, nodo(x.cuerpo));
            return id;
        }
        if (n instanceof NodoStruct) {
            NodoStruct x = (NodoStruct) n;
            int id = nuevo("struct " + x.nombre);
            for (NodoDeclVar c : x.campos) {
                arista(id, nuevo(c.tipo + " " + c.nombre));
            }
            return id;
        }
        if (n instanceof NodoSliceLiteral) {
            NodoSliceLiteral x = (NodoSliceLiteral) n;
            int id = nuevo("[]" + (x.tipoElemento != null ? x.tipoElemento : "") + "{}");
            for (Nodo e : x.elementos) arista(id, nodo(e));
            return id;
        }
        if (n instanceof NodoMake) {
            NodoMake x = (NodoMake) n;
            int id = nuevo("make []" + x.tipoElemento);
            arista(id, nodo(x.tamano));
            return id;
        }
        if (n instanceof NodoAccesoIndice) {
            NodoAccesoIndice x = (NodoAccesoIndice) n;
            int id = nuevo("indice []");
            arista(id, nodo(x.base));
            arista(id, nodo(x.indice));
            return id;
        }
        if (n instanceof NodoAccesoAtributo) {
            NodoAccesoAtributo x = (NodoAccesoAtributo) n;
            int id = nuevo("." + x.campo);
            arista(id, nodo(x.base));
            return id;
        }
        // Fallback genérico
        return nuevo(n.getClass().getSimpleName());
    }

    /** Emite un case/default y su cuerpo. */
    private int caso(NodoCaso c) {
        int id = nuevo(c.esDefault ? "default" : "case");
        for (Nodo v : c.valores) arista(id, nodo(v));
        for (Nodo s : c.cuerpo)  if (s != null) arista(id, nodo(s));
        return id;
    }

    private int nuevo(String etiqueta) {
        int id = contador++;
        sb.append("  n").append(id)
          .append(" [label=\"").append(esc(etiqueta)).append("\"];\n");
        return id;
    }

    private void arista(int a, int b) {
        sb.append("  n").append(a).append(" -> n").append(b).append(";\n");
    }

    private String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ");
    }
}
