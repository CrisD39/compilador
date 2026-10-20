package Model.semantico;

import java.util.Map;

public interface Chequeable {
    // 'tabla' es el sumidero de errores (TablaSimbolos.agregarError) -- cada
    // entidad delega en lo que contiene, pasando la misma tabla hacia abajo.
    // 'entornoGenerico' son los idGen vigentes en este punto (parámetros de
    // tipo de la clase/interfaz contenedora, unidos a los del método si
    // aplica), cada uno con la entidad que lo declara -- así Tipo resuelve su
    // origen (§5.1). Cada entidad lo propaga o lo extiende antes de delegar
    // hacia lo que contiene.
    void estaBienDeclarado(TablaSimbolos tabla, Map<String, OrigenParametroTipo> entornoGenerico);
}
