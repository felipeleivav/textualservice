# Textual Service
This is the REST server for Textual App.

You can find Textual App sources here (ionic v1):
- https://github.com/selknam/textualapp

Textual server compiles to a single fat JAR file, which runs an embedded Jetty server. This sources also includes the neccesary files for running Textual server (shells and properties files).

This servers search automatically keystore files so you can enable SSL by default. If not found, will throw an alert.
