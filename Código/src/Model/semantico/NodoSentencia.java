package Model.semantico;

/* Nodo que representa a las sentencias no es un nodo en sí
    De aqui heredan todas las clases que vienen a representar a los nodos sentencias
    Bloque, if, asignación, etc. Cosas llamables.
 */
public abstract class NodoSentencia {
    public abstract void chequearNodo();
}
