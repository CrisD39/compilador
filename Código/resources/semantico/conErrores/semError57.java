///[Error:toString|6]
// ERR_REDEFINICION_INCOMPATIBLE: Punto hereda de Object "String toString()".
// "int toString()" tiene la misma clave (toString/0) pero otro retorno, asi
// que es una redefinicion con otra signatura. Se reporta el metodo de Punto.
class Punto {
    int toString(){
        return 0;
    }
}
