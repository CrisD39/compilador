package Model.semantico.ast;

import Model.lexico.Token;
import Model.lexico.TokenType;
import Model.semantico.EntidadDeclarada;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

import java.util.List;

// new T[e1][e2]...: <DimensionesNew> con <TrasCorcheteNew> ::= <Expresion> ]
// <DimensionesConTamanioOpc> (⚠11). corchetes guarda el "[" de cada tamaño,
// que es el lexema de ERR_INDICE_NO_ENTERO. No es asignable ni llamable:
// new int[3] = null; y new int[3]; son errores (semIIError68, 79).
public class NodoCreacionArregloConTamanio extends NodoPrimario {
    private final Tipo tipoBase;
    private final List<Token> corchetes;
    private final List<NodoExpresion> tamanios;

    public NodoCreacionArregloConTamanio(Tipo tipoBase, List<Token> corchetes, List<NodoExpresion> tamanios) {
        this.tipoBase = tipoBase;
        this.corchetes = corchetes;
        this.tamanios = tamanios;
    }

    // §11.8: el tipo base es un primitivo, una clase o una interfaz (new
    // I1[5] crea referencias, no instancias: semIICorrecto17), con las
    // reglas de tipos de la Etapa 3 y sus códigos, como en NodoCreacionObjeto.
    // Cada tamaño es int, sin conversión desde char (semIIError70). El tipo
    // tiene tantas dimensiones como tamaños: new int[3][4] es int[][].
    // Pendiente (ticket 06): el tipo base paramétrico o genérico instanciado
    // (new T[10], new Caja<String>[10]).
    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        chequearTipoBase(tabla);
        for (int i = 0; i < tamanios.size(); i++) {
            if (!tamanios.get(i).chequear(tabla).conformaCon(Tipo.INT, tabla)) {
                throw new ExcepcionSemantica(corchetes.get(i), "ERR_INDICE_NO_ENTERO");
            }
        }
        return chequearEncadenado(tabla, tipoBase.conDimensiones(tamanios.size()));
    }

    private void chequearTipoBase(TablaSimbolos tabla) {
        Token nombre = tipoBase.getToken();
        if (nombre.getTipo() == TokenType.ID_GEN) {
            throw ExcepcionSemantica.noSoportado(nombre);
        }
        if (nombre.getTipo() != TokenType.ID_CLASE) {
            return;
        }
        EntidadDeclarada entidad = tabla.buscarClase(nombre.getLexema());
        if (entidad == null) {
            throw new ExcepcionSemantica(nombre, "ERR_TIPO_NO_DECLARADO");
        }
        boolean esGenerica = !entidad.getParametrosTipo().isEmpty();
        if (esGenerica && tipoBase.getArgumento() == null) {
            throw new ExcepcionSemantica(nombre, "ERR_TIPO_SIN_ARGUMENTO");
        }
        if (!esGenerica && tipoBase.getArgumento() != null) {
            throw new ExcepcionSemantica(nombre, "ERR_TIPO_NO_GENERICO");
        }
        if (esGenerica) {
            throw ExcepcionSemantica.noSoportado(nombre);
        }
    }
}
