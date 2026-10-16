package com.bolivar.polizas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Punto de entrada de la aplicacion.
// @SpringBootApplication activa tres cosas:
//  1. Autoconfiguracion: Spring configura solo lo que encuentra en el pom (web, JPA, H2).
//  2. Escaneo de componentes: busca clases con @RestController, @Service, @Component, etc.
//     dentro de este paquete y sus subpaquetes, y las crea automaticamente.
//  3. Permite definir configuracion propia en esta clase si se necesita.
@SpringBootApplication
public class PolizasApplication {

    // main: lo primero que ejecuta Java. Arranca Spring y el servidor Tomcat en el puerto 8080.
    public static void main(String[] args) {
        SpringApplication.run(PolizasApplication.class, args);
    }
}
