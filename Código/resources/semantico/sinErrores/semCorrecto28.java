///[SinErrores]
// Parametros de tipo de metodo: se comparan por posicion, no por nombre.
// "<V> V id(V x)" redefine "<U> U id(U x)" en una subclase, y cumple el
// contrato de una interfaz que declara "<U> U id(U x)".
class Base {
    <U> U id(U x){
        return x;
    }
}

class Derivada extends Base {
    <V> V id(V x){
        return x;
    }
}

interface Identidad {
    <U> U id(U x);
}

class Implementacion implements Identidad {
    <V> V id(V x){
        return x;
    }
}
