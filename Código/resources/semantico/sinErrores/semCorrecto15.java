///[SinErrores]
// Caso completo de Logro 2: Volador y Nadador tienen cada una un mover() de
// distinta firma (parametro int vs char) y Anfibio las extiende a las dos.
// Pato implementa Anfibio y define ambos mover sin disparar
// ERR_METODO_DUPLICADO ni ERR_METODO_INTERFAZ_NOIMPLEMENTADO.
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
