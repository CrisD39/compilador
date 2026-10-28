///[Error:if|18]
///[Error:while|20]
///[Error:for|22]
///[Error:=|24]
// Un acceso con super en las condiciones y en el for-each da los mismos
// errores que cualquier expresion: if con int (ERR_CONDICION_NO_BOOLEAN,
// sobre if), while con String (sobre while), for-each sobre un int
// (ERR_FOREACH_NO_ITERABLE, sobre for) y super.x = super.s con String s
// (ERR_ASIGNACION_INCOMPATIBLE, sobre =).

class A1 {
    int x;
    String s;
}

class B2 extends A1 {
    void m1() {
        if (super.x) {
        }
        while (super.s) {
        }
        for (String e : super.x) {
        }
        super.x = super.s;
    }
}

class Init{
    static void main()
    { }
}
