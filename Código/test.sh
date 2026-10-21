#!/bin/bash
# Compila el compilador y los testers, y corre los testers de una o más etapas.
# Imprime una línea por tester; si alguno falla, también sus fallas, y sale con 1.
# Uso:
#   ./test.sh            # Etapas 1-3 (léxico, sintáctico, semántico de declaraciones)
#   ./test.sh sem2       # sólo Sem2 (chequeo de sentencias, todavía en progreso)
#   ./test.sh todo       # Etapas 1-3 y Sem2
set -e
cd "$(dirname "$0")"

ETAPAS_1_3="TesterDeCasosSinErrores TesterDeCasosConErrores
TesterSintacticoDeCasosSinErrores TesterSintacticoDeCasosConErrores TesterSintacticoModoPanico
TesterSemanticoDeCasosSinErrores TesterSemanticoDeCasosConErrores"
SEM2="TesterSinErroresSem2 TesterConErroresSem2"

case "${1:-}" in
    "")   TESTERS="$ETAPAS_1_3" ;;
    sem2) TESTERS="$SEM2" ;;
    todo) TESTERS="$ETAPAS_1_3 $SEM2" ;;
    *)    echo "Uso: ./test.sh [sem2|todo]" >&2; exit 2 ;;
esac

./build.sh >/dev/null

# Se recompilan los testers desde cero para no correr clases viejas.
CP="out/classes:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar"
rm -rf out/test-classes
javac -nowarn -cp "$CP" -d out/test-classes $(find src/test -name "*.java") 2>&1 | grep -v '^Note:' || true

fallo=0
for t in $TESTERS; do
    salida=$(java -cp "$CP:out/test-classes" org.junit.runner.JUnitCore "test.java.$t" 2>&1) || true
    if echo "$salida" | grep -q '^OK ('; then
        echo "$t: $(echo "$salida" | grep '^OK (')"
    else
        fallo=1
        echo "$t: FALLA -- $(echo "$salida" | grep '^Tests run')"
        # Cada falla de JUnit empieza con "N) nombre"; la línea siguiente es el
        # mensaje. Se muestran las primeras 10 para no inundar la salida.
        echo "$salida" | grep -A1 -E '^([1-9]|10)\) ' | grep -v '^--$' | sed 's/^/    /'
        total=$(echo "$salida" | grep -cE '^[0-9]+\) ' || true)
        [ "$total" -gt 10 ] && echo "    ... y $((total - 10)) fallas más"
    fi
done
exit $fallo
