///[Error:toString|6]
// ERR_REDEFINICION_INCOMPATIBLE: los metodos de una interfaz se comparan con
// los metodos de instancia de Object con la regla de la redefinicion.
// "int toString()" tiene la clave de "String toString()" con otro retorno.
interface Ii {
    int toString();
}
