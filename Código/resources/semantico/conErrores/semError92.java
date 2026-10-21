///[Error:Dd|15]
// ERR_HERENCIA_ARGUMENTOS_DISTINTOS: un mismo ancestro generico no se puede
// heredar con dos argumentos distintos (como en Java). Dd llega a Aa por Bb
// (Aa<String>) y por Cc (Aa<Object>). Se reporta sobre Dd (linea 15), la
// interfaz que une los dos caminos, y el resultado no depende del orden del
// 'extends': semError93 es este mismo caso con "extends Cc, Bb". Antes de la
// correccion ganaba el ultimo padre y el conflicto no se reportaba.
interface Aa<T> {
    T m();
}
interface Bb extends Aa<String> {
}
interface Cc extends Aa<Object> {
}
interface Dd extends Bb, Cc {
}
