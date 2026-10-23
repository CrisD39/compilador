///[Error:Caja|7]
// ERR_TIPO_SIN_ARGUMENTO (ADR 0007, §5.2) -- tipo raw en un parametro de
// metodo (linea 7). El metodo es estatico: la regla vale igual.
class Caja<T> {
}
class Uso {
    static void poner(Caja c) {
    }
}
