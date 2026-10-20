///[Error:=|9]
// §9.4 -- Una fila de String[][] tiene tipo String[], que no conforma con Object[].
// Se reporta '=' en la linea 9.

class A1 {
    Object[] a1;

    void m1(String[][] p1) {
        a1 = p1[0];
    }
}

class Init{
    static void main()
    { }
}
