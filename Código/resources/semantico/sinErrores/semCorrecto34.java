///[SinErrores]
// Contrato instanciado a traves de la cadena de la interfaz. Lista hereda
// "T get()" de Fuente y de Origen: son T distintos, pero desde Lista los dos
// son U, asi que no hay conflicto entre superinterfaces. Caja implementa
// Lista<String>: desde Caja, el "T get()" de Fuente es "String get()" y el
// "void poner(U valor)" de Lista es "void poner(String valor)".
interface Fuente<T> {
    T get();
}

interface Origen<T> {
    T get();
}

interface Lista<U> extends Fuente<U>, Origen<U> {
    void poner(U valor);
}

class Caja implements Lista<String> {
    String get(){
        return null;
    }

    void poner(String valor){
    }
}
