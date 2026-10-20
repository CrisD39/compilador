///[Error:=|10]
// §9.4 -- La cantidad de dimensiones forma parte del tipo: int[][] no conforma con
// int[].
// Se reporta '=' en la linea 10.

class A1 {
    int[] a1;

    void m1() {
        a1 = new int[2][3];
    }
}

class Init{
    static void main()
    { }
}
