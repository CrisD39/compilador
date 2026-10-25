///[Error:yy|12]
///[Error:zz|16]
///[Error:ww|20]
// Los errores salen en el orden del programa aunque se mezclen metodos y
// constructores: m1 (linea 12) esta antes que el constructor (linea 16), y
// m2 (linea 20) despues.

class A1 {
    int a1;

    void m1() {
        a1 = yy;
    }

    A1() {
        a1 = zz;
    }

    void m2() {
        a1 = ww;
    }
}

class Init{
    static void main()
    { }
}
