///[Error:x|8]
// Choca con una local visible de un bloque contenedor (spec 0009, D5).

class A1 {
    void m1(boolean c) {
        int x = 1;
        if (c) {
            int y, x = 2;
        }
    }
}

class Init{
    static void main()
    { }
}
