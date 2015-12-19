# lein-vanity

Lines of code statistics for vanity's sake

[![Build Status](https://travis-ci.org/dgtized/lein-vanity.svg)](https://travis-ci.org/dgtized/lein-vanity)

## Install

Modify `~/.lein/profiles.clj` to include:
```clojure
{:user {:plugins [[lein-vanity "0.2.0"]]}}
```

## Usage

```
$ lein vanity
|                        :source | :LOC | :comment | :blank |
|--------------------------------+------+----------+--------|
|       src/leiningen/vanity.clj |   61 |        0 |      9 |
| test/leiningen/vanity_test.clj |   23 |        0 |      3 |
|                        - Total |   84 |        0 |     12 |
```

## CLJS Support

Limited support for CLJS if using some forms of `lein-cljsbuild`
configuration in the `project.clj`. The intent is to limit statistics
to only those files in `:source-paths`, and `:test-paths` with a
`.clj` or `.cljs` extension.

## License

Copyright © 2012-2013 Charles L.G. Comstock

Distributed under the MIT Public License, see LICENSE file
