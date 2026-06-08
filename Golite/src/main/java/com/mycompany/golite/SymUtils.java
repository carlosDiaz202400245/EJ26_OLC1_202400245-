package com.mycompany.golite;

import com.mycompany.golite.parser.sym;

public final class SymUtils {
    private SymUtils() {
    }

    public static String nombreTipo(int tipo) {
        if (tipo >= 0 && tipo < sym.terminalNames.length) {
            return sym.terminalNames[tipo];
        }
        return "UNKNOWN(" + tipo + ")";
    }
}
