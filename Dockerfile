# Используем официальный образ OpenJDK
FROM openjdk:23-jdk

# Указываем рабочую директорию в контейнере
WORKDIR /app

# Копируем JAR-файл из папки target
COPY target/DeliveryBackendApplication-0.0.1-SNAPSHOT.jar app.jar

# Указываем команду для запуска приложения
CMD ["java", "-jar", "app.jar"]

