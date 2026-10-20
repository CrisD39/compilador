///[Error:m|11]
// ERR_REDEFINICION_INCOMPATIBLE: Hija hereda "void m(int x)" y declara
// "void m(char x)". Misma clave m/1 con otro tipo de parametro: ya no es una
// sobrecarga, es una redefinicion con otra signatura.
class Base {
    void m(int x){
    }
}

class Hija extends Base {
    void m(char x){
    }
}
