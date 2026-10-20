///[Error:m|11]
// ERR_REDEFINE_ESTATICO: un metodo estatico no puede tener la clave de un
// metodo de instancia heredado. Hija hereda "void m()" de Base y declara
// "static void m()". Se reporta el metodo de Hija.
class Base {
    void m(){
    }
}

class Hija extends Base {
    static void m(){
    }
}
