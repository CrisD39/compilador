///[Error:;|7]
// Coma colgante en una local clásica: después de la "," <MasDeclaradores>
// pide otro <Declarador>, que empieza con idMetVar, y encuentra el ";"
// (spec 0009, D2).
class Coma{
    void m(){
        int x = 1, ;
    }
}
