///[Error:pick|11]
// Dos argumentos de tipos distintos para el mismo T (int y String), sin cotas
// ni tipo comun.

class A1 {
    <T> T pick(T a, T b) {
        return a;
    }

    void m1() {
        var r = pick(1, "x");
    }
}

class Init{
    static void main()
    { }
}
