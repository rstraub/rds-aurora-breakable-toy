# Scala Kata Base

[![Scala CI](https://github.com/rstraub/scala-kata-base/actions/workflows/scala.yml/badge.svg)](https://github.com/rstraub/scala-kata-base/actions/workflows/scala.yml)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

Starter project for Code Katas in Scala. Batteries included.

Included:

* [Sbt](https://www.scala-sbt.org/) (build tool)
* [Sbt Extras](https://github.com/dwijnand/sbt-extras) (wrapper script)
* [Scalatest](https://www.scalatest.org/) (testing library)
* [ScalaCheck](https://www.scalatest.org/plus/scalacheck) (property-based testing library)
* [scoverage](https://github.com/scoverage) (code coverage)
* [Mockito](https://www.scalatest.org/plus/mockito) (mocking library)
* [Cats](https://typelevel.org/cats/) (functional programming library)
* [Scalafmt](https://scalameta.org/scalafmt/) (code formatting)
* [Scalafix](https://scalacenter.github.io/scalafix/) (refactoring and linting)
* [Scala Steward](https://github.com/scala-steward-org/scala-steward) (automatic dependency updates)
* CI using [Github Actions](https://github.com/features/actions)
* Fast feedback using git hooks (pre-commit, pre-push)

## Installation

This project requires a JDK on your machine, as described in the `.sdkmanrc`. If you use sdkman `cd` into the
root of this project and run:

```shell
sdk env install
```

to install it. You can also install them yourself.

The build tool (Sbt) and Scala are installed automatically by the wrapper script located at `./sbtx`.
It will grab the correct versions based off the project configuration.

## Compile

```shell
./sbtx compile
```

## Tests

Run tests once:

```shell
./sbtx test
```

Or in watch mode:

```shell
./sbtx ~test
```

## Formatting

```shell
./sbtx scalafmtAll
```

```shell
./sbtx scalafixAll
```

## Code Coverage

For generating a code coverage report run:

```shell
./sbtx clean coverage test coverageReport
```

Which generates a report in `target/scala-2.13/scoverage-report`.

## Git Hooks

To install the git hooks run:
```shell
./hooks/install
```

## IDE

If you're using Intellij, make sure you have the [Scala plugin](https://plugins.jetbrains.com/plugin/1347-scala)
installed.
