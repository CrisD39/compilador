///[Error:m|11]
// ERR_REDEFINICION_INCOMPATIBLE: Ii exige "void m(int x)" y Aa declara
// "void m(char c)". Misma clave m/1, otra signatura: no cumple el contrato.
// Se reporta sobre el metodo de Aa (no es ERR_METODO_INTERFAZ_NOIMPLEMENTADO,
// porque la clave esta).
interface Ii {
    void m(int x);
}

class Aa implements Ii {
    void m(char c){
    }
}
