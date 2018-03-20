# revolut-test

A RESTful non-blocking API for money transfers between accounts.

The project is based on [Play Framework 2.5 (Java)](https://www.playframework.com/documentation/2.5.x/Home).

## Usage
Run the application and send the POST request with Json body. The response status is OK if everything's fine and BAD REQUEST if not.
```
http://localhost:9000/transfer

Body:
{
  "senderId":10,
  "receiverId":20,
  "amount":1000
}
```
You can do it for example with `curl`:
```
curl -i -d '{"senderId":10,"receiverId":20,"amount":1000}' localhost:9000/transfer --header "Content-Type:application/json"
```

## Running tests
To run tests checking the API use `sbt test` in the folder with the source code.

## Building and running a binary
To obtain a binary version of application run `sbt dist` in the folder with the source code or download the [existing binary](https://github.com/velika12/revolut-test/releases/tag/1.0-SNAPSHOT).

This command produces a ZIP file containing all JAR files needed to run the application in the `target/universal` folder.

To run the application, unzip the file and then run the script in the `bin` directory.
```
unzip revolut-test-1.0-SNAPSHOT.zip
./revolut-test-1.0-SNAPSHOT/bin/revolut-test
```
Note that you should have Java installed.
