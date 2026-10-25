package Model.semantico;

// Referencia de la forma de la interfaz predefinida MiniIterable<T> del logro
// For. El compilador no usa este tipo Java: la interfaz MiniJava que chequea
// el for-each la arma TablaSimbolos.registrarMiniIterable(). Si cambia una,
// hay que cambiar la otra.
public interface MiniIterable<T>{
    void start();
    T next();
    boolean hasNext();
}
