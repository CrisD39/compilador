///[SinErrores]
// §11.4 / 11.9 -- La asignacion produce un valor: x = (y = 3), var z = (x = 3), return x = 3,
// y parentesis en expresiones.

class A1 {
    int a1;

    int m1(int x, int y) {
        x = (y = 3);
        var z = (x = 3);
        var total = (x + y) * z;
        var u = (m2());
        return a1 = 3;
    }

    int m2() {
        return 1;
    }
}

class Init{
    static void main()
    { }
}
