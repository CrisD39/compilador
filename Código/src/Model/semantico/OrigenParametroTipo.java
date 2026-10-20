package Model.semantico;

// Marca de las entidades que pueden declarar un parámetro de tipo: Clase,
// Interfaz y Metodo (genericidad propia). Un Tipo que es parámetro de tipo
// (idGen) guarda cuál de ellas lo declara (Tipo.getOrigen), porque el §5.1
// pide "conservar su declaración de origen, además del nombre": en
// "class D<T> extends Base", el T de D y el T de Base son parámetros de tipo
// distintos aunque se llamen igual. Ver "Parámetros de tipo por origen" en
// analizador-semantico.md.
public interface OrigenParametroTipo {
}
