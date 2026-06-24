# Gramática GoLite — Producciones BNF (gramática tipo 2 / libre de contexto)

> Actualizada para la Fase 2: funciones, return, switch-case, slices y matrices,
> structs con métodos, y recuperación de errores (modo pánico).

```bnf
<program>    ::= <decl_list>

<decl_list>  ::= <decl_list> <decl>
              |  ε

<decl>       ::= <func_decl>
              |  <struct_decl>
              |  <stmt>
              |  error                         /* modo pánico (nivel declaración) */

<tipo>       ::= INT_TYPE
              |  FLOAT_TYPE
              |  STRING_TYPE
              |  BOOL_TYPE
              |  RUNE_TYPE
              |  IDENTIFIER
              |  LBRACKET RBRACKET <tipo>      /* tipo slice: []T, [][]T, ... */

/* ── Funciones y métodos ─────────────────────────────────────────── */
<func_decl>      ::= FUNC IDENTIFIER LPAREN <param_list> RPAREN <bloque>
                  |  FUNC IDENTIFIER LPAREN <param_list> RPAREN <tipo> <bloque>
                  |  FUNC LPAREN IDENTIFIER <tipo> RPAREN IDENTIFIER LPAREN <param_list> RPAREN <bloque>
                  |  FUNC LPAREN IDENTIFIER <tipo> RPAREN IDENTIFIER LPAREN <param_list> RPAREN <tipo> <bloque>

<param_list>     ::= <param_list_ne>
                  |  ε

<param_list_ne>  ::= <param_list_ne> COMMA <param>
                  |  <param>

<param>          ::= IDENTIFIER <tipo>

/* ── Structs ─────────────────────────────────────────────────────── */
<struct_decl>    ::= STRUCT IDENTIFIER LBRACE <campo_list> RBRACE

<campo_list>     ::= <campo_list> <campo>
                  |  ε

<campo>          ::= <tipo> IDENTIFIER SEMICOLON

/* ── Bloques y sentencias ────────────────────────────────────────── */
<bloque>     ::= LBRACE <stmt_list> RBRACE

<stmt_list>  ::= <stmt_list> <stmt>
              |  ε

<stmt>       ::= <bloque>
              |  <var_decl>
              |  <asignacion>
              |  IDENTIFIER INC
              |  IDENTIFIER DEC
              |  <llamada_funcion>
              |  <llamada_metodo>
              |  <llamada_embebida>
              |  <if_stmt>
              |  <switch_stmt>
              |  <for_stmt>
              |  BREAK
              |  CONTINUE
              |  RETURN <expr>
              |  SEMICOLON
              |  error                         /* modo pánico (nivel sentencia) */

<var_decl>   ::= VAR IDENTIFIER <tipo>
              |  VAR IDENTIFIER <tipo> ASSIGN <expr>
              |  VAR IDENTIFIER ASSIGN <expr>
              |  IDENTIFIER DEFINE <expr>

<asignacion> ::= IDENTIFIER ASSIGN <expr>
              |  IDENTIFIER PLUS_ASSIGN <expr>
              |  IDENTIFIER MINUS_ASSIGN <expr>
              |  <expr> LBRACKET <expr> RBRACKET ASSIGN <expr>   /* s[i] = x , m[i][j] = x */
              |  <expr> DOT IDENTIFIER ASSIGN <expr>             /* e.Campo = x */

/* ── Control de flujo ────────────────────────────────────────────── */
<if_stmt>    ::= IF <expr> <bloque>
              |  IF <expr> <bloque> ELSE <bloque>
              |  IF <expr> <bloque> ELSE <if_stmt>

<switch_stmt> ::= SWITCH <expr> LBRACE <case_list> RBRACE
              |  SWITCH LBRACE <case_list> RBRACE

<case_list>  ::= <case_list> <case_item>
              |  ε

<case_item>  ::= CASE <expr_list> COLON <stmt_list>
              |  DEFAULT COLON <stmt_list>

<for_stmt>   ::= FOR <bloque>
              |  FOR <expr> <bloque>
              |  FOR <for_init> SEMICOLON <expr> SEMICOLON <for_post> <bloque>

<for_init>   ::= <var_decl>
              |  <asignacion>
              |  ε

<for_post>   ::= IDENTIFIER INC
              |  IDENTIFIER DEC
              |  <asignacion>
              |  ε

/* ── Llamadas ────────────────────────────────────────────────────── */
<llamada_funcion> ::= IDENTIFIER LPAREN <arg_list> RPAREN

<llamada_metodo>  ::= <expr> DOT IDENTIFIER LPAREN <arg_list> RPAREN

<arg_list>        ::= <arg_list_ne>
                   |  ε

<arg_list_ne>     ::= <arg_list_ne> COMMA <expr>
                   |  <expr>

<llamada_embebida> ::= PRINTLN LPAREN <expr_list> RPAREN
                    |  ATOI LPAREN <expr> RPAREN
                    |  PARSEFLOAT LPAREN <expr> RPAREN
                    |  TYPEOF LPAREN <expr> RPAREN
                    |  LEN LPAREN <expr> RPAREN
                    |  APPEND LPAREN <arg_list> RPAREN
                    |  SLICES_INDEX LPAREN <expr> COMMA <expr> RPAREN
                    |  STRINGS_JOIN LPAREN <expr> COMMA <expr> RPAREN

<expr_list>        ::= <expr_list_ne>
                    |  ε

<expr_list_ne>     ::= <expr_list_ne> COMMA <expr>
                    |  <expr>

/* ── Elementos de literales de slice (permiten {..} anidados) ─────── */
<elem_list>        ::= <elem_list_ne>
                    |  ε

<elem_list_ne>     ::= <elem_list_ne> COMMA <elem>
                    |  <elem>

<elem>             ::= <expr>
                    |  LBRACE <elem_list> RBRACE        /* sub-slice elidido {1,2} */

/* ── Expresiones ─────────────────────────────────────────────────── */
<expr>       ::= <expr> PLUS <expr>
              |  <expr> MINUS <expr>
              |  <expr> TIMES <expr>
              |  <expr> DIVIDE <expr>
              |  <expr> MOD <expr>
              |  <expr> EQ <expr>
              |  <expr> NE <expr>
              |  <expr> LT <expr>
              |  <expr> LE <expr>
              |  <expr> GT <expr>
              |  <expr> GE <expr>
              |  <expr> AND <expr>
              |  <expr> OR <expr>
              |  NOT <expr>
              |  MINUS <expr>                                   /* menos unario */
              |  LPAREN <expr> RPAREN
              |  <llamada_funcion>
              |  <llamada_metodo>
              |  <llamada_embebida>
              |  <expr> LBRACKET <expr> RBRACKET                /* acceso s[i] / m[i][j] */
              |  LBRACKET RBRACKET <tipo> LBRACE <elem_list> RBRACE   /* literal []T{...} */
              |  MAKE LPAREN LBRACKET RBRACKET <tipo> COMMA <expr> RPAREN  /* make([]T, n) */
              |  <expr> DOT IDENTIFIER                           /* acceso a atributo e.Campo */
              |  INT_LITERAL
              |  FLOAT_LITERAL
              |  STRING_LITERAL
              |  RUNE_LITERAL
              |  BOOL_LITERAL
              |  NIL
              |  IDENTIFIER
```

## Precedencia (de menor a mayor)

```
ASSIGN PLUS_ASSIGN MINUS_ASSIGN   (der)
OR                                (izq)
AND                               (izq)
EQ NE                             (izq)
LT LE GT GE                       (izq)
PLUS MINUS                        (izq)
TIMES DIVIDE MOD                  (izq)
MINUS (unario)                    (der)
NOT                               (der)
LPAREN                            (izq)
LBRACKET                          (izq)
```

## Notas

- `error` es el token especial de CUP para la **recuperación en modo pánico**:
  ante un error sintáctico, el parser descarta tokens hasta resincronizar y
  reportar varios errores en una sola pasada.
- Las funciones con receptor `FUNC LPAREN id tipo RPAREN ...` son los **métodos
  de struct**, invocados con `<expr> DOT IDENTIFIER LPAREN <arg_list> RPAREN`.
- Los conflictos shift-reduce del indexado y del acceso/asignación con `.` y `[]`
  se resuelven por *shift* (compilado con `-expect`).
```
