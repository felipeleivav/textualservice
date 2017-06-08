#!/usr/bin/sh

rm -R dist
mkdir dist
mvn -X compile
mvn -X package
cp ./target/TextualService*with-dependencies.jar ./dist
cp ./config/* ./dist
mv ./dist/TextualService-*.jar ./dist/TextualService.jar
