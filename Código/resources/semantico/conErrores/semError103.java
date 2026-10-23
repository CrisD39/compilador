///[Error:Caja|10]
// ERR_TIPO_SIN_ARGUMENTO (ADR 0007, §3) -- un generico sin argumento tampoco
// puede ser el argumento de otro: en Lista<Caja> (linea 10) el raw es Caja,
// no Lista.
class Caja<T> {
}
class Lista<T> {
}
class Uso {
    Lista<Caja> listas;
}
