<p align="center"><a href="https://www.linkedin.com/company/nelumbo-consultores/" target="_blank"><img src="https://github.com/JarlinFonseca/Reto-Backend-Parqueadero-Nelumbo/assets/48332117/4a575e1d-daca-4ea9-96ed-7d77b825f5ac" width="400"></a></p>

# Reto Técnico Nelumbo Backend: Parqueadero

## Tabla de Contenido
1. [Descripción](#descripción)
2. [Tecnologías](#tecnologías)
3. [Modelo Relacional BD](#modelo-relacional-bd)
4. [Requisitos](#ide)
5. [Herramientas Recomendadas](#)
6. [Instalación y Uso](#instalación)
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
### Instalación y Uso:


#### Pasos de instalación y Uso

##### 1. Clonar el repositorio del proyecto en Laravel
Para clonar el proyecto abre una terminal o consola de comandos y escribe la siguiente nomenclatura, esto es después de la instrucción git clone agrega tu dirección:

```sh
git clone https://github.com/JarlinFonseca/SAGIS.git
```

##### 2. Instalar dependencias del proyecto
Cuando guardas tu proyecto Laravel en un repositorio GIT, en el archivo .gitignore se excluye la carpeta vendor que es donde están las librerías que usa tu proyecto, es por eso que se debe correr en la terminal una instrucción que tome del archivo composer.json todas las referencias de las librerías que deben estar instaladas en tu proyecto.

Ingresa desde la terminal a la carpeta del proyecto SAGIS y escribe:

```sh
composer install
```
Este comando instalará todas las librerías que están declaradas para tu proyecto.

##### 3. Generar archivo .env
Por seguridad el archivo .env está excluido del repositorio, para generar uno nuevo se toma como plantilla el archivo .env.example para copiar este archivo en una nuevo escribe en tu terminal:

```sh
cp .env.example .env
```

##### 4. Generar Key
Para que el proyecto en Laravel corra sin problemas es necesario generar una key de seguirdad, para ello en tu terminal corre el siguiente comando:

```sh
php artisan key:generate
```
Esta key nueva se agregará a tu archivo .env

##### 5. Crear base de datos (Omitir este paso, si ya has creado la BD)
Sí tu proyecto en Laravel funciona haciendo consultas a una base de datos entonces tienes que crear una nueva base de datos, la forma más rápida para crearla es desde la terminal:
```sh
mysql -u root -p

CREATE DATABASE nombreDeTuDBAqui CHARACTER SET utf8 COLLATE utf8_spanish_ci;
```
Para salir de la consola de MySQL solo escribe ‘exit’ y tecla Enter.

___

### Autor:

1. Jarlin Andres Fonseca Bermón - jarlinandres5000@gmail.com

