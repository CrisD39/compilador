///[Error:obtener|11]
// ERR_REDEFINICION_INCOMPATIBLE: Aa implementa Ii sin argumento. El T de Ii
// se conserva, no se reemplaza por Object (Parte I, S5), asi que
// "Object obtener()" tiene la clave de "T obtener()" pero otra signatura.
// Se reporta sobre el metodo de Aa.
interface Ii<T> {
    T obtener();
}

class Aa implements Ii {
    Object obtener(){
        return null;
    }
}
