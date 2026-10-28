///[Error:,|7]
// La inicialización del for admite un solo nombre (⚠9): <TrasIdForTipo> no
// pasa por <MasDeclaradores>, así que después de "int i = 0" espera el ";"
// y encuentra la "," (spec 0009, D2).
class ForDosNombres{
    void m(){
        for (int i = 0, j = 0; i < 10; i = i + 1) { }
    }
}
