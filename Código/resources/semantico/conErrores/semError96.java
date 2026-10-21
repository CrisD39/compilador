///[Error:T|7]
// ERR_TIPO_GENERICO_NO_DECLARADO (ADR 0006, §10.4) -- la prohibicion del
// parametro de tipo de la clase en un estatico tambien vale cuando aparece
// como argumento generico. Caja<T> existe, pero el T de la linea 7 no esta
// en alcance en la firma de un metodo estatico.
class Caja<T> {
    static void poner(Caja<T> c) {
    }
}
