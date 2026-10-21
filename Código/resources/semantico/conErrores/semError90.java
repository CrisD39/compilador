///[Error:)|10]
// Error SINTACTICO en un caso del semantico: falta el tipo del segundo
// parametro de m ("int a, )"). tipo() devuelve null tras el error y
// consolidar() corre igual, aun con errores sintacticos (CLAUDE.md). Antes
// de la correccion, el Parametro con tipo null tiraba NullPointerException
// en el control de declaraciones y no se imprimia ninguna etiqueta. Se
// reporta el ")" de la linea 10, donde se esperaba un tipo. No hay error
// semantico: el parametro sin tipo no se valida.
class Aa {
    void m(int a, ) {}
}
