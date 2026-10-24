package Model.semantico;

import Model.lexico.Token;

// Lo que resuelve un nombre suelto en un cuerpo (spec 0003, D6): una local,
// un parámetro o un atributo. Ver TablaSimbolos.buscarVariable().
public interface Variable {
    Token getNombre();

    Tipo getTipo();
}
