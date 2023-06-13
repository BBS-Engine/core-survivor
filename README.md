# Core Survivor

**Core Survivor** is a basic rogue-lite clicker game where the goal is to protect the core from incoming hordes of alien spaceships. This game is written in Java using [BBS engine](https://github.com/BBS-Engine/bbs).

You can download and play on [itch.io](https://mchorse.itch.io/core-survivor).

## Building

To build, you need Java **8+** and Gradle **7.5.1**.

You need a [BBS build](https://github.com/BBS-Engine/bbs/releases). Place the file in `libs/` (create it if it doesn't exist) folder. For more information check `dependencies {}` block in `build.gradle`.

As for IDE, BBS was developed in IntelliJ **2022.3.1** (Community Edition). Build is as easy as executing `./buil.sh`, that should compile BBS to `release/`, and you should be able to run it by double-clicking `launcher.jar`.

## Developing

To launch Core Survivor in IntelliJ, you need to create an Application run configuration with following options:

* Module: `Core_Survivor.main`
* JVM arguments: `-Dfile.encoding=UTF-8`
* Prorgram arguments: `--gameDirectory $ProjectFileDir$\game-2d\ --development --width 1280 --height 720`
* Main class: `mchorse.game.Game2D`

You'll need to create folder `game-2d/` in the root of the project.