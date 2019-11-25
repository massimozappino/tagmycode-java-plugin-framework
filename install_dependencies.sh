#!/usr/bin/env bash
set -e

sdk_project="tagmycode-java-sdk"
branch=$(git branch | grep \* | cut -d ' ' -f2)

rm -fr ${sdk_project}

echo "Installing ${sdk_project}, branch ${branch}"

git clone https://github.com/massimozappino/${sdk_project}.git
cd ${sdk_project}
git checkout ${branch}
mvn install -DskipTests
