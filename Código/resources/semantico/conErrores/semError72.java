///[Error:x|7]
// ERR_ATRIBUTO_DUPLICADO antes de un error de relacion: los errores
// detectados antes de "class Bb extends Ii" (Ii interfaz) se siguen
// imprimiendo. Antes, el compilador se caia y no imprimia ninguno.
class Aa {
    int x;
    int x;
}

interface Ii {
}

class Bb extends Ii {
}
