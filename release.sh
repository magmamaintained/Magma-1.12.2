#!/bin/sh

# This script is used to release Magma on GitLab CI

# Token argument is required
# Sha argument is required


TOKEN=$1
PROJECTID=$2
SHA=$3

release-cli --server-url https://git.magmafoundation.org --private-token=$TOKEN --project-id=$PROJECTID create --name "Magma 1.12.2-$SHA" --description "Magma Release" --tag-name "$SHA" --ref "$SHA" --assets-link "{\"url\":\"https://git.magmafoundation.org/api/v4/projects/7/packages/maven/org/magmafoundation/Magma/1.12.2-$SHA/Magma-1.12.2-$SHA.jar\",\"name\":\"Magma-1.12.2-$SHA.jar\"}"
