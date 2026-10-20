///[Error:getValor|10]
// Alfa exige "int getValor()" y Beta exige "char getValor()" -- misma
// clave pero retornos incompatibles. AlfaBeta las hereda a las dos, asi que
// es un conflicto real de Java. Se reporta sobre el getValor de Beta.
interface Alfa {
    int getValor();
}

interface Beta {
    char getValor();
}

interface AlfaBeta extends Alfa, Beta {
}

class Gama implements AlfaBeta {
}
