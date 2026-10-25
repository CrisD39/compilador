///[Error:i|12]
// La variable del for no se ve despues del for: su scope se cierra con el
// cuerpo (ERR_VAR_NO_DECLARADA sobre i).

class A1 {
    int a1;

    void m1() {
        for (var i = 0; i < 10; i = i + 1) {
            a1 = i;
        }
        a1 = i;
    }
}

class Init{
    static void main()
    { }
}
