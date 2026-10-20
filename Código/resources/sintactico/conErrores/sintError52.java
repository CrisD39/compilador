///[Error:,|5]
// ADR 0005: una clase implementa como maximo una interfaz. <HerenciaOpcional>
// acepta un unico <TipoReferencia> tras 'implements', asi que la coma que
// abre una segunda interfaz es inesperada (se esperaba '{').
class Malo implements Uno, Dos {

}

interface Uno{

}

interface Dos{

}
