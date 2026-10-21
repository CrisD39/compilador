///[Error:get|15]
// ERR_REDEFINICION_INCOMPATIBLE: el contrato se instancia por la cadena de
// la interfaz. Lista<U> extends Fuente<U> y Caja implementa Lista<String>,
// asi que desde Caja el "T get()" de Fuente es "String get()". Caja declara
// "Object get()": misma clave get/0, otra signatura. Se reporta sobre el
// metodo de Caja.
interface Fuente<T> {
    T get();
}

interface Lista<U> extends Fuente<U> {
}

class Caja implements Lista<String> {
    Object get(){
        return null;
    }
}
