///[Error:Circulo|5]
// Mismo chequeo que semError45 pero del lado INTERFAZ: "Circulo" esta en el
// permits de la sealed interface "Figura", pero no la implementa (ni
// ninguna otra interfaz la extiende con ese nombre).
sealed interface Figura permits Circulo{
}

class Circulo{
}
