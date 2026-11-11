package com.ejemplo;

import java_cup.runtime.Symbol;

%%

%class Lexer
%unicode
%line
%column
%cup

%{
    // Crea un token sin valor semántico asociado (para palabras reservadas, operadores y delimitadores)
    private Symbol symbol(int type) {
        return new Symbol(type, yyline + 1, yycolumn + 1);
    }

    // Crea un token con un valor asociado (para números enteros, booleanos e identificadores)
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }
%}

/* Expresiones regulares base (Punto 2 y Punto 3) */
LETRA = [a-zA-Z]
DIGITO = [0-9]
IDENTIFICADOR = {LETRA}({LETRA}|{DIGITO})*
ENTERO = {DIGITO}+

%%

/* Palabras reservadas del lenguaje */
"int"           { return symbol(sym.INT); }
"bool"          { return symbol(sym.BOOL); }
"void"          { return symbol(sym.VOID); }
"main"          { return symbol(sym.MAIN); }
"while"         { return symbol(sym.WHILE); }
"return"        { return symbol(sym.RETURN); }

/* Constantes lógicas booleanas */
"true"          { return symbol(sym.TRUE, Boolean.TRUE); }
"false"         { return symbol(sym.FALSE, Boolean.FALSE); }

/* Constantes numéricas enteras y nombres de variables */
{ENTERO}         { return symbol(sym.NUMERO, Integer.valueOf(yytext())); }
{IDENTIFICADOR}  { return symbol(sym.IDENTIFICADOR, yytext()); }

/* Operadores y delimitadores */
"+"             { return symbol(sym.SUMA); }
"*"             { return symbol(sym.MULTIPLICACION); }
"="             { return symbol(sym.ASIGNACION); }
";"             { return symbol(sym.PUNTO_Y_COMA); }
"("             { return symbol(sym.PARENTESIS_IZQ); }
")"             { return symbol(sym.PARENTESIS_DER); }
"{"             { return symbol(sym.LLAVE_IZQ); }
"}"             { return symbol(sym.LLAVE_DER); }

/* Espacios en blanco, tabulaciones y saltos de línea se ignoran */
[ \t\r\n]+      { /* Ignorar espacios */ }

/* Cualquier otro carácter no válido lanza error léxico */
. {
    throw new RuntimeException("Error léxico: carácter no reconocido '" + yytext() + "'");
}