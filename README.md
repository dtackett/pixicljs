# pixi cljs

This is an experiment in using [Pixi.js](http://www.pixijs.com/) and [ClojureScript](http://clojurescript.com/)

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To compile the clojurescript run:

	lein cljsbuild once

To setup auto complication run:

	lein cljsbuild auto


To start a web server for the application, run:

    lein ring server

## License

Copyright © 2013 Devon Tackett