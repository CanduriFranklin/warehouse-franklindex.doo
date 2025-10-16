#!/bin/bash
# Script to run Spring Boot application with .env file loaded

# Load environment variables from .env file (ignoring comments and empty lines)
set -a
source <(grep -v '^#' .env | grep -v '^$' | sed 's/\r$//')
set +a

# Run the application
./gradlew bootRun
