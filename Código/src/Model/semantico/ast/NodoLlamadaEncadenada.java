package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.EntidadDeclarada;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.Metodo;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

import java.util.List;

// <ReferenciaResto> ::= . idMetVar <ArgsActuales> <ReferenciaResto>: .m(...)
// (⚠2, ⚠6).
public class NodoLlamadaEncadenada extends NodoEncadenado {
    private final Token nombre;
    private final List<NodoExpresion> argumentos;

    public NodoLlamadaEncadenada(Token nombre, List<NodoExpresion> argumentos, NodoEncadenado siguienteEncadenado) {
        super(siguienteEncadenado);
        this.nombre = nombre;
        this.argumentos = argumentos;
    }

    @Override
    protected boolean esLlamableSinSiguiente() {
        return true;
    }

    // §10.2: m se busca por nombre/aridad en el tipo estático del receptor
    // (ver entidadDelReceptor()), clase o interfaz. Una interfaz ya trae los
    // métodos de instancia de Object (i.toString()). Con receptor también
    // vale un estático (persona.consultar(2), semIICorrecto19). Un arreglo
    // sólo tiene los métodos de instancia de Object: toString() sí, el
    // estático debugPrint no, y clone() no existe (§11.8, semIIError74). Sus
    // parámetros y su retorno se ven desde el receptor (D10).
    @Override
    public Tipo chequear(TablaSimbolos tabla, Tipo tipoIzquierdo) {
        EntidadDeclarada entidad = entidadDelReceptor(tabla, tipoIzquierdo, nombre);
        Metodo metodo = entidad.buscarMetodo(Metodo.clave(nombre.getLexema(), argumentos.size()));
        if (metodo == null || (tipoIzquierdo.esArreglo() && metodo.isEstatico())) {
            throw new ExcepcionSemantica(nombre, "ERR_METODO_NO_ENCONTRADO");
        }
        List<Tipo> tiposArgumentos = ChequeoLlamada.chequearArgumentos(tabla, argumentos);
        Tipo tipo = ChequeoLlamada.tipoDeLlamada(
                tabla, metodo, tabla.instanciacionDe(tipoIzquierdo), nombre, tiposArgumentos);
        return chequearSiguiente(tabla, tipo);
    }
}
