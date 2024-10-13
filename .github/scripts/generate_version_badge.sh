#!/bin/bash

# Path to gradle.properties
GRADLE_PROPERTIES=./gradle.properties

# Property you want to display
PROPERTY_NAME=version

# Extract the property value using awk
PROPERTY_VALUE=$(awk -F= -v key="$PROPERTY_NAME" '$1 == key { print $2 }' $GRADLE_PROPERTIES)

# Encode the property value for URL (if necessary)
ENCODED_VALUE=$(echo $PROPERTY_VALUE | sed -e 's/ /%20/g')

echo $ENCODED_VALUE

# Generate Shields.io badge URL
BADGE_URL="https://img.shields.io/badge/${PROPERTY_NAME}-${ENCODED_VALUE}-brightgreen"

# Download the badge
curl -o ./.github/badges/${PROPERTY_NAME}.svg $BADGE_URL

# Commit the updated badge
#git config --local user.email "action@github.com"
#git config --local user.name "GitHub Action"
#git add badges/${PROPERTY_NAME}.svg
#git commit -m "Update ${PROPERTY_NAME} badge to ${PROPERTY_VALUE}"
#git push
