///[SinErrores]
// Alfa y Beta exigen la misma clave Y el mismo tipo de retorno
// ("int getValor()") -- no hay conflicto al heredarlas juntas en AlfaBeta,
// y Gama las satisface a ambas con una sola implementacion.
interface Alfa {
    int getValor();
}

interface Beta {
    int getValor();
}

interface AlfaBeta extends Alfa, Beta {
}

class Gama implements AlfaBeta {
    int getValor(){
        return 1;
    }
}
