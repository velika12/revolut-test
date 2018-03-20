# revolut-test

A RESTful non-blocking API for money transfers between accounts.

The project is based on Play Framework 2.5 (Java).

## Running tests
To run tests checking the API use `sbt test` in the folder with the source code.

## Building and running a binary
To obtain a binary version of application run `sbt dist` in the folder with the source code or download the [existing binary](https://github.com/velika12/revolut-test/releases/tag/1.0-SNAPSHOT).

This command produces a ZIP file containing all JAR files needed to run the application in the `target/universal` folder.

To run the application, unzip the file and then run the script in the `bin` directory.
```
unzip revolut-test-1.0-SNAPSHOT.zip
./revolut-test-1.0-SNAPSHOT/bin/revolut-test -Dplay.crypto.secret=abcdefghijk -Dplay.evolutions.db.default.autoApply=true
```
Note that you should have Java installed.