///[SinErrores]
// Tipos predefinidos: Object, String y System se usan como tipos de
// atributos, parametros y retornos sin declararlos en el programa. Los
// registra la tabla antes de analizar el fuente (ADR 0004).
class Registro {
    Object dato;
    String nombre;
    System sistema;

    Registro(String n, Object d){
    }

    String getNombre(){
        return nombre;
    }

    Object getDato(){
        return dato;
    }

    System getSistema(){
        return sistema;
    }

    void guardar(Object o, String s, System sis){
    }
}
