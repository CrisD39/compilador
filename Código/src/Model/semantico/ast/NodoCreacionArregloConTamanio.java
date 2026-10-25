package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

import java.util.List;

// new T[e1][e2]...: <DimensionesNew> con <TrasCorcheteNew> ::= <Expresion> ]
// <DimensionesConTamanioOpc> (⚠11). corchetes guarda el "[" de cada tamaño,
// que es el lexema de ERR_INDICE_NO_ENTERO. No es asignable ni llamable:
// new int[3] = null; y new int[3]; son errores (semIIError68, 79). Con
// diamante (new C<>[3]) es el logro Genericidad avanzada, todavía no
// soportado (D11), igual que new C<>() (semIIError172).
public class NodoCreacionArregloConTamanio extends NodoPrimario {
    private final Tipo tipoBase;
    private final boolean esDiamante;
    private final List<Token> corchetes;
    private final List<NodoExpresion> tamanios;

    public NodoCreacionArregloConTamanio(Tipo tipoBase, boolean esDiamante, List<Token> corchetes,
                                         List<NodoExpresion> tamanios) {
        this.tipoBase = tipoBase;
        this.esDiamante = esDiamante;
        this.corchetes = corchetes;
        this.tamanios = tamanios;
    }

    // §11.8: el tipo base es un primitivo, una clase o una interfaz (new
    // I1[5] crea referencias, no instancias: semIICorrecto17), con las
    // reglas de tipos de la Etapa 3 y sus códigos, como en NodoCreacionObjeto.
    // También un parámetro de tipo o un genérico instanciado (new T[10],
    // new Caja<T>[3][4], semIICorrecto16), salvo el T de la clase en un
    // estático (ERR_PARAMETRO_TIPO_EN_ESTATICO, semIIError32).
    // Cada tamaño es int, sin conversión desde char (semIIError70). El tipo
    // tiene tantas dimensiones como tamaños: new int[3][4] es int[][].
    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        if (esDiamante) {
            throw ExcepcionSemantica.noSoportado(tipoBase.getToken());
        }
        Tipo base = tipoBase.resolverEnCuerpo(tabla);
        for (int i = 0; i < tamanios.size(); i++) {
            if (!tamanios.get(i).chequear(tabla).conformaCon(Tipo.INT, tabla)) {
                throw new ExcepcionSemantica(corchetes.get(i), "ERR_INDICE_NO_ENTERO");
            }
        }
        return chequearEncadenado(tabla, base.conDimensiones(tamanios.size()));
    }
}
