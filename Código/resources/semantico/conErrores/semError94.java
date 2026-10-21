///[Error:toString|13]
// ERR_REDEFINICION_INCOMPATIBLE: Cc implementa Ii y declara
// "int toString()". Choca con el toString de Object (redefinicion) y con el
// "String toString()" de Ii (contrato), que es el mismo de Object. Es un
// solo error y se tiene que reportar UNA vez, sobre el toString de Cc
// (linea 13). Antes de la correccion salia dos veces: desde la
// redefinicion y desde el contrato. El tester solo busca la etiqueta, asi
// que la repeticion se controla a mano (ver casos-de-prueba.md, 5.6).
interface Ii {
    String toString();
}
class Cc implements Ii {
    int toString() {}
}
