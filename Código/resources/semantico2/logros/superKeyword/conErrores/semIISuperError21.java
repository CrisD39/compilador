///[Error:super|15]
///[Error:m|19]
// Herencia sin argumento de una clase generica (excepcion del ADR 0007): los
// miembros heredados de A1<T> conservan su T, que no se reemplaza por Object
// (spec 0001, D5), y ningun argumento concreto conforma con ella. super(5)
// da ERR_ARGUMENTO_INCOMPATIBLE sobre super, y super.m(5) sobre el nombre del
// metodo. En Java compilan los dos (T se borra a Object, con un warning
// unchecked).

class A1<T> {
    A1(T t) { }
    void m(T t) { }
}
class B2 extends A1 {
    B2() { super(5); }
}
class C3 extends A1 {
    C3() { super(null); }
    void p() { super.m(5); }
}

class Init{
    static void main()
    { }
}
