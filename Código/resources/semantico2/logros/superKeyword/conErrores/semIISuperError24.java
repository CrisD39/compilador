///[Error:super|18]
///[Error:super|19]
///[Error:super|23]
///[Error:super|30]
// Acceso con super en contexto estatico, aunque el miembro sea estatico o el
// acceso siga encadenado (ERR_SUPER_EN_ESTATICO, sobre super): super.s() y
// super.get().y en un metodo estatico, un return super.s() en otro, y
// super.toString() en el main de Init (cuya superclase es Object).

class A1 {
    int y;
    static int s() { return 1; }
    A1 get() { return this; }
}

class B2 extends A1 {
    static void m1() {
        var a = super.s();
        var b = super.get().y;
    }

    static int m2() {
        return super.s();
    }
}

class Init{
    static void main()
    {
        var t = super.toString();
    }
}
