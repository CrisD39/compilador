///[Error:Caja|7]
// ERR_TIPO_SIN_ARGUMENTO (ADR 0007, §5.2) -- tipo raw en un atributo: Caja es
// generica y se usa sin argumento (linea 7).
class Caja<T> {
}
class Uso {
    Caja dato;
}
