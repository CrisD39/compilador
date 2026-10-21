///[Error:Dd|15]
// ERR_HERENCIA_ARGUMENTOS_DISTINTOS: el mismo caso que semError92 con el
// 'extends' de Dd al reves ("extends Cc, Bb"). Dd llega a Aa por Cc
// (Aa<Object>) y por Bb (Aa<String>), y el error sale igual, sobre Dd
// (linea 15). Antes de la correccion, con este orden ganaba Aa<String>, y
// una clase que implementaba Dd con "String m()" pasaba sin errores, cuando
// con el otro orden fallaba.
interface Aa<T> {
    T m();
}
interface Bb extends Aa<String> {
}
interface Cc extends Aa<Object> {
}
interface Dd extends Cc, Bb {
}
