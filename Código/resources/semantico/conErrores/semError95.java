///[Error:T|8]
// ERR_TIPO_GENERICO_NO_DECLARADO (ADR 0006, §10.4) -- el parametro de tipo
// de la clase no puede usarse en el encabezado de un metodo estatico. La
// firma de un estatico se valida sin los parametros de la clase, asi que el
// T de Caja queda fuera de alcance. Hay dos apariciones en la linea 8
// (retorno y parametro): cada una es un error, y la etiqueta es la misma.
class Caja<T> {
    static T identidad(T valor) {
        return valor;
    }
}
