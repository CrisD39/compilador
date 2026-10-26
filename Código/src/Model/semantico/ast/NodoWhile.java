package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// <While> ::= while ( <Expresion> ) <Sentencia>
public class NodoWhile extends NodoSentencia {
    private final Token tokenWhile;
    private final NodoExpresion condicion;
    private final NodoSentencia cuerpo;

    public NodoWhile(Token tokenWhile, NodoExpresion condicion, NodoSentencia cuerpo) {
        this.tokenWhile = tokenWhile;
        this.condicion = condicion;
        this.cuerpo = cuerpo;
    }

    // §12.4: misma regla de condición y de cuerpo que NodoIf.
    @Override
    public void chequear(TablaSimbolos tabla) {
        if (!Tipo.BOOLEAN.equals(condicion.chequear(tabla))) {
            throw new ExcepcionSemantica(tokenWhile, "ERR_CONDICION_NO_BOOLEAN");
        }
        chequearCuerpo(cuerpo, tabla);
    }

    // D3: while (true) no termina normalmente; con cualquier otra condición
    // el cuerpo puede no ejecutarse nunca. Sin break, el cuerpo no cambia la
    // salida del bucle.
    @Override
    public Flujo salida(Flujo entrada) {
        return esConstanteTrue(condicion) ? Flujo.MUERTO : entrada;
    }
}
