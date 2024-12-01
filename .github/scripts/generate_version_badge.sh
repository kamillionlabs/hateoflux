#!/bin/bash

# Path to gradle.properties
GRADLE_PROPERTIES=./gradle.properties

# Badge label you want to display
BADGE_LABEL="development-version"

# Property key to extract from gradle.properties
PROPERTY_KEY="version"

# Function to URL-encode a string
urlencode() {
    local raw="$1"
    local encoded=""
    local length="${#raw}"
    for (( i = 0; i < length; i++ )); do
        local c="${raw:i:1}"
        case "$c" in
            [a-zA-Z0-9.~_-])
                encoded+="$c"
                ;;
            *)
                printf -v hex '%%%02X' "'$c"
                encoded+="$hex"
                ;;
        esac
    done
    echo "$encoded"
}

# Extract the property value using awk
PROPERTY_VALUE=$(awk -F= -v key="$PROPERTY_KEY" '
    $1 == key {
        # Remove leading and trailing whitespace
        gsub(/^[ \t]+|[ \t]+$/, "", $2)
        print $2
    }
' "$GRADLE_PROPERTIES")

# Check if PROPERTY_VALUE is empty
if [ -z "$PROPERTY_VALUE" ]; then
    echo "Error: Property '$PROPERTY_KEY' not found in $GRADLE_PROPERTIES."
    exit 1
fi

# Encode both the badge label and property value for URL
ENCODED_LABEL=$(urlencode "$BADGE_LABEL")
ENCODED_VALUE=$(urlencode "$PROPERTY_VALUE")

echo "Encoded Label: $ENCODED_LABEL"
echo "Encoded Version: $ENCODED_VALUE"

# Define badge color based on version type
if [[ "$PROPERTY_VALUE" == *"-SNAPSHOT"* ]]; then
    COLOR="orange"
elif [[ "$PROPERTY_VALUE" == *"-alpha"* || "$PROPERTY_VALUE" == *"-beta"* || "$PROPERTY_VALUE" == *"-rc"* ]]; then
    COLOR="yellow"
else
    COLOR="brightgreen"
fi

echo "Badge Color: $COLOR"

# Generate Shields.io badge URL using static badge endpoint
BADGE_URL="https://img.shields.io/static/v1?label=${ENCODED_LABEL}&message=${ENCODED_VALUE}&color=${COLOR}"

echo "Badge URL: $BADGE_URL"

# Ensure the badges directory exists
mkdir -p ./.github/badges

# Download the badge and capture HTTP status code
HTTP_STATUS=$(curl -s -w "%{http_code}" -o ./.github/badges/${BADGE_LABEL}.svg "$BADGE_URL")

# Check if the badge was downloaded successfully
if [ -n "$HTTP_STATUS" ] && [ "$HTTP_STATUS" -eq 200 ]; then
    echo "Badge downloaded successfully to ./.github/badges/${BADGE_LABEL}.svg"
else
    echo "Error: Failed to download the badge. HTTP status code: '$HTTP_STATUS'"
    exit 1
fi

