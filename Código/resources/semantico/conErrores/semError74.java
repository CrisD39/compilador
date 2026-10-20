///[Error:debugPrint|10]
// ERR_REDEFINE_ESTATICO tras un error de relacion: "extends Ii" se descarta
// (Ii es interfaz) y Bb queda con Object como padre efectivo. Por eso hereda
// el estatico debugPrint/1 y su "void debugPrint(int x)" es un error. El
// tester busca esta etiqueta, no la del extends.
interface Ii {
}

class Bb extends Ii {
    void debugPrint(int x){
    }
}
