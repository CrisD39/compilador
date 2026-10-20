///[Error:s|14]
// ERR_REDEFINE_ESTATICO: un estatico heredado tampoco se puede volver a
// declarar como estatico en un descendiente. Nieta hereda "static void s()"
// de Base a traves de Hija.
class Base {
    static void s(){
    }
}

class Hija extends Base {
}

class Nieta extends Hija {
    static void s(){
    }
}
