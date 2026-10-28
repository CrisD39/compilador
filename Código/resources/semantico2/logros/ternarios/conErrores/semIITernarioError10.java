///[Error:?|7]
// Los arreglos son invariantes: int[] e int[][] no conforman entre si
// (ERR_TERNARIO_INCOMPATIBLE, sobre el ?).

class A1 {
    void m1(boolean c, int[] a1, int[][] a2) {
        var v = c ? a1 : a2;
    }
}

class Init{
    static void main()
    { }
}
