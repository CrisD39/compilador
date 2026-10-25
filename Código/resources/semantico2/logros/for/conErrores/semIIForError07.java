///[Error:i|9]
// El var del for sigue la regla de ocultamiento: no puede repetir el nombre
// de un parametro (ERR_LOCAL_DUPLICADA sobre i).

class A1 {
    int a1;

    void m1(int i) {
        for (var i = 0; i < 10; i = i + 1) {
            a1 = i;
        }
    }
}

class Init{
    static void main()
    { }
}
