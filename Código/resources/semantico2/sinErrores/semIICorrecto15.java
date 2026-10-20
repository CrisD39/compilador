///[SinErrores]
// §9.4 / 11.8 -- Un arreglo conforma con Object; length y toString() estan disponibles;
// matriz[0] es int[].

class A1 {
    int[] fila;

    void recibir(Object p1) {}

    void m1() {
        recibir(new int[10]);
        var matriz = new int[3][4];
        fila = matriz[0];
        var n = matriz.length + matriz[0].length;
        var s = fila.toString();
        matriz[1][2] = n;
        matriz[0] = fila;
    }
}

class Init{
    static void main()
    { }
}
