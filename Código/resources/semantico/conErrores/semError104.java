///[Error:Lista|9]
// ERR_TIPO_SIN_ARGUMENTO (ADR 0007, §3) -- la excepcion de herencia sin
// argumento cubre solo la cabeza: en "extends Caja<Lista>" (linea 9) el
// argumento Lista es un tipo raw.
class Caja<T> {
}
class Lista<T> {
}
class Uso extends Caja<Lista> {
}
