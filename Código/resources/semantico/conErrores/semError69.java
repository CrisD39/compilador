///[Error:Bb|8]
// ERR_TIPO_NO_GENERICO: Bb no declara parametro de tipo, asi que no se puede
// extender con argumento (Bb<Bb>). Mismo chequeo que semError51, pero en un
// 'extends'.
class Bb {
}

class Cc extends Bb<Bb> {
}
