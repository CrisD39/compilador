///[Error:p1|8]
// §12.3 -- Un parametro no puede ser ocultado por una local de un bloque anidado.
// Se reporta 'p1' en la linea 8.

class A1 {
    void m1(int p1) {
        {
            var p1 = 2;
        }
    }
}

class Init{
    static void main()
    { }
}
