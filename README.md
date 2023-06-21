# Sleep Tracker

Project requirements: https://www.codementor.io/projects/web/daily-sleep-tracker-web-app-byi4kpk5rt

## Installation

This project uses the [Clojure CLI](https://clojure.org/guides/deps_and_cli) as the main package manager as well as [docker](https://www.docker.com/) to run a local database. For the frontend build, it uses [shadow-cljs](https://github.com/thheller/shadow-cljs) and [node.js](https://nodejs.org/en)

## Stack

- database
  - [postgres](https://www.postgresql.org/) - SQL database
  - [next.jdbc](https://github.com/seancorfield/next-jdbc) - clojure database access
  - [honey sql](https://github.com/seancorfield/honeysql) - write database queries as clojure maps
  - [hikari-cp](https://github.com/tomekw/hikari-cp) - jdbc connection pool
- routing
  - [reitit](https://github.com/metosin/reitit) - data driven routing library
  - [swagger](https://swagger.io/) - api documentation
  - [malli](https://github.com/metosin/malli) - schema definitions and coercion
- utility
  - [integrant](https://github.com/weavejester/integrant) - clojure state management
- frontend
  - [reagent](https://reagent-project.github.io/) - [react](https://react.dev/) for clojurescript
  - [re-frame](http://day8.github.io/re-frame/) - state management system
  - [tailwindcss](https://tailwindcss.com/) - utility css framework
  - [headless ui](https://headlessui.com/) - unstyled utility components

## Usage

First download and run the postgres instance using `docker compose` inside of the working directory.

```bash
$ docker compose up -d
$ docker ps -a
CONTAINER ID   IMAGE                        COMMAND                  CREATED        STATUS                      PORTS                                       NAMES
3e0fb42d3001   postgres                     "docker-entrypoint.s…"   20 hours ago   Up 33 minutes
```

Now you can run the main function using the `:server` alias and access the http server on port 8080. To run the frontend application use the npm start command.

```bash
$ clj -M:server
$ npm start

```
