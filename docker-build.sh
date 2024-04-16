#!/usr/bin/env bash

if [ "$1" = "native" ];
then
  ./mvnw clean -Pnative spring-boot:build-image -DskipTests
else
  ./mvnw clean compile jib:dockerBuild
fi
