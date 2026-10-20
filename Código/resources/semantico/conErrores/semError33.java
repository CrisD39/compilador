///[Error:Uno|12]
// 'extends Uno, Dos, Uno' repite la misma interfaz en la lista, pero no en
// posiciones vecinas -- debe ser error (no deduplicarse en silencio). Se
// reporta la segunda aparicion. semError34 cubre la repeticion inmediata.
// (Antes probaba 'implements Uno, Uno'; ese control se retiro con ADR 0005.)
interface Uno {
}

interface Dos {
}

interface Malo extends Uno, Dos, Uno {
}
