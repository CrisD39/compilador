///[Error:m|10]
// ERR_METODO_RETORNO_INCOMPATIBLE: Xx extiende Ii1 e Ii2, que traen la misma
// clave m/1 con signaturas distintas (int vs char). Xx hereda las dos, y es
// un conflicto. Se reporta sobre el m de Ii2.
interface Ii1 {
    void m(int x);
}

interface Ii2 {
    void m(char c);
}

interface Xx extends Ii1, Ii2 {
}
