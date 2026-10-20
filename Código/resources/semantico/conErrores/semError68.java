///[Error:Nada|8]
// ERR_TIPO_NO_DECLARADO: el argumento de un 'extends' se valida como
// cualquier tipo referencia. Caja existe y es generica, pero Nada no esta
// declarado.
class Caja<T> {
}

class Bb extends Caja<Nada> {
}
