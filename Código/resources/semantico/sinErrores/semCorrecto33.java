///[SinErrores]
// Herencia sin argumento de un tipo declarado generico (Parte I, S5): es
// valida tanto en 'extends' de una clase como en 'implements' y en el
// 'extends' de una interfaz.
class Caja<T> {
}

class CajaCruda extends Caja {
}

interface Contenedor<T> {
}

class Bolsa implements Contenedor {
}

interface Deposito extends Contenedor {
}
