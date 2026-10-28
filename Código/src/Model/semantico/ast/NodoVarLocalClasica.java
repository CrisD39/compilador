package Model.semantico.ast;

import Model.lexico.Token;
import Model.semantico.Atributo;
import Model.semantico.ExcepcionSemantica;
import Model.semantico.TablaSimbolos;
import Model.semantico.Tipo;
import Model.semantico.Variable;
import Model.semantico.VariableLocal;

import java.util.List;

// <Sentencia> ::= <TipoBase> <RestoDeclLocal> (int x = 1, y;, int[] a;). Un
// solo nodo por sentencia, no uno por nombre: el bloque recibe una sentencia
// por cada declaración del fuente, que es lo que cuentan el flujo (spec 0004)
// y la Multi-detección (spec 0009, D3). El tipo ya trae sus dimensiones, y
// los declaradores van en el orden del fuente, cada uno con su inicializador.
// El for arma el mismo nodo con un solo declarador.
public class NodoVarLocalClasica extends NodoSentencia {
    private final Tipo tipo;
    private final List<Declarador> declaradores;

    public NodoVarLocalClasica(Tipo tipo, List<Declarador> declaradores) {
        this.tipo = tipo;
        this.declaradores = declaradores;
    }

    public Token getPrimerNombre() {
        return declaradores.get(0).nombre();
    }

    // El tipo (int, A1) va antes que el primer nombre (spec 0004, D7).
    @Override
    public Token primerToken() {
        return tipo.getToken();
    }

    // Logro Variables locales clásicas controladas (spec 0009). El tipo se
    // resuelve una vez, como el del for-each (D4); si falla, no se chequea
    // ningún inicializador y los nombres se declaran sin tipo. Después, de
    // izquierda a derecha, cada declarador sigue el orden de NodoVarLocal
    // (D5): inicializador contra el tipo declarado (su tipo esperado, ADR
    // 0011), choque con una local visible o un parámetro, y declaración. Así
    // un inicializador ve los nombres anteriores pero no el suyo (int x = x
    // lee el atributo x, si hay). No hay asignación definida (ADR 0012).
    // Un error por sentencia (D6): el primero corta, pero antes se declaran
    // el nombre que falló y los siguientes, salvo los que chocan, para que
    // las sentencias siguientes no sumen errores en cascada (semIIVarLocal
    // Error16).
    @Override
    public void chequear(TablaSimbolos tabla) {
        Tipo declarado;
        try {
            declarado = tipo.resolverEnCuerpo(tabla);
        } catch (ExcepcionSemantica e) {
            declararDesde(0, null, tabla);
            throw e;
        }
        for (int i = 0; i < declaradores.size(); i++) {
            try {
                chequearDeclarador(declaradores.get(i), declarado, tabla);
            } catch (ExcepcionSemantica e) {
                declararDesde(i, declarado, tabla);
                throw e;
            }
        }
    }

    // El error del inicializador va sobre su "=", como en
    // Atributo.chequearInicializador; uno de un ternario ya salió sobre su
    // "?" y no llega a este conformaCon.
    private void chequearDeclarador(Declarador declarador, Tipo declarado, TablaSimbolos tabla) {
        if (declarador.inicializador() != null) {
            Tipo valor = declarador.inicializador().chequearConTipoEsperado(tabla, declarado);
            if (!valor.conformaCon(declarado, tabla)) {
                throw new ExcepcionSemantica(declarador.asignacion(), "ERR_ASIGNACION_INCOMPATIBLE");
            }
        }
        if (esLocalOParametro(declarador.nombre(), tabla)) {
            throw new ExcepcionSemantica(declarador.nombre(), "ERR_LOCAL_DUPLICADA");
        }
        tabla.declararLocal(new VariableLocal(declarador.nombre(), declarado));
    }

    // Recuperación (D4 y D6): declara, sin chequear sus inicializadores, los
    // nombres desde el que falló; sin tipo si el tipo era inválido (null).
    // Un nombre que choca no se declara: sigue valiendo la variable anterior.
    private void declararDesde(int desde, Tipo declarado, TablaSimbolos tabla) {
        for (Declarador declarador : declaradores.subList(desde, declaradores.size())) {
            Token nombre = declarador.nombre();
            if (!esLocalOParametro(nombre, tabla)) {
                tabla.declararLocal(declarado == null
                        ? VariableLocal.sinTipo(nombre) : new VariableLocal(nombre, declarado));
            }
        }
    }

    // Ocultamiento (§10.1): un atributo no choca, la local lo oculta.
    private static boolean esLocalOParametro(Token nombre, TablaSimbolos tabla) {
        Variable previa = tabla.buscarVariable(nombre.getLexema());
        return previa != null && !(previa instanceof Atributo);
    }
}
