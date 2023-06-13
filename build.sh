#!/bin/bash

# Build the game
rm -rf build/dependencies/
printf "Building...\n\n"

FOLDER="release"

if [ "$#" -lt 1 ]; then
    gradle build
    gradle copyRuntimeLibs
else
    FOLDER="release-$1"
    gradle build -Pos="$1"
    gradle copyRuntimeLibs -Pos="$1"
fi

# Copy assets
printf "\nCopying assets..."

mkdir -p "$FOLDER"
mkdir -p "$FOLDER/game"
cp build/libs/launcher.jar "$FOLDER/"
cp -r build/dependencies/ "$FOLDER/dependencies/"
cp -r licenses/ "$FOLDER/licenses/"

if [ "$1" == "windows" ] || [ "$1" == "windows-x86" ] || [ "$1" == "windows-arm64" ]; then
    cp extra/launch.bat "$FOLDER/launch.bat"
else
    cp extra/launch.sh "$FOLDER/launch.sh"
fi

mkdir -p "zips"
zip -r "zips/bbs-$FOLDER.zip" "$FOLDER/"