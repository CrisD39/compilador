///[Error:T|6]
// ERR_TIPO_GENERICO_NO_DECLARADO (ADR 0006, §10.4) -- la prohibicion del
// parametro de tipo de la clase en un estatico tambien vale dentro de un
// tipo arreglo: el T de T[] (linea 6) no esta en alcance.
class Caja<T> {
    static T[] vacio() {
    }
}
