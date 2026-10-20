package Model.semantico;

import Model.lexico.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Interfaz implements Chequeable, EntidadDeclarada, OrigenParametroTipo {
    private Token nombre;
    // Parámetro de tipo propio de esta interfaz (ej. el T de
    // "interface Comparador<T>"): 0 o 1 elemento (REQ-AS-010).
    private List<Token> parametrosTipo = new ArrayList<>();
    // Key por firma (nombre + tipos de parámetros), no por Token -- mismo
    // criterio que Clase.metodos (ver Metodo.getFirma()): Token no sobrescribe
    // equals()/hashCode(), así que un HashMap<Token,Metodo> nunca detectaría
    // una firma repetida (cada Token es una instancia distinta del lexer).
    private HashMap<String,Metodo> metodos = new HashMap<>();
    private boolean consolidado = false;
    private boolean enCiclo = false;
    private final TablaSimbolos tablaSimbolos;
    // Todas las interfaces que esta extiende (0, 1 o varias -- Logro 2:
    // "interface X extends Y, Z"). No hay un campo separado para "la primera"
    // como pasaba antes con 'extendida': una lista homogénea es más simple.
    // Es la única lista de supertipos del lenguaje: una clase implementa a
    // lo sumo una interfaz (ADR 0005, Clase.interfaz).
    // Cada entrada es un Tipo (cabeza más argumento opcional, D2), validado
    // en estaBienDeclarado() como cualquier tipo referencia.
    private LinkedHashSet<Tipo> herencias;
    private boolean sealed = false;
    private boolean nonSealed = false;
    private LinkedHashSet<Token> permitidos = new LinkedHashSet<Token>();
    // true si ALGUNA de las interfaces en 'herencias' resolvió a un tipo
    // sealed real -- mismo rol que Clase.tieneSupertipoSellado, pero acá solo
    // hay un lado (extends; las interfaces no 'implements').
    private boolean tieneSupertipoSellado = false;

    public Interfaz(Token nombre, TablaSimbolos tablaSimbolos) {
        this.nombre = nombre;
        this.tablaSimbolos = tablaSimbolos;
        this.herencias = new LinkedHashSet<>();
    }

    public void setSealed(boolean sealed) {
        this.sealed = sealed;
    }

    public boolean isSealed() {
        return sealed;
    }

    public void setNonSealed(boolean nonSealed) {
        this.nonSealed = nonSealed;
    }

    public void agregarPermitido(Token permitido) {
        permitidos.add(permitido);
    }

    // Chequea por lexema, no por Set.add() (Token no sobrescribe equals()/
    // hashCode(), así que el LinkedHashSet nunca detectaría un duplicado
    // literal por sí solo -- "interface X extends Y, Y" debe ser error, no
    // deduplicarse en silencio).
    // Se compara sólo la cabeza: "extends Comp<A>, Comp<B>" también repite
    // la interfaz (Java tampoco deja heredar dos parametrizaciones).
    public void agregarHerencia(Tipo padre) {
        Token cabeza = padre.getToken();
        for (Tipo existente : herencias) {
            if (existente.getToken().getLexema().equals(cabeza.getLexema())) {
                tablaSimbolos.agregarError(new ErrorSemantico(
                        "ERR_INTERFAZ_DUPLICADA", cabeza.getLinea(), cabeza.getLexema()));
                return;
            }
        }
        herencias.add(padre);
    }

    // Devuelve false si ya había un método con la misma firma -- mismo
    // criterio que Clase.agregarMetodo (ver su comentario).
    public void agregarMetodo(Metodo metodo) {
        String firma = metodo.getFirma();
        if (metodos.containsKey(firma)) {
            tablaSimbolos.agregarError(new ErrorSemantico(
                    "ERR_METODO_DUPLICADO", metodo.getNombre().getLinea(), metodo.getNombre().getLexema()));
            return;
        }
        metodos.put(firma, metodo);
    }

    @Override
    public String getName() {
        return nombre.getLexema();
    }

    public Token getNombre() {
        return nombre;
    }

    @Override
    public int getLinea() {
        return nombre.getLinea();
    }

    public List<Token> getParametrosTipo() {
        return parametrosTipo;
    }

    public void setParametrosTipo(List<Token> parametrosTipo) {
        this.parametrosTipo = parametrosTipo;
    }

    // Tal como se escribieron (con su argumento), sin resolver, en orden de
    // declaración. Una repetida (ERR_INTERFAZ_DUPLICADA) no se guarda.
    public Iterable<Tipo> getHerencias() {
        return herencias;
    }

    @Override
    public void estaBienDeclarado(TablaSimbolos tabla, Map<String, OrigenParametroTipo> entornoGenericoExterno) {
        // metodos ya se puebla (metodoInterfaz() construye el Metodo
        // completo, sin placeholder/parcheo porque <MetodoInterfaz> no tiene
        // cuerpo que parsear después). Cada Metodo valida sus propios
        // parámetros (ERR_PARAMETRO_DUPLICADO) igual que en Clase.
        Map<String, OrigenParametroTipo> entornoInterfaz = new HashMap<>();
        for (Token parametroTipo : parametrosTipo) {
            entornoInterfaz.put(parametroTipo.getLexema(), this);
        }
        // Superinterfaces: mismo control que el 'extends'/'implements' de
        // Clase (D2, D7; ver Clase.estaBienDeclarado()).
        // Una superinterfaz que resulta ser una clase da
        // ERR_HERENCIA_TIPO_INCORRECTO (D1) y la consolidación la saltea.
        for (Tipo padre : herencias) {
            padre.estaBienDeclarado(tabla, entornoInterfaz);
            tabla.chequearCategoriaSupertipo(padre, false);
        }
        for (Metodo metodo : metodos.values()) {
            metodo.estaBienDeclarado(tabla, entornoInterfaz);
        }
    }

    public Iterable<Metodo> getMetodos(){
        return metodos.values();
    }

    // Único punto de entrada público, igual que Clase.consolidar(): construye
    // su propio camino de recursión para no depender de quien lo llama.
    public void consolidar() {
        consolidarHerencia(new LinkedHashSet<Interfaz>());
    }

    // Mismo algoritmo que Clase.consolidarHerencia (ver su comentario), pero
    // sobre la cadena "extends" de Interfaz, que es un campo/objeto distinto
    // -- y ahora sobre una LISTA de padres, no uno solo (Logro 2: "interface
    // X extends Y, Z"). A diferencia de Clase (que solo puede tener un
    // 'extends'), acá cada entrada de 'herencias' es independiente: un
    // problema con un padre (no declarado, ciclo) NO aborta el chequeo de
    // los demás -- 'continue' en vez de 'return' (multi-detección, Logro 4).
    // Además copia los métodos de cada padre resuelto (heredarMetodosDe()).
    private void consolidarHerencia(LinkedHashSet<Interfaz> camino) {
        if (consolidado || enCiclo) {
            return;
        }
        camino.add(this);

        // Métodos heredados de los padres ya recorridos, por firma -- sirve
        // para comparar entre sí lo que llega de padres distintos (ver
        // heredarMetodosDe()).
        Map<String, Metodo> heredados = new HashMap<>();
        for (Tipo padreTipo : herencias) {
            EntidadDeclarada padreEntidad = tablaSimbolos.buscarInterfaz(padreTipo.getToken().getLexema());
            if (padreEntidad == null) {
                // Ya reportado como ERR_TIPO_NO_DECLARADO en
                // estaBienDeclarado() (D7): se saltea sin reportar.
                continue;
            }
            if (!(padreEntidad instanceof Interfaz)) {
                // D1: 'extends' de interfaz apuntando a una clase. Ya
                // reportado como ERR_HERENCIA_TIPO_INCORRECTO en
                // estaBienDeclarado(): se saltea sin reportar.
                continue;
            }
            Interfaz padre = (Interfaz) padreEntidad;
            if (camino.contains(padre)) {
                tablaSimbolos.agregarError(new ErrorSemantico(
                        "ERR_HERENCIA_CICLICA", nombre.getLinea(), nombre.getLexema()));
                marcarCicloDesde(padre, camino);
                enCiclo = true;
                continue;
            }
            padre.consolidarHerencia(camino);
            if (padre.enCiclo) {
                enCiclo = true;
                continue;
            }
            // Mismo chequeo que Clase.consolidarHerencia, sin la rama 'final'
            // (no existe 'final interface', ver analizador-semantico.md Logro
            // 1): sealed + permits de un solo nivel, y exhaustividad de dos
            // vías (sealed/nonsealed -- no hay tercera opción 'final' acá).
            if (padre.sealed) {
                tieneSupertipoSellado = true;
                if (!padre.permiteComoSubclase(nombre.getLexema())) {
                    tablaSimbolos.agregarError(new ErrorSemantico(
                            "ERR_HERENCIA_NO_PERMITIDA", nombre.getLinea(), nombre.getLexema()));
                } else if (!sealed && !nonSealed) {
                    tablaSimbolos.agregarError(new ErrorSemantico(
                            "ERR_HERENCIA_PERMITIDA_SIN_MODIFICADOR", nombre.getLinea(), nombre.getLexema()));
                }
            }
            heredarMetodosDe(padre, heredados);
        }
        validarNonSealed();
        validarPermitidos();
        consolidado = true;
        camino.remove(this);
    }

    // Copia en esta interfaz los métodos de 'padre' (ya consolidado, así que
    // trae también los de sus propias superinterfaces), para que una clase
    // que la implementa vea el contrato completo (ADR 0005: la clase
    // implementa una sola interfaz, y la herencia múltiple se arma acá).
    //
    // Si dos padres traen la misma firma con distinto retorno, es el
    // conflicto real de Java entre métodos "override-equivalentes" de
    // retorno incompatible (semError36, semError44). Se reporta sobre el
    // método del padre posterior en la lista de 'extends'. La misma firma
    // con el mismo retorno no es conflicto (semCorrecto13, semCorrecto21:
    // los parámetros de tipo propios se comparan por posición, ver
    // Metodo.tipoCanonico()), y en un diamante llega el mismo Metodo por
    // los dos lados.
    //
    // Un método que esta interfaz declara con una firma heredada no se pisa.
    // La comparación entre el propio y el heredado es la de la redefinición,
    // que no se controla acá.
    private void heredarMetodosDe(Interfaz padre, Map<String, Metodo> heredados) {
        for (Metodo metodoHeredado : padre.metodos.values()) {
            String firma = metodoHeredado.getFirma();
            Metodo previo = heredados.get(firma);
            if (previo == null) {
                heredados.put(firma, metodoHeredado);
                metodos.putIfAbsent(firma, metodoHeredado);
            } else if (previo != metodoHeredado
                    && !Objects.equals(previo.getRetornoCanonico(), metodoHeredado.getRetornoCanonico())) {
                tablaSimbolos.agregarError(new ErrorSemantico(
                        "ERR_METODO_RETORNO_INCOMPATIBLE",
                        metodoHeredado.getNombre().getLinea(), metodoHeredado.getNombre().getLexema()));
            }
        }
    }

    // 'nonsealed' solo es valido si esta interfaz tiene AL MENOS UN padre
    // sealed real entre todos los 'extends' -- mismo criterio que
    // Clase.validarNonSealed (ahí hay dos lados posibles, extends/implements;
    // acá solo uno, pero puede haber varios candidatos dentro de ese lado).
    private void validarNonSealed() {
        if (nonSealed && !tieneSupertipoSellado) {
            tablaSimbolos.agregarError(new ErrorSemantico(
                    "ERR_NONSEALED_SIN_PADRE_SEALED", nombre.getLinea(), nombre.getLexema()));
        }
    }

    // 'permits' solo se puebla si la interfaz es sealed (ver
    // permitidosSealedInterfaz() en el parser). Mismo criterio que
    // Clase.validarPermitidos(): un solo nivel, lee el campo crudo de la
    // entidad permitida sin depender de que ya haya corrido su propio
    // consolidar().
    private void validarPermitidos() {
        for (Token permitido : permitidos) {
            EntidadDeclarada entidad = tablaSimbolos.buscarInterfaz(permitido.getLexema());
            if (entidad == null) {
                tablaSimbolos.agregarError(new ErrorSemantico(
                        "ERR_TIPO_NO_DECLARADO", permitido.getLinea(), permitido.getLexema()));
            } else if (!esSubtipoDirecto(entidad)) {
                tablaSimbolos.agregarError(new ErrorSemantico(
                        "ERR_PERMITS_NO_ES_SUBTIPO", permitido.getLinea(), permitido.getLexema()));
            }
        }
    }

    // Un subtipo directo de una interfaz sealed puede ser otra interfaz que
    // la extiende, o una clase que la implementa -- Java no distingue entre
    // ambos lados para el 'permits' de una interfaz.
    private boolean esSubtipoDirecto(EntidadDeclarada entidad) {
        if (entidad instanceof Interfaz) {
            for (Tipo padre : ((Interfaz) entidad).getHerencias()) {
                if (padre.getToken().getLexema().equals(nombre.getLexema())) {
                    return true;
                }
            }
            return false;
        } else if (entidad instanceof Clase) {
            Tipo interfazImplementada = ((Clase) entidad).getInterfaz();
            return interfazImplementada != null
                    && interfazImplementada.getToken().getLexema().equals(nombre.getLexema());
        }
        return false;
    }

    // Compara por lexema, no por identidad -- mismo motivo y mismo código
    // que Clase.permiteComoSubclase (Token no sobrescribe equals()/hashCode()).
    // Público (a diferencia de la versión de Clase) porque también lo llama
    // Clase.consolidarInterfaces() desde otra instancia de OTRA clase -- ahí
    // no alcanza el acceso a privados entre instancias de la misma clase.
    public boolean permiteComoSubclase(String lexemaHijo) {
        for (Token permitido : permitidos) {
            if (permitido.getLexema().equals(lexemaHijo)) {
                return true;
            }
        }
        return false;
    }

    private void marcarCicloDesde(Interfaz padre, LinkedHashSet<Interfaz> camino) {
        boolean dentroDelCiclo = false;
        for (Interfaz i : camino) {
            if (i == padre) {
                dentroDelCiclo = true;
            }
            if (dentroDelCiclo) {
                i.enCiclo = true;
            }
        }
    }
}
