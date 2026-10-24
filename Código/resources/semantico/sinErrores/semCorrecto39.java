///[SinErrores]
// Spec 0002 (historias 11-16): toda interfaz contiene los metodos de
// instancia de Object.
// - Ii no declara toString: lo incorpora de Object.
// - Jj lo redeclara con la misma signatura: es valido.
// - Kk extiende Ii y Jj: el toString llega por los dos lados (el mismo de
//   Object) y no es conflicto.
// - Mm declara debugPrint(int) de instancia: el estatico de Object no pasa a
//   las interfaces, asi que no choca (una clase no podria declararlo,
//   ERR_REDEFINE_ESTATICO).
// - Cc implementa Kk sin declarar toString: lo tiene por Object.
interface Ii {
    void m1();
}
interface Jj {
    String toString();
}
interface Kk extends Ii, Jj {
}
interface Mm {
    void debugPrint(int nivel);
}
class Cc implements Kk {
    void m1() {}
}
