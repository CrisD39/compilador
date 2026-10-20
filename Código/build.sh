#!/bin/bash
# Compila el compilador (léxico + sintáctico + semántico, etapa 3) y arma out/compilador.jar (REQ-MP-07).
# Uso:
#   ./build.sh
#   java -jar out/compilador.jar <archivo-fuente>
set -e
cd "$(dirname "$0")"

rm -rf out/classes
mkdir -p out/classes

javac -d out/classes $(find src/Model src/Controller src/View -name "*.java")

jar --create --file out/compilador.jar --main-class View.ModuloPrincipalET3 -C out/classes .

echo "Listo: out/compilador.jar"
