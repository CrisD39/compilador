package Model.semantico.ast;

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
}
