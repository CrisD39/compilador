#!/bin/bash
# Compila el compilador (léxico + sintáctico + semántico, etapa 4) y arma out/Compilador.jar (REQ-MP-07).
# Uso:
#   ./build.sh
#   java -jar out/Compilador.jar <archivo-fuente>
set -e
cd "$(dirname "$0")"

rm -rf out/classes
mkdir -p out/classes

javac -d out/classes $(find src/Model src/Controller src/View -name "*.java")

jar --create --file out/Compilador.jar --main-class View.ModuloPrincipalET4 -C out/classes .

echo "Listo: out/Compilador.jar"
