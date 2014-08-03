# pixi cljs

This is an experiment in using [Pixi.js](http://www.pixijs.com/) and [ClojureScript](http://clojurescript.com/)

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Setup

Install JavaScript dependencies with:

	npm install

## Running

To compile the clojurescript run:

	lein cljsbuild once

To setup auto complication run:

	lein cljsbuild auto


To start a web server for the application, run:

    lein ring server

## License

Some image assets are from kenney.nl

Copyright © 2014 Devon Tackett
