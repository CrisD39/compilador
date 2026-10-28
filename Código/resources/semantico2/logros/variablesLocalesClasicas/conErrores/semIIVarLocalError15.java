///[Error:Foo|8]
// Con un tipo invalido no se chequea ningun inicializador (spec 0009, D4):
// new NoExiste() no suma un error, e y queda declarada sin tipo, asi que
// la asignacion siguiente tampoco.

class A1 {
    void m1() {
        Foo x = new NoExiste(), y;
        y = 1;
    }
}

class Init{
    static void main()
    { }
}
