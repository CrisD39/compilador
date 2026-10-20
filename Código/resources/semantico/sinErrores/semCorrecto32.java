///[SinErrores]
// Un metodo de interfaz con la clave de debugPrint (debugPrint/1) no choca
// con Object: el debugPrint de Object es estatico, y los metodos de una
// interfaz solo se comparan con los metodos de instancia de Object.
interface Depurable {
    void debugPrint(int nivel);
}
