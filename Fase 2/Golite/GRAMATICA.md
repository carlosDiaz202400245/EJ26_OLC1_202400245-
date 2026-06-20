# Gramática GoLite — Producciones BNF

```bnf
<program>    ::= <decl_list>

<decl_list>  ::= <decl_list> <decl>
              |  ε

<decl>       ::= <func_decl>
              |  <stmt>

<tipo>       ::= INT_TYPE
              |  FLOAT_TYPE
              |  STRING_TYPE
              |  BOOL_TYPE
              |  RUNE_TYPE
              |  IDENTIFIER

<func_decl>      ::= FUNC IDENTIFIER LPAREN <param_list> RPAREN <bloque>
                  |  FUNC IDENTIFIER LPAREN <param_list> RPAREN <tipo> <bloque>

<param_list>     ::= <param_list_ne>
                  |  ε

<param_list_ne>  ::= <param_list_ne> COMMA <param>
                  |  <param>

<param>          ::= IDENTIFIER <tipo>

<bloque>     ::= LBRACE <stmt_list> RBRACE

<stmt_list>  ::= <stmt_list> <stmt>
              |  ε

<stmt>       ::= <bloque>
              |  <var_decl>
              |  <asignacion>
              |  IDENTIFIER INC
              |  IDENTIFIER DEC
              |  <llamada_funcion>
              |  <llamada_embebida>
              |  <if_stmt>
              |  <for_stmt>
              |  BREAK
              |  CONTINUE
              |  RETURN <expr>
              |  SEMICOLON

<var_decl>   ::= VAR IDENTIFIER <tipo>
              |  VAR IDENTIFIER <tipo> ASSIGN <expr>
              |  VAR IDENTIFIER ASSIGN <expr>
              |  IDENTIFIER DEFINE <expr>

<asignacion> ::= IDENTIFIER ASSIGN <expr>
              |  IDENTIFIER PLUS_ASSIGN <expr>
              |  IDENTIFIER MINUS_ASSIGN <expr>

<if_stmt>    ::= IF <expr> <bloque>
              |  IF <expr> <bloque> ELSE <bloque>
              |  IF <expr> <bloque> ELSE <if_stmt>

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

<llamada_funcion> ::= IDENTIFIER LPAREN <arg_list> RPAREN

<arg_list>        ::= <arg_list_ne>
                   |  ε

<arg_list_ne>     ::= <arg_list_ne> COMMA <expr>
                   |  <expr>

<llamada_embebida> ::= PRINTLN LPAREN <expr_list> RPAREN
                    |  ATOI LPAREN <expr> RPAREN
                    |  PARSEFLOAT LPAREN <expr> RPAREN
                    |  TYPEOF LPAREN <expr> RPAREN

<expr_list>        ::= <expr_list_ne>
                    |  ε

<expr_list_ne>     ::= <expr_list_ne> COMMA <expr>
                    |  <expr>

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
              |  MINUS <expr>
              |  LPAREN <expr> RPAREN
              |  <llamada_funcion>
              |  <llamada_embebida>
              |  <expr> DOT IDENTIFIER
              |  INT_LITERAL
              |  FLOAT_LITERAL
              |  STRING_LITERAL
              |  RUNE_LITERAL
              |  BOOL_LITERAL
              |  NIL
              |  IDENTIFIER
```
