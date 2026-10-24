package Model.semantico;

import Model.lexico.Token;

import java.util.List;
import java.util.Map;

public interface EntidadDeclarada {
    String getName();

    // Línea de la declaración (token de nombre) -- para reportar duplicados
    // sobre la entidad que efectivamente dispara el error.
    int getLinea();

    // Parámetro de tipo declarado (0 o 1 elemento) -- para saber si la
    // entidad acepta un argumento genérico al usarla como tipo.
    List<Token> getParametrosTipo();

    // Método con esa clave (nombre/aridad), propio o ya copiado de un
    // ancestro; null si no hay. Lo usan las llamadas del chequeo de
    // sentencias (spec 0003, ticket 04), que reciben una clase o una
    // interfaz según el tipo estático del receptor.
    Metodo buscarMetodo(String clave);

    // Para cada ancestro genérico, el argumento con que esta entidad lo
    // hereda (ver Clase/Interfaz). Lo compone TablaSimbolos.instanciacionDe()
    // con el argumento del receptor (spec 0003, D10).
    Map<OrigenParametroTipo, Tipo> getInstanciacion();
}
