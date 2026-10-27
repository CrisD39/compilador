///[Error:=|9]
// Un objeto del padre no conforma con un atributo de la subclase: B2 extiende
// a A1, asi que B2 b = new A1() es incompatible (ERR_ASIGNACION_INCOMPATIBLE,
// sobre el =). El gemelo sano, A1 a = new B2(), esta en Correcto02.

class A1 { }

class B2 extends A1 {
    B2 b = new A1();
}

class Init{
    static void main()
    { }
}
