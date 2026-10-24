///[SinErrores]
// Gemelo sano de semError98-104 (ADR 0007): la herencia sin argumento sigue
// siendo valida (§5), en extends de clase, implements y extends de interfaz.
// Los usos con argumento, en todas las posiciones, tambien. (Marca no
// declara metodos con T: Lector no podria cumplirlos sin argumento, §5.)
interface Fuente<T> {
    T leer();
}
interface Origen extends Fuente {
}
interface Marca<T> {
}
class Caja<T> {
    T contenido;
}
class Lista<T> {
}
class Simple extends Caja {
}
class Lector implements Marca {
}
class Uso {
    Caja<String> texto;
    Caja<String>[] cajas;
    Lista<Caja<String>> anidada;
    Caja<Uso> obtener(Lista<String> origen) {
    }
}
