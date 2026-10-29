///[Error:;|10]
// §12.2 -- Una expresion parentizada no es una sentencia valida, aunque
// adentro tenga un error de tipos: la forma se controla antes de tipar, como
// javac ("not a statement"; spec 0007, D5), asi que se reporta el ';'
// (ERR_SENTENCIA_INVALIDA) y no el '+' de 4 + true. Hermano de semIIError148.
// Se reporta ';' en la linea 10.

class Aa {
    void m1() {
        (4 + true);
    }
}
