# Ultra chat

An app that demonstrates server-sent events with [http-kit](https://github.com/http-kit/http-kit).

## Requirements

* [Java 8+](https://adoptium.net/)
* [Clojure](https://clojure.org/)

## Development

If you're a beginner to Clojure and don't have a favorite setup yet, I recommend using [Visual Studio Code](https://code.visualstudio.com/) in combination with the [Calva extension](https://calva.io/).

Once you've installed Visual Studio Code and Calva, [connect Calva to the project](https://calva.io/connect/) and start development.

A convenient way to get started is opening `src/ultra-chat/core.clj` and evaluating that file by pressing `ctrl+alt+c enter`.
Afterwards, put you cursor somewhere inside an expression and press `alt+enter` to evaluate it.

## Running the app

Run `clojure -X:run` to start the app.
Obviously, this requires Clojure.

## Creating an uberjar

Run `clojure -T:build clean && clojure -T:build uber` to create an uberjar.
Run `java -jar target/ultra-chat-<version>-standalone.jar` to start the application.
Starting the application like this doesn't require Clojure, only Java.
