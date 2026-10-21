///[SinErrores]
// Gemelo sano de semError91: una interfaz puede redeclarar un metodo
// heredado con la misma signatura. Bb redeclara "int m()" de Aa tal cual, y
// Gg redeclara el "T obtener()" de Ff<T> como "String obtener()", que es la
// misma signatura vista desde Gg (extends Ff<String>). Las clases cumplen
// cada contrato con la signatura de la interfaz.
interface Aa {
    int m();
}
interface Bb extends Aa {
    int m();
}
class Cc implements Bb {
    int m() {}
}
interface Ff<T> {
    T obtener();
}
interface Gg extends Ff<String> {
    String obtener();
}
class Hh implements Gg {
    String obtener() {}
}
