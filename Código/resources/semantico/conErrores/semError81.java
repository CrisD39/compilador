///[Error:s|10]
// ERR_REDEFINE_ESTATICO: Hija hereda el estatico "static void s()" y declara
// un metodo de instancia con la misma clave s/0.
class Base {
    static void s(){
    }
}

class Hija extends Base {
    void s(){
    }
}
