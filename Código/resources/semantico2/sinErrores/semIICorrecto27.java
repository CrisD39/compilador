///[SinErrores]
// §11.2 -- Operadores bien tipados: aritmeticos, relacionales, logicos, unarios y
// igualdad.

class A1 {
    void m1(int a, int b, boolean c) {
        var v1 = a + b - a * b / b % a;
        var v2 = -a + +b;
        var v3 = (a < b) && (a <= b) || !(a > b) && (a >= b);
        var v4 = !c;
        var v5 = a == b;
        var v6 = c != true;
    }
}

class Init{
    static void main()
    { }
}
