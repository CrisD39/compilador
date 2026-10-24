package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;

// <For> en su forma clásica (⚠9). La inicialización es una sentencia
// (NodoVarLocalClasica o NodoSentenciaExpresion), y las tres cláusulas
// pueden ser null (for (;;)). El cuerpo se setea aparte porque el parser lo
// lee después de cerrar las cláusulas. Logro For: todavía no soportado (D11).
public class NodoFor extends NodoSentencia {
    private final Token tokenFor;
    private final NodoSentencia inicializacion;
    private final NodoExpresion condicion;
    private final NodoExpresion actualizacion;
    private NodoSentencia cuerpo;

    public NodoFor(Token tokenFor, NodoSentencia inicializacion, NodoExpresion condicion,
                   NodoExpresion actualizacion) {
        this.tokenFor = tokenFor;
        this.inicializacion = inicializacion;
        this.condicion = condicion;
        this.actualizacion = actualizacion;
    }

    public void setCuerpo(NodoSentencia cuerpo) {
        this.cuerpo = cuerpo;
    }

    @Override
    public void chequear(TablaSimbolos tabla) {
        throw ExcepcionSemantica.noSoportado(tokenFor);
    }
}
