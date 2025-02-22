# Usando a imagem do OpenJDK 17
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o JAR da aplicação para o container
COPY target/core-1.0.0.jar app.jar

# Define o comando de execução do container
ENTRYPOINT ["java", "-jar", "app.jar"]
