///[Error:zz|12]
///[Error:v|13]
///[Error:ww|17]
// Un var que falla no arrastra errores en cascada: x y v quedan declaradas
// sin tipo, y sus usos (lineas 14, 15, 16) no reportan nada. El resto se
// sigue chequeando: ww (linea 17) si se reporta.

class A1 {
    int a1;

    void m1() {
        var x = zz;
        var v = null;
        x = 1;
        var y = x;
        a1 = v;
        a1 = ww;
    }
}

class Init{
    static void main()
    { }
}
