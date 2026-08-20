FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build
# Copy toàn bộ mã nguồn project vào builder để Maven Reactor đọc đủ cấu trúc
COPY . .
# Build riêng course-service và các module nó phụ thuộc
RUN mvn clean package -pl course-service -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/course-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]