///[Error:return|7]
// §12.5 -- En un constructor no se permite return e;.
// Se reporta 'return' en la linea 7.

class A1 {
    public A1() {
        return this;
    }
}

class Init{
    static void main()
    { }
}
