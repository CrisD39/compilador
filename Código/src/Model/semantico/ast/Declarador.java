package Model.semantico.ast;

import Model.lexico.Token;

// <Declarador> ::= idMetVar <InitLocalOpc>
// Un nombre de una local clásica con su inicializador opcional (spec 0009,
// D3): en int x = 1, y; cada nombre tiene el suyo, como en Java (JLS
// §14.4). Sin inicializador, asignacion e inicializador son null. El "=" se
// guarda porque es el lexema de ERR_ASIGNACION_INCOMPATIBLE.
public record Declarador(Token nombre, Token asignacion, NodoExpresion inicializador) {
}
