///[Error:mover|22]
// Era semCorrecto15: Volador y Nadador declaran mover(int) y mover(char),
// Anfibio extiende a las dos y Pato implementa Anfibio con ambos mover. Con
// la clave (nombre, aridad) (D4) los dos mover de Pato son mover/1:
// ERR_METODO_DUPLICADO sobre el segundo.
interface Volador {
    void mover(int pasos);
}

interface Nadador {
    void mover(char pasos);
}

interface Anfibio extends Volador, Nadador {
}

class Pato implements Anfibio {
    void mover(int pasos){

    }

    void mover(char pasos){

    }
}
