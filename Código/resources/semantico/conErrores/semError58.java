///[Error:debugPrint|6]
// ERR_REDEFINE_ESTATICO: Object declara "static void debugPrint(int i)" y
// toda clase lo hereda. Ningun descendiente puede declarar otro metodo con
// la clave debugPrint/1, ni siquiera con la misma signatura.
class Depurador {
    void debugPrint(int x){
    }
}
