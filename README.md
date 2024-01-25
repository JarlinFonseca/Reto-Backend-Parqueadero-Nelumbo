<p align="center"><a href="https://www.linkedin.com/company/nelumbo-consultores/" target="_blank"><img src="https://github.com/JarlinFonseca/Reto-Backend-Parqueadero-Nelumbo/assets/48332117/4a575e1d-daca-4ea9-96ed-7d77b825f5ac" width="400"></a></p>

# Reto Técnico Nelumbo Backend: Parqueadero

## Tabla de Contenido
1. [Descripción](#descripción)
2. [Tecnologías](#tecnologías)
3. [Modelo Relacional BD](#modelo-relacional-bd)
4. [Requisitos](#requisitos)
5. [Herramientas Recomendadas](#herramientas-recomendadas)
6. [Instalación y Uso](#instalación-y-uso)
7. [Autor](#autor)


___
### Descripción: 

Esta APIREST está diseñada para gestionar el control de vehículos en parqueaderos de varios socios, ofreciendo funcionalidades como el registro de vehículos, indicadores y un historial de vehículos parqueados. Además, se implementa un sistema de usuarios con roles (ADMIN, SOCIO) y protección mediante autorización JWT token con una expiración de 6 horas.

___
### Tecnologías:
#### Backend

- [Java (JDK 11)](https://www.oracle.com/co/java/technologies/javase/jdk11-archive-downloads.html "Java JDK 11") : Java es un lenguaje de programación de propósito general que se diseñó para tener la menor cantidad de dependencias de implementación posibles. Es conocido por ser multiplataforma, lo que significa que las aplicaciones Java pueden ejecutarse en diferentes sistemas operativos sin necesidad de recompilar el código fuente.
- [Spring Boot](https://spring.io/projects/spring-boot/ "Spring Boot"):  Spring Boot es un framework desarrollado para el trabajo con Java como lenguaje de programación. Se trata de un entorno de desarrollo de código abierto y gratuito. Spring Boot cuenta con una serie de características que han incrementado su popularidad y, en consecuencia, su uso por parte de los desarrolladores back-end.
- [Spring Security](https://spring.io/projects/spring-security/ "Spring Security"): Spring Security es el módulo del proyecto Spring para incorporar seguridad de acceso a las aplicaciones hechas con Spring Boot. Permite controles de acceso por URL entre otras muchas opciones y es más que suficiente para proteger tu programa.
- [Token JWT (JSON Web Token)](https://jwt.io/ "Token JWT (JSON Web Token)"): JWT es un estándar abierto (RFC 7519) que define una forma compacta y autocontenida de representar información entre dos partes. La información puede ser verificada y confiable, ya que está firmada digitalmente.
- [PostgreSQL](https://www.postgresql.org/ "PostgreSQL"): PostgreSQL es un sistema de gestión de bases de datos relacional y de código abierto. Es conocido por su robustez, capacidad de gestión de grandes cantidades de datos y soporte para funciones avanzadas.


___
### Modelo Relacional BD:

![Modelo relacional Parqueadero drawio](https://github.com/JarlinFonseca/Reto-Backend-Parqueadero-Nelumbo/assets/48332117/24704d6d-4a79-4f78-a562-5947fd14200d)


___

### Requisitos:

- [JDK 11 o superior](https://www.oracle.com/co/java/technologies/javase/jdk11-archive-downloads.html "JDK 11 o superior")
- [Gradle](https://gradle.org/ "Gradle")
- [PostgreSQL](https://www.postgresql.org/ "PostgreSQL")
- [Git](https://git-scm.com/ "Git")

___

### Herramientas Recomendadas:

- [IntelliJ IDEA Community](https://www.jetbrains.com/idea/download/ "IntelliJ IDEA Community")
- [Postman](https://www.postman.com/ "Postman")
- [DBeaver Community](https://dbeaver.io/ "DBeaver Community")

___


### Instalación y Uso:


##### 1. Clonar el repositorio del proyecto
Para clonar el proyecto abre una terminal o consola de comandos y escribe el siguiente comando, esto es después de la instrucción git clone agrega la dirección GitHub:

```sh
git clone https://github.com/JarlinFonseca/Reto-Backend-Parqueadero-Nelumbo.git
```

##### 2. Crear dos base de datos para los dos microservicios
Crear dos base de datos local en PostgreSQL:
- La primera de nombre "parqueadero" para el microservicio de parqueadero-service.
- La segunda de nombre "correo" para el microservicio de correo-service.

![BD local](https://github.com/JarlinFonseca/Reto-Backend-Parqueadero-Nelumbo/assets/48332117/b25228f5-2055-4ae1-89b4-6f4b3fd292e2)


##### 3.Abrir las dos carpetas de los microservicios de forma independiente en el IDE
Luego de crear las base de datos en local, se abren las carpetas de ambos microservicios de forma independiente de preferencia en el IDE de IntelliJ IDEA Community y se le da en el botón de "Trust project", ambos proyectos abrirán, y empezaran a descargar las dependencias internamente.

![carpetas proyecto](https://github.com/JarlinFonseca/Reto-Backend-Parqueadero-Nelumbo/assets/48332117/504659be-44dc-4adb-83d3-3bd0d76226d9)


##### 4. Configurar variables de entorno en el microservicio de parqueadero
Nos vamos al microservicio del parqueadero, en la estructura del proyecto nos ubicamos en src/main/resources/application-dev.yml y encontrarás las propiedades y variables de entorno(ENV), tienes que agregar los valores de las ENV en el IDE que son las siguientes (ejemplo con los datos que agregue con mi configuración de BD local):

- URL_DB : jdbc:postgresql://localhost:5432/parqueadero
- USERNAME_DB: postgres
- PASSWORD_DB: admin
- DDL_AUTO: create(Si es la primera vez que abres el proyecto) o update(cuando ya se ha importado el import.sql y creado las tablas en la BD)
  #### NOTA: En la propiedad de spring.jpa.hibernate.ddl-auto, en el ${DDL_AUTO} si es la primera vez que abre el proyecto debe colocar "create" para que le importe los datos en la BD y se creen las tablas sin ningún problema, ya cuando se importen los datos y las tablas esten creadas, cambias el valor de la variable de entorno ${DDL_AUTO} en "update".
- ACCESS_TOKEN_SECRET: (codigo muy secreto ej: VEVFZ1EwOU9WRkpC) : en este apartado agregas un código secreto tuyo que no debe saber nadie, y es necesario para firmar el token JWT y tener siempre segura la API.

Valores de Variables de Entorno en IntelliJ IDEA Community (Parqueadero)

![ENV parqueadero](https://github.com/JarlinFonseca/Reto-Backend-Parqueadero-Nelumbo/assets/48332117/a7747b73-c8f9-40e6-b5d7-4f147494f026)



```sh
-> src/main/resources/application-dev.yml
spring:
  datasource:
    url: ${URL_DB}
    username: ${USERNAME_DB}
    password: ${PASSWORD_DB}
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQL95Dialect
    hibernate:
      ddl-auto: ${DDL_AUTO}
    show-sql: true

ACCESS_TOKEN_SECRET: ${ACCESS_TOKEN_SECRET}
```


##### 5. Configurar variables de entorno en el microservicio de correo
Posteriormente vamos al microservicio de correo, en la estructura del proyecto nos ubicamos en src/main/resources/application-dev.yml y encontrarás las propiedades y variables de entorno(ENV), tienes que agregar los valores de las ENV en el IDE que son las siguientes (ejemplo con los datos que agregue con mi configuración de BD local):

- URL_DB : jdbc:postgresql://localhost:5432/correo
- USERNAME_DB: postgres
- PASSWORD_DB: admin
- DDL_AUTO: create(Si es la primera vez que abres el proyecto) o update(cuando ya se ha importado el import.sql y creado las tablas en la BD)
  #### NOTA: En la propiedad de spring.jpa.hibernate.ddl-auto, en el ${DDL_AUTO} si es la primera vez que abre el proyecto debe colocar "create" para que le importe los datos en la BD y se creen las tablas sin ningún problema, ya cuando se importen los datos y las tablas esten creadas, cambias el valor de la variable de entorno ${DDL_AUTO}  en "update".


Valores de Variables de Entorno en IntelliJ IDEA Community (Correo)

![ENV correo](https://github.com/JarlinFonseca/Reto-Backend-Parqueadero-Nelumbo/assets/48332117/ba0464bc-c982-43cf-8e6c-5391f9f8c416)


```sh
-> src/main/resources/application-dev.yml
spring:
  datasource:
    url: ${URL_DB}
    username: ${USERNAME_DB}
    password: ${PASSWORD_DB}
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQL95Dialect
    hibernate:
      ddl-auto: ${DDL_AUTO}
    show-sql: true
```


##### 6. Ejecutar ambos microservicios de Parqueadero y Correo
Como es la primera vez que ejecutas los microservicios y además en el ENV de DDL_AUTO se agrego con "create", entonces poblara toda la data en las tablas de las base de datos de forma correcta en ambos microservicios, luego de revisar que todo este creado correctamente cambias el DDL_AUTO a "update", como lo explique en los pasos anteriores .


##### 7. Probar las diferentes funcionalidades importando la colección de Postman
Luego que ambos microservicios se esten ejecutando, importas la colección de Postman que estará en este repositorio para que puedas probar las funcionalidades de ambos microservicios.

NOTA: es importante que ambos corran para que no tenga errores, ya que el parqueadero usa una funcionalidad del correo.

##### 8. Siempre debe loguearse para poder acceder a los demás endpoints
Al momento de probar las funcionalidades debe hacer login como ADMIN o SOCIO, depende de lo que vaya a probar para poder generar el token JWT y ese token lo pega en el apartado de Postman en Authorization->Bearer Token->Token o en Headers agregas en Key: Authorization y en Value: Bearer tokenJWT.

##### Ejemplo:
Nos logueamos como ADMIN:

![Login ADMIN](https://github.com/JarlinFonseca/Reto-Backend-Parqueadero-Nelumbo/assets/48332117/03f9fdb7-1a4d-4189-9a7e-8f17c05b458f)


Copiamos el token JWT, y nos vamos al endpoint que vamos a probar, por ejemplo la primera opción para agregar el token en Postman:
![explicacion tokenJWT](https://github.com/JarlinFonseca/Reto-Backend-Parqueadero-Nelumbo/assets/48332117/a0ce3bb8-bcbf-40df-98c2-34306b8dcd3a)

Y de esta forma sería la segunda opción de agregar el token JWT:
![explicacion tokenJWT2](https://github.com/JarlinFonseca/Reto-Backend-Parqueadero-Nelumbo/assets/48332117/2352ff6c-ace1-4d4f-a7e0-fd76c0804e72)

NOTA: Cualquiera de estas dos opciónes puedes usar, ya que es necesario Autenticarse(Hacer el Login) el cual genera un token JWT y luego Autorizar los endpoints de acuerdo a los Roles(ADMIN o SOCIO) con sus permisos. 

___

### Autor:

1. Jarlin Andres Fonseca Bermón - jarlinandres5000@gmail.com
