package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

// Raíz de las expresiones del AST: chequear() devuelve el tipo de la
// expresión, o lanza ExcepcionSemantica. esAsignable() y esLlamable() dicen
// si puede ser destino de "=" y si alcanza como sentencia suelta (§11, §12.2);
// en una cadena de accesos decide el último eslabón (ver NodoPrimario).
// Es un ValorArreglo para poder ir, junto a los inicializadores anidados, en
// la lista de un NodoInicializadorArreglo (⚠12).
public abstract class NodoExpresion implements ValorArreglo {
    public abstract Tipo chequear(TablaSimbolos tabla);

    // Primer token que guarda la expresión, bajando por la izquierda. Lo usa
    // NodoSentenciaExpresion para reportar una sentencia inalcanzable (spec
    // 0004, D7): el nombre en una variable o una llamada, this, la clase en
    // una llamada estática o en un new, y lo de adentro de un paréntesis.
    public abstract Token primerToken();

    public boolean esAsignable() {
        return false;
    }

    public boolean esLlamable() {
        return false;
    }

    // Si la expresión puede ir sola con ";" (§12.2): por defecto, si termina
    // en una llamada o en un new de objeto. La asignación y el postfijo la
    // redefinen. Se pregunta sobre la expresión principal, así
    // (a1 = 3); no vale (semIIError80).
    public boolean esSentenciaValida() {
        return esLlamable();
    }

    // Valor de la expresión si es una condición constante (spec 0004, D6), o
    // null si no lo es. Sólo lo son los literales true y false, solos o entre
    // paréntesis: no se evalúa ninguna operación, así que !false o 1 < 2 no
    // son constantes, a diferencia de Java (ADR 0009). Por eso devuelve
    // Boolean: una condición constante sólo puede ser true o false. Sólo lo
    // usa el análisis de flujo, para las condiciones de while y for.
    public Boolean valorConstante() {
        return null;
    }
}
