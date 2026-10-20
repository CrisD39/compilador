///[Error:m|11]
// ERR_REDEFINICION_INCOMPATIBLE: int[] e int son tipos distintos. Hija
// hereda "void m(int[] a)" y declara "void m(int a)": misma clave m/1,
// otra signatura. Se reporta el metodo de Hija.
class Base {
    void m(int[] a){
    }
}

class Hija extends Base {
    void m(int a){
    }
}
