La idea de este archivo es convertilo en una skill para tener un agente que desarrolle un compilador


# Recorrido del chequeo semantico.
// 1. El parser recorre el código, llena la TS y arma los ASTs de cada método
parser.parse();

// 2. Primera pasada semántica: Verifica que los tipos declarados existan,
// que no haya herencia circular, e incorpora métodos/atributos heredados.
tablaSimbolos.chequearDeclaraciones();
tablaSimbolos.consolidar();

// 3. Segunda pasada semántica: Recién AHORA que todas las clases están
// completas y consolidadas, mandamos a chequear los cuerpos (los AST).
tablaSimbolos.chequearSentencias();