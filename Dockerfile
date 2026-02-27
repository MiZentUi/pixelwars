FROM maven AS builder
WORKDIR /app
COPY . /app/
RUN mvn clean package -Dmaven.test.skip=true

FROM eclipse-temurin
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/*.jar
COPY .env /app
EXPOSE 80
CMD ["java", "-jar", "/app/*.jar"]
