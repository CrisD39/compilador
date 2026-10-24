///[SinErrores]
// Postfijo y ternario en la rama "." de <SentIdClase> y de <TrasIdClaseFor>
// (requerimientos.md, ⚠13): la cola de <Expresion> que se rearma a mano
// después de "idClase . idMetVar <ArgsActuales>" tiene que aceptar lo mismo
// que el camino normal. Sólo exige que parsee: semánticamente, la segunda
// sentencia no es válida.
class A1 {
    static A1 m() { return new A1(); }
    void m2() {
        A1.m().x++;
        A1.m().b ? 1 : 2;
        for (A1.m().x--; true; ) { }
    }
}
