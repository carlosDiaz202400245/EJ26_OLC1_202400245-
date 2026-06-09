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

    /**
     * Crea el Symbol para CUP, registra el token en la lista de reporte
     * y lo retorna. Usa yyline+1 / yycolumn+1 para base-1.
     */
    private Symbol token(int tipo, Object valor) {
        int lin = yyline + 1;
        int col = yycolumn + 1;
        listaTokens.add(new TokenReporte(yytext(), SymUtils.nombreTipo(tipo), lin, col));
        return new Symbol(tipo, lin, col, valor);
    }

    private Symbol token(int tipo) {
        return token(tipo, yytext());
    }

    private void errorLexico() {
    int lin = yyline + 1;
    int col = yycolumn + 1;
    String msg = "El símbolo \"" + yytext() + "\" no es aceptado en el lenguaje.";
    listaErrores.add(new ErrorLexico(msg, lin, col));  // ← lista original intacta
    Errores.agregarLexico(msg, lin, col);              // ← también va a Errores
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
"//" [^\r\n]*               
"/*" ~"*/"                   
{whitespace}                 

// ─────────────────────────────────────────────────────────────────────
// FUNCIONES EMBEBIDAS
// Deben ir ANTES que IDENTIFICADOR y ANTES que el operador punto "."
// porque contienen un punto en su lexema.
// ─────────────────────────────────────────────────────────────────────
"fmt.Println"                { return token(sym.PRINTLN);       }
"strconv.Atoi"               { return token(sym.ATOI);          }
"strconv.ParseFloat"         { return token(sym.PARSEFLOAT);    }
"reflect.TypeOf"             { return token(sym.TYPEOF);        }

// ─────────────────────────────────────────────────────────────────────
// PALABRAS RESERVADAS
// Deben ir ANTES que IDENTIFICADOR
// ─────────────────────────────────────────────────────────────────────
"var"                        { return token(sym.VAR);           }
"func"                       { return token(sym.FUNC);          }
"if"                         { return token(sym.IF);            }
"else"                       { return token(sym.ELSE);          }
"for"                        { return token(sym.FOR);           }
"break"                      { return token(sym.BREAK);         }
"continue"                   { return token(sym.CONTINUE);      }
"return"                     { return token(sym.RETURN);        }
"nil"                        { return token(sym.NIL);           }

// Tipos de datos
"int"                        { return token(sym.INT_TYPE);      }
"float64"                    { return token(sym.FLOAT_TYPE);    }
"string"                     { return token(sym.STRING_TYPE);   }
"bool"                       { return token(sym.BOOL_TYPE);     }
"rune"                       { return token(sym.RUNE_TYPE);     }

// Literales booleanos (son palabras reservadas en GoLite)
"true"                       { return token(sym.BOOL_LITERAL, true);  }
"false"                      { return token(sym.BOOL_LITERAL, false); }

// ─────────────────────────────────────────────────────────────────────
// LITERALES NUMÉRICOS
// DECIMAL antes que ENTERO: evita que "1.5" → INT(1) + DOT + INT(5)
// ─────────────────────────────────────────────────────────────────────
{digit}+ "." {digit}+        { return token(sym.FLOAT_LITERAL, Double.parseDouble(yytext())); }
{digit}+                     { return token(sym.INT_LITERAL,   Integer.parseInt(yytext()));   }

// ─────────────────────────────────────────────────────────────────────
// LITERAL STRING
// ─────────────────────────────────────────────────────────────────────
\" {str_content} \"          {
                                 // Guardamos el texto sin las comillas externas
                                 String val = yytext().substring(1, yytext().length() - 1);
                                 return token(sym.STRING_LITERAL, val);
                             }

// ─────────────────────────────────────────────────────────────────────
// LITERAL RUNE  (comilla simple)
// ─────────────────────────────────────────────────────────────────────
\' {rune_content} \'         { return token(sym.RUNE_LITERAL, yytext()); }

// ─────────────────────────────────────────────────────────────────────
// IDENTIFICADORES
// ─────────────────────────────────────────────────────────────────────
{letter} {alphanumeric}*     { return token(sym.IDENTIFIER, yytext()); }

// ─────────────────────────────────────────────────────────────────────
// OPERADORES
// Operadores de DOS caracteres ANTES que los de un carácter
// (":=" antes que ":", "+=" antes que "+", etc.)
// ─────────────────────────────────────────────────────────────────────

// Asignación
":="                         { return token(sym.DEFINE);        }
"="                          { return token(sym.ASSIGN);        }

// Asignación compuesta
"+="                         { return token(sym.PLUS_ASSIGN);   }
"-="                         { return token(sym.MINUS_ASSIGN);  }

// Incremento / Decremento (necesarios para "i++" en el for)
"++"                         { return token(sym.INC);           }
"--"                         { return token(sym.DEC);           }

// Aritméticos
"+"                          { return token(sym.PLUS);          }
"-"                          { return token(sym.MINUS);         }
"*"                          { return token(sym.TIMES);         }
"/"                          { return token(sym.DIVIDE);        }
"%"                          { return token(sym.MOD);           }

// Comparación (dos chars antes que uno)
"=="                         { return token(sym.EQ);            }
"!="                         { return token(sym.NE);            }
"<="                         { return token(sym.LE);            }
">="                         { return token(sym.GE);            }
"<"                          { return token(sym.LT);            }
">"                          { return token(sym.GT);            }

// Lógicos
"&&"                         { return token(sym.AND);           }
"||"                         { return token(sym.OR);            }
"!"                          { return token(sym.NOT);           }

// ─────────────────────────────────────────────────────────────────────
// DELIMITADORES
// ─────────────────────────────────────────────────────────────────────
"("                          { return token(sym.LPAREN);        }
")"                          { return token(sym.RPAREN);        }
"{"                          { return token(sym.LBRACE);        }
"}"                          { return token(sym.RBRACE);        }
"["                          { return token(sym.LBRACKET);      }
"]"                          { return token(sym.RBRACKET);      }
","                          { return token(sym.COMMA);         }
";"                          { return token(sym.SEMICOLON);     }
"."                          { return token(sym.DOT);           }

// ─────────────────────────────────────────────────────────────────────
// ERROR LÉXICO — debe ser la ÚLTIMA regla
// Captura cualquier carácter no reconocido por las reglas anteriores
// ─────────────────────────────────────────────────────────────────────
[^]                          { errorLexico(); }
