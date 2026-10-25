///[SinErrores]
// Un private queda encapsulado: la subclase usa los metodos publicos que
// lo manejan. Redefinir un metodo con el mismo nombre que uno private del
// padre no es un error (la Etapa 3 no cambia), y un constructor private
// del padre no molesta a la subclase, porque MiniJava no tiene la llamada
// implicita a super() (Parte I 6.3).

class A1 {
    private int x;
    private A1() { }

    public A1(int v) { x = v; }

    int getX() { return x; }
    void setX(int v) { x = v; }

    private void interno() { }
}

class B2 extends A1 {
    B2() { }

    void interno() { }

    void m1(B2 otro) {
        setX(getX() + otro.getX());
        otro.interno();
    }
}

class Init{
    static void main()
    { }
}
