package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.EntidadDeclarada;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.Metodo;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;

import java.util.List;
import java.util.Map;

// <LlamadaMetodoEstatico> ::= idClase . idMetVar <ArgsActuales> (⚠6). Sale
// también de la rama "." de <SentIdClase> y de <TrasIdClaseFor> (⚠13).
public class NodoLlamadaEstatica extends NodoPrimario {
    private final Token clase;
    private final Token nombre;
    private final List<NodoExpresion> argumentos;

    public NodoLlamadaEstatica(Token clase, Token nombre, List<NodoExpresion> argumentos) {
        this.clase = clase;
        this.nombre = nombre;
        this.argumentos = argumentos;
    }

    @Override
    public Token primerToken() {
        return clase;
    }

    @Override
    protected boolean esLlamableSinEncadenado() {
        return true;
    }

    // §10.2: C tiene que existir (ERR_TIPO_NO_DECLARADO, el de la Etapa 3,
    // sobre C) y m ser estático, porque no hay objeto receptor
    // (semIIError18). Los estáticos heredados ya están copiados en C
    // (A.debugPrint(3)). Una interfaz no tiene estáticos. La firma de un
    // estático no puede nombrar el T de su clase (ADR 0006), así que no hay
    // nada que sustituir. Un private sólo vale desde C (logro Visibilidad
    // chequeada).
    @Override
    public Tipo chequear(TablaSimbolos tabla) {
        EntidadDeclarada entidad = tabla.buscarClase(clase.getLexema());
        if (entidad == null) {
            throw new ExcepcionSemantica(clase, "ERR_TIPO_NO_DECLARADO");
        }
        Metodo metodo = entidad.buscarMetodo(Metodo.clave(nombre.getLexema(), argumentos.size()));
        if (metodo == null) {
            throw new ExcepcionSemantica(nombre, "ERR_METODO_NO_ENCONTRADO");
        }
        ChequeoVisibilidad.exigirAcceso(tabla, metodo.isPrivado(), metodo.getMiClase(), entidad, nombre);
        if (!metodo.isEstatico()) {
            throw new ExcepcionSemantica(nombre, "ERR_METODO_NO_ESTATICO");
        }
        List<Tipo> tiposArgumentos = ChequeoLlamada.chequearArgumentos(tabla, argumentos);
        return chequearEncadenado(tabla, ChequeoLlamada.tipoDeLlamada(tabla, metodo, Map.of(), nombre, tiposArgumentos));
    }
}
