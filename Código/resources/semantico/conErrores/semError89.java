///[Error:get|12]
// ERR_METODO_RETORNO_INCOMPATIBLE: el conflicto entre superinterfaces se
// compara instanciado. Mixta extiende Fuente<String> y Origen<Object>: desde
// Mixta, el "T get()" de Fuente es "String get()" y el de Origen es
// "Object get()". Misma clave, otra signatura: se reporta sobre el get de
// Origen, el padre posterior en la lista de 'extends' (como semError62).
interface Fuente<T> {
    T get();
}

interface Origen<T> {
    T get();
}

interface Mixta extends Fuente<String>, Origen<Object> {
}
