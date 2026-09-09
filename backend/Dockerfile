FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /src
COPY pom.xml .
COPY common/pom.xml common/pom.xml
COPY auth/pom.xml auth/pom.xml
COPY funeral/pom.xml funeral/pom.xml
RUN apt-get update && apt-get install -y -qq maven \
    && mvn -q -DskipTests dependency:go-offline || true
COPY common common
COPY auth auth
COPY funeral funeral
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /src/funeral/target/funeral-1.0.0.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
