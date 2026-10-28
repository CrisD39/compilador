///[Error:m|12]
// 10.2 -- Herencia sin argumento de una clase generica (excepcion del ADR
// 0007): B2 hereda m(T t) de A1<T> y la T se conserva, no se reemplaza por
// Object (spec 0001, D5). Ningun argumento concreto conforma con T, asi que
// m(5) da ERR_ARGUMENTO_INCOMPATIBLE sobre el nombre del metodo. En Java
// compila (T se borra a Object, con un warning unchecked).

class A1<T> {
    void m(T t) { }
}
class B2 extends A1 {
    void p() { m(5); }
}

class Init{
    static void main()
    { }
}
