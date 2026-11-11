package com.ejemplo;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un nodo del Árbol Sintáctico Abstracto (AST - Punto 5).
 * Cada instancia representa una construcción del programa (declaración, asignación,
 * while, return, suma, multiplicación, constante o identificador) identificada por su TipoNodo.
 */
public class AST {
    private final TipoNodo tipoNodo; // Qué tipo de nodo es (PROGRAMA, WHILE, SUMA, etc.)
    private String nombre;           // Nombre del identificador o función (ej: "x", "main")
    private String tipoDato;         // Tipo de dato en declaraciones o retorno ("int", "bool", "void")
    private Object valor;            // Valor literal en hojas constantes (Integer o Boolean)
    private Info infoSimbolo;        // Referencia directa a la entrada en la Tabla de Símbolos (Punto 6)
    private final List<AST> hijos = new ArrayList<>(); // Subnodos hijos de este nodo

    // Constructor para nodos contenedores o sin atributos extra (ej: LISTA_DECLARACIONES, RETURN vacío)
    public AST(TipoNodo tipoNodo) {
        this.tipoNodo = tipoNodo;
    }

    // Constructor para constantes literales (NUMERO o BOOLEANO)
    public AST(TipoNodo tipoNodo, Object valor) {
        this.tipoNodo = tipoNodo;
        this.valor = valor;
    }

    // Constructor para nodos con un identificador (ej: IDENTIFICADOR o ASIGNACION)
    public AST(TipoNodo tipoNodo, String nombre) {
        this.tipoNodo = tipoNodo;
        this.nombre = nombre;
    }

    // Constructor para nodos con tipo de dato y nombre (ej: DECLARACION o PROGRAMA)
    public AST(TipoNodo tipoNodo, String tipoDato, String nombre) {
        this.tipoNodo = tipoNodo;
        this.tipoDato = tipoDato;
        this.nombre = nombre;
    }

    // Constructor para nodos binarios (ej: SUMA, MULTIPLICACION, WHILE)
    public AST(TipoNodo tipoNodo, AST izq, AST der) {
        this.tipoNodo = tipoNodo;
        if (izq != null) this.hijos.add(izq);
        if (der != null) this.hijos.add(der);
    }

    /**
     * Agrega un nodo hijo a la lista de hijos de este nodo.
     */
    public void agregarHijo(AST hijo) {
        if (hijo != null) {
            this.hijos.add(hijo);
        }
    }

    public TipoNodo getTipoNodo() {
        return tipoNodo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipoDato() {
        return tipoDato;
    }

    public Object getValor() {
        return valor;
    }

    public Info getInfoSimbolo() {
        return infoSimbolo;
    }

    public void setInfoSimbolo(Info infoSimbolo) {
        this.infoSimbolo = infoSimbolo;
    }

    public List<AST> getHijos() {
        return hijos;
    }

    /**
     * Obtiene el primer hijo (hijo izquierdo en operaciones binarias, condición en WHILE,
     * o expresión en ASIGNACION / RETURN).
     */
    public AST getIzq() {
        return hijos.size() > 0 ? hijos.get(0) : null;
    }

    /**
     * Obtiene el segundo hijo (hijo derecho en operaciones binarias, o cuerpo en WHILE).
     */
    public AST getDer() {
        return hijos.size() > 1 ? hijos.get(1) : null;
    }

    /**
     * Imprime de forma jerárquica el árbol sintáctico abstracto por consola.
     */
    public void imprimir(String indent) {
        StringBuilder linea = new StringBuilder(indent).append(tipoNodo);
        if (tipoDato != null) {
            linea.append(" [").append(tipoDato).append("]");
        }
        if (nombre != null) {
            linea.append(" (").append(nombre).append(")");
        }
        if (valor != null) {
            linea.append(" = ").append(valor);
        }
        System.out.println(linea);
        for (AST hijo : hijos) {
            hijo.imprimir(indent + "  ");
        }
    }
}
