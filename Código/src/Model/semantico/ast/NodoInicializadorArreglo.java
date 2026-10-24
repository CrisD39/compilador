package Model.semantico.ast;

import java.util.List;

// <InicializadorArreglo> ::= { <ListaValoresArregloOpcional> }. No es una
// expresión (⚠12): sólo aparece después de new T[] o dentro de otro
// inicializador. Lo chequea NodoCreacionArregloConInicializador.
public class NodoInicializadorArreglo implements ValorArreglo {
    private final List<ValorArreglo> valores;

    public NodoInicializadorArreglo(List<ValorArreglo> valores) {
        this.valores = valores;
    }
}
