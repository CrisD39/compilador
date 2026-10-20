///[SinErrores]
// Ave extiende dos interfaces sealed (Volador/Nadador, ambas la permiten)
// mas una interfaz comun (Comun) -- lista de 'extends' con tres entradas,
// mezclando sealed y no-sealed, todas satisfechas. Ave es nonsealed porque
// extiende interfaces sealed, y Pato la implementa (ADR 0005: una clase
// implementa una sola interfaz).
sealed interface Volador permits Ave {
    void volar();
}

sealed interface Nadador permits Ave {
    void nadar();
}

interface Comun {
    void comun();
}

nonsealed interface Ave extends Volador, Nadador, Comun {
}

final class Pato implements Ave {
    void volar(){
    }
    void nadar(){
    }
    void comun(){
    }
}
