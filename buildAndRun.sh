set -x

./gradlew clean assemble

docker build -t spring-testcontainers-image .

docker-compose up