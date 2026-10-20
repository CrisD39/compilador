///[Error:Circulo|9]
// Forma es una interfaz comun (no sealed); Circulo se declara nonsealed
// al implementarla, pero no hay nada sealed que este reabriendo --
// mismo chequeo que para 'extends', ahora considerando 'implements'.
interface Forma {
    int area();
}

nonsealed class Circulo implements Forma {
    int area(){
        return 1;
    }
}
