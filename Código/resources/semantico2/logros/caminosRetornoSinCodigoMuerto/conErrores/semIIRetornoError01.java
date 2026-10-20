///[Error:m1|6]
// Hay un camino (b falso) que llega al final del metodo sin return; se reporta
// sobre el metodo.

class A1 {
    int m1(boolean b) {
        if (b) {
            return 1;
        }
    }
}

class Init{
    static void main()
    { }
}
