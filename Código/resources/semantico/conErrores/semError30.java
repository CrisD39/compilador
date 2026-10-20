///[Error:Ave|14]
// Ave extiende dos interfaces sealed: Volador (que SI la permite) y Nadador
// (que permite a Ganso, no a Ave) -- cada entrada de la lista de 'extends'
// se chequea de forma independiente contra su propio permits. A diferencia
// de semError31, las dos entradas son sealed.
sealed interface Volador permits Ave {
    void volar();
}

sealed interface Nadador permits Ganso {
    void nadar();
}

nonsealed interface Ave extends Volador, Nadador {
}

final class Pato implements Ave {
    void volar(){
    }
    void nadar(){
    }
}

final class Ganso implements Nadador {
    void nadar(){
    }
}
