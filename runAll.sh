#!/bin/bash

./gradlew :auth:bootRun &
./gradlew :transactions:bootRun &
./gradlew :app:bootRun &
wait
