///[SinErrores]
// Variable LOCAL de tipo arreglo (spec 0009, D2): <RestoDeclLocal> arranca
// con <DimensionesOpcionales>, igual que <CuerpoMiembro> para atributos, así
// que "int[] x;" dentro de un método es una declaración válida. Antes daba
// error sobre el "[", porque se esperaba el nombre justo después del tipo.
class LocalArreglo{

    void metodo(){
        int[] x;
    }

}
