module oscargrc.zoo {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires open;

    // Logger
    requires io.github.microutils.kotlinlogging;
    requires koin.logger.slf4j;
    requires org.slf4j;

    // Gson
    requires com.google.gson;

    // Result
    requires kotlin.result.jvm;

    // SqlDeLight
    requires runtime.jvm;
    requires sqlite.driver;
    // Como no pongas esto te vas a volver loco con los errores
    requires java.sql;

    // Koin
    requires koin.core.jvm;
    requires jdk.management.agent;


    // Abrimos y exponemos el paquete a JavaFX
    opens oscargrc.zoo to javafx.fxml;
    exports oscargrc.zoo;

    // Controladores
    opens oscargrc.zoo.controllers to javafx.fxml;
    exports oscargrc.zoo.controllers;

    // Rutas
    opens oscargrc.zoo.routes to javafx.fxml;
    exports oscargrc.zoo.routes;

    // dtos, abrimos a Gson
    opens oscargrc.zoo.dto.json to com.google.gson;

    // Modelos a javafx para poder usarlos en las vistas
    opens oscargrc.zoo.models to javafx.fxml;
    exports oscargrc.zoo.models;
}