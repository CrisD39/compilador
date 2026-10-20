///[SinErrores]
// Contrato cumplido por un metodo heredado: Imprimible exige
// "String toString()" y Hoja no lo declara, pero lo hereda de Object (toda
// clase que implementa una interfaz tiene a Object como padre).
interface Imprimible {
    String toString();
}

class Hoja implements Imprimible {
}
