package com.ejemplo;

/**
 * Representa una instrucción individual de Código de Tres Direcciones (cuádrupla).
 * Cada instrucción consta de una operación, un destino (resultado) y hasta dos operandos (arg1, arg2).
 */
public class InstruccionC3D {
    private final OperacionC3D operacion; // Tipo de operación C3D
    private final String resultado;       // Variable/temporal destino o nombre de etiqueta
    private final String arg1;            // Primer operando (o condición a evaluar)
    private final String arg2;            // Segundo operando (en operaciones binarias)

    public InstruccionC3D(OperacionC3D operacion, String resultado, String arg1, String arg2) {
        this.operacion = operacion;
        this.resultado = resultado;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public OperacionC3D getOperacion() {
        return operacion;
    }

    public String getResultado() {
        return resultado;
    }

    public String getArg1() {
        return arg1;
    }

    public String getArg2() {
        return arg2;
    }

    @Override
    public String toString() {
        return switch (operacion) {
            case ASIGNACION -> resultado + " = " + arg1;
            case SUMA -> resultado + " = " + arg1 + " + " + arg2;
            case MULTIPLICACION -> resultado + " = " + arg1 + " * " + arg2;
            case ETIQUETA -> resultado + ":";
            case SALTO_FALSO -> "ifFalse " + arg1 + " goto " + resultado;
            case SALTO_INCONDICIONAL -> "goto " + resultado;
            case RETURN -> (arg1 != null) ? "return " + arg1 : "return";
        };
    }
}
