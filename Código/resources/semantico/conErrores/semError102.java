///[Error:Caja|7]
// ERR_TIPO_SIN_ARGUMENTO (ADR 0007, §5.2) -- tipo raw como tipo base de un
// arreglo: Caja[] (linea 7). Caja<String>[] seria valido.
class Caja<T> {
}
class Uso {
    Caja[] cajas;
}
