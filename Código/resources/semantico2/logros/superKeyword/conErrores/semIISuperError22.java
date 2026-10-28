///[Error:equals|10]
// super.equals(o) en una clase sin extends: su superclase es Object, que en
// MiniJava solo tiene debugPrint y toString (ERR_METODO_NO_ENCONTRADO,
// sobre equals). En Java compila.

class A1 {
    boolean b;

    void m1(Object o) {
        b = super.equals(o);
    }
}

class Init{
    static void main()
    { }
}
