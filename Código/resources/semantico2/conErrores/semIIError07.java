///[Error:i|14]
// §10.1 -- La local declarada en un bloque deja de ser visible al cerrarlo: el bloque
// hermano no la ve.
// Se reporta 'i' en la linea 14.

class A1 {
    int a1;

    void m1() {
        {
            var i = 1;
        }
        {
            a1 = i;
        }
    }
}

class Init{
    static void main()
    { }
}
