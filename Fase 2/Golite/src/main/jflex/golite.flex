package com.mycompany.golite;
import com.mycompany.golite.parser.sym;
import java_cup.runtime.Symbol;
import java.util.ArrayList;
import java.util.List;

%%

%cup
%class GoliteLexer
%public
%unicode
%line
%column

%{
    // ─── Listas para reportes ──────────────────────────────────────────
    public static List<TokenReporte> listaTokens  = new ArrayList<>();
    public static List<ErrorLexico>  listaErrores = new ArrayList<>();

    // Cuando es false, el lexer NO registra tokens ni errores en las listas.
    // Se usa para la pasada del parser, evitando duplicar el reporte léxico.
    public static boolean registrar = true;

    /**
     * Crea el Symbol para CUP, registra el token en la lista de reporte
     * y lo retorna. Usa yyline+1 / yycolumn+1 para base-1.
     */
    private Symbol token(int tipo, Object valor) {
        int lin = yyline + 1;
        int col = yycolumn + 1;
        if (registrar) {
            listaTokens.add(new TokenReporte(yytext(), SymUtils.nombreTipo(tipo), lin, col));
        }
        return new Symbol(tipo, lin, col, valor);
    }

    private Symbol token(int tipo) {
        return token(tipo, yytext());
    }

    private void errorLexico() {
        int lin = yyline + 1;
        int col = yycolumn + 1;
        String msg = "El símbolo \"" + yytext() + "\" no es aceptado en el lenguaje.";
        if (registrar) {
            listaErrores.add(new ErrorLexico(msg, lin, col));
            Errores.agregarLexico(msg, lin, col);
        }
    }
%}

// ─── MACROS ───────────────────────────────────────────────────────────
digit        = [0-9]
letter       = [a-zA-Z_]
alphanumeric = [a-zA-Z0-9_]

// Secuencias de escape válidas en strings
escape_str   = \\[\"\\\nrt]
normal_str   = [^\"\\\n\r]
str_content  = ({normal_str} | {escape_str})*

// Literal rune: un solo carácter o secuencia de escape entre comillas simples
escape_rune  = \\['\\\nrt]
normal_rune  = [^'\\\n\r]
rune_content = ({normal_rune} | {escape_rune})

whitespace   = [ \r\t\f\n]+

%%

// ─────────────────────────────────────────────────────────────────────
// COMENTARIOS E IGNORADOS

// ─────────────────────────────────────────────────────────────────────
"//" [^\r\n]*                { /* ignorar */ }
"/*" ~"*/"                   { /* ignorar */ }
{whitespace}                 { /* ignorar */ }                

// ─────────────────────────────────────────────────────────────────────
// FUNCIONES EMBEBIDAS
// Deben ir ANTES que IDENTIFICADOR y ANTES que el operador punto "."
// porque contienen un punto en su lexema.
// ─────────────────────────────────────────────────────────────────────
"fmt.Println"                { return token(sym.IMPRIMIR);       }
"strconv.Atoi"               { return token(sym.A_ENTERO);          }
"strconv.ParseFloat"         { return token(sym.A_FLOTANTE);    }
"reflect.TypeOf"             { return token(sym.TIPO_DE);        }

// ─────────────────────────────────────────────────────────────────────
// PALABRAS RESERVADAS
// Deben ir ANTES que IDENTIFICADOR
// ─────────────────────────────────────────────────────────────────────
"var"                        { return token(sym.VARIABLE);           }
"func"                       { return token(sym.FUNCION);          }
"if"                         { return token(sym.SI);            }
"else"                       { return token(sym.SINO);          }
"for"                        { return token(sym.PARA);           }
"break"                      { return token(sym.ROMPER);         }
"continue"                   { return token(sym.CONTINUAR);      }
"return"                     { return token(sym.RETORNAR);        }
"nil"                        { return token(sym.NULO);           }
"switch"                     { return token(sym.SWITCH);         }
"case"                       { return token(sym.CASO);           }
"default"                    { return token(sym.DEFECTO);        }
"make"                       { return token(sym.MAKE);           }
"len"                        { return token(sym.LEN);            }
"append"                     { return token(sym.APPEND);         }

// Tipos de datos
"int"                        { return token(sym.TIPO_ENTERO);      }
"float64"                    { return token(sym.TIPO_FLOTANTE);    }
"string"                     { return token(sym.TIPO_CADENA);   }
"bool"                       { return token(sym.TIPO_BOOLEANO);     }
"rune"                       { return token(sym.TIPO_RUNA);     }

// Literales booleanos (son palabras reservadas en GoLite)
"true"                       { return token(sym.LITERAL_BOOLEANO, true);  }
"false"                      { return token(sym.LITERAL_BOOLEANO, false); }

// ─────────────────────────────────────────────────────────────────────
// LITERALES NUMÉRICOS
// DECIMAL antes que ENTERO: evita que "1.5" → INT(1) + PUNTO + INT(5)
// ─────────────────────────────────────────────────────────────────────
{digit}+ "." {digit}+        { return token(sym.LITERAL_FLOTANTE, Double.parseDouble(yytext())); }
{digit}+                     { return token(sym.LITERAL_ENTERO,   Integer.parseInt(yytext()));   }

// ─────────────────────────────────────────────────────────────────────
// LITERAL STRING
// ─────────────────────────────────────────────────────────────────────
\" {str_content} \"          {
                                 // Guardamos el texto sin las comillas externas
                                 String val = yytext().substring(1, yytext().length() - 1);
                                 return token(sym.LITERAL_CADENA, val);
                             }

// ─────────────────────────────────────────────────────────────────────
// LITERAL RUNE  (comilla simple)
// ─────────────────────────────────────────────────────────────────────
\' {rune_content} \'         { return token(sym.LITERAL_RUNA, yytext()); }

// ─────────────────────────────────────────────────────────────────────
// IDENTIFICADORES
// ─────────────────────────────────────────────────────────────────────
{letter} {alphanumeric}*     { return token(sym.IDENTIFICADOR, yytext()); }

// ─────────────────────────────────────────────────────────────────────
// OPERADORES
// Operadores de DOS caracteres ANTES que los de un carácter
// (":=" antes que ":", "+=" antes que "+", etc.)
// ─────────────────────────────────────────────────────────────────────

// Asignación
":="                         { return token(sym.DEFINIR);        }
"="                          { return token(sym.ASIGNAR);        }

// Asignación compuesta
"+="                         { return token(sym.MAS_ASIGNAR);   }
"-="                         { return token(sym.MENOS_ASIGNAR);  }

// Incremento / Decremento (necesarios para "i++" en el for)
"++"                         { return token(sym.INCREMENTO);           }
"--"                         { return token(sym.DECREMENTO);           }

// Aritméticos
"+"                          { return token(sym.MAS);          }
"-"                          { return token(sym.MENOS);         }
"*"                          { return token(sym.POR);         }
"/"                          { return token(sym.ENTRE);        }
"%"                          { return token(sym.MODULO);           }

// Comparación (dos chars antes que uno)
"=="                         { return token(sym.IGUAL);            }
"!="                         { return token(sym.DIFERENTE);            }
"<="                         { return token(sym.MENOR_IGUAL);            }
">="                         { return token(sym.MAYOR_IGUAL);            }
"<"                          { return token(sym.MENOR);            }
">"                          { return token(sym.MAYOR);            }

// Lógicos
"&&"                         { return token(sym.Y);           }
"||"                         { return token(sym.O);            }
"!"                          { return token(sym.NO);           }

// ─────────────────────────────────────────────────────────────────────
// DELIMITADORES
// ─────────────────────────────────────────────────────────────────────
"("                          { return token(sym.PAR_IZQ);        }
")"                          { return token(sym.PAR_DER);        }
"{"                          { return token(sym.LLAVE_IZQ);        }
"}"                          { return token(sym.LLAVE_DER);        }
"["                          { return token(sym.CORCHETE_IZQ);     }
"]"                          { return token(sym.CORCHETE_DER);     }
","                          { return token(sym.COMA);         }
";"                          { return token(sym.PUNTO_COMA);     }
":"                          { return token(sym.DOS_PUNTOS);     }
"."                          { return token(sym.PUNTO);           }

// ─────────────────────────────────────────────────────────────────────
// ERROR LÉXICO — debe ser la ÚLTIMA regla
// Captura cualquier carácter no reconocido por las reglas anteriores
// ─────────────────────────────────────────────────────────────────────
[^]                          { errorLexico(); }
