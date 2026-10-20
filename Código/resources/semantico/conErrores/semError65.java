///[Error:Ii|8]
// ERR_HERENCIA_TIPO_INCORRECTO: el 'extends' de una clase tiene que nombrar
// una clase, y Ii es una interfaz ("Ii no es una clase"). Lexema y linea del
// tipo nombrado. Antes, este programa hacia caer al compilador.
interface Ii {
}

class Bb extends Ii {
}
