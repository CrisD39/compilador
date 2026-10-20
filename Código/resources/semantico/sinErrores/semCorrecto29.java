///[SinErrores]
// Extender Object explicitamente es valido, como en Java: Object es una
// clase predefinida que no es final. Tambien se puede redefinir su
// toString con la misma signatura.
class Animal extends Object {
    String toString(){
        return "animal";
    }
}

class Perro extends Animal {
}
