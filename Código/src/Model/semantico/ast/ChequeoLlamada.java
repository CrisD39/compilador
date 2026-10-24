package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.Metodo;
import Model.semantico.Parametro;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

import java.util.ArrayList;
import java.util.List;

// Lo que comparten las tres llamadas (m(), C.m() y e.m(), ⚠6) y la creación
// de objetos: tipar los argumentos y controlarlos contra los parámetros del
// método o constructor ya elegido por nombre/aridad (§10.2, §10.3). No es un
// nodo: cada uno resuelve su método a su manera y después delega acá.
final class ChequeoLlamada {
    private ChequeoLlamada() {
    }

    // Cada nodo los chequea recién después de resolver el método o el
    // constructor, que sólo necesita la cantidad: con un error por sentencia
    // (D7), el informado es el primero en el orden del fuente, y en
    // noExiste(z) gana noExiste y no z.
    static List<Tipo> chequearArgumentos(TablaSimbolos tabla, List<NodoExpresion> argumentos) {
        List<Tipo> tipos = new ArrayList<>();
        for (NodoExpresion argumento : argumentos) {
            tipos.add(argumento.chequear(tabla));
        }
        return tipos;
    }

    // Tipo de la llamada a un método ya resuelto. Un método con parámetros de
    // tipo propios es del logro Métodos genéricos (D11), y se corta antes de
    // comparar argumentos contra sus T.
    static Tipo tipoDeLlamada(TablaSimbolos tabla, Metodo metodo, Token nombre, List<Tipo> tiposArgumentos) {
        if (!metodo.getParametrosTipoPropios().isEmpty()) {
            throw ExcepcionSemantica.noSoportado(nombre);
        }
        exigirConformidad(tabla, metodo.getParams(), tiposArgumentos, nombre);
        return metodo.getTipoDeLlamada();
    }

    // Cada argumento conforma con su parámetro, en orden (§10.2). La aridad
    // ya coincide porque se eligió por clave, y no se busca otra variante por
    // tipos (semIIError16). Un argumento void no conforma con nada
    // (semIIError49). El error va sobre el nombre del método o de la clase.
    static void exigirConformidad(TablaSimbolos tabla, List<Parametro> formales, List<Tipo> actuales, Token token) {
        for (int i = 0; i < formales.size(); i++) {
            if (!actuales.get(i).conformaCon(formales.get(i).getTipo(), tabla)) {
                throw new ExcepcionSemantica(token, "ERR_ARGUMENTO_INCOMPATIBLE");
            }
        }
    }
}
