///[Error:Caja|7]
// ERR_TIPO_SIN_ARGUMENTO (ADR 0007, §5.2) -- tipo raw en un parametro de
// constructor (linea 7).
class Caja<T> {
}
class Uso {
    Uso(int n, Caja c) {
    }
}
