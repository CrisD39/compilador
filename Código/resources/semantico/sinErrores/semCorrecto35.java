///[SinErrores]
// Gemelo sano de semError92/93: Dd llega a Aa por Bb y por Cc con el MISMO
// argumento (Aa<String> por los dos lados). No hay conflicto: los dos
// caminos dan la misma instanciacion, y Ee cumple el contrato de Dd con
// "String m()" (el "T m()" de Aa visto desde Dd).
interface Aa<T> {
    T m();
}
interface Bb extends Aa<String> {
}
interface Cc extends Aa<String> {
}
interface Dd extends Bb, Cc {
}
class Ee implements Dd {
    String m() {}
}
