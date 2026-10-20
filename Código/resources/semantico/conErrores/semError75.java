///[Error:T|8]
// ERR_TIPO_GENERICO_NO_DECLARADO: el argumento de un 'extends' es un
// parametro de tipo fuera de alcance. Bb no es generica, asi que el T de
// Caja<T> no esta declarado.
class Caja<T> {
}

class Bb extends Caja<T> {
}
