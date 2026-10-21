///[Error:m|12]
// ERR_REDEFINICION_INCOMPATIBLE: una interfaz que redeclara un metodo
// heredado lo redefine, y la signatura tiene que ser exactamente igual
// (regla de la redefinicion, spec 0001). Bb extiende Aa y declara
// "String m()" contra el "int m()" de Aa: misma clave m/0, otro retorno. Se
// reporta sobre el m de Bb (linea 12), el que redefine. Cc cumple el
// contrato de Bb con su propio "String m()", sin otro error.
interface Aa {
    int m();
}
interface Bb extends Aa {
    String m();
}
class Cc implements Bb {
    String m() {}
}
