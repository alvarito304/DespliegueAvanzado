# Tutorial de Despliegue Avanzado

## Proyecto

En este caso tenemos una aplicacion basica donde hemos implementado un test, algo de java doc y el plugin de jacoco para que a la hora de que la aplicacion se construya genere ciertas paginas web las cuales serviremos con docker y un proxy inverso

## Docker file

```dockerfile
FROM gradle:jdk21-alpine AS build

WORKDIR /app

COPY . /app
ARG DOCKER_HOST_ARG=tcp://host.docker.internal:2375
ENV DOCKER_HOST={$DOCKER_HOST_ARG}
RUN ./gradlew build
RUN ./gradlew javadoc


FROM eclipse-temurin:21-jre-alpine AS run
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/my-app.jarv
COPY --from=build /app/build/docs /app/doc
COPY --from=build /app/build/jacoco /app/jacoco
COPY --from=build /app/build/reports/tests /app/tests
ENTRYPOINT ["java"]
CMD ["-jar", "/app/my-app.jar"]
```

- Aqui tenemos el `docker file` para construir nuestra aplicacion, el docker file en si se plantea de forma multi etapa donde en la primera etapa construimos el proyecto y en la segunad generamos un contenedor con las páginas web que genera automaticamente.

## Docker Compose

```yml
services:
 cont:
   build:
     context: .
     dockerfile: Dockerfile
   container_name: java_continuo
   image: java_continuo
   volumes:
       - documentation_doc:/app/doc
       - documentation_jacoco:/app/jacoco
       - documentation_tests:/app/tests


 nginx_presentacion:
   image: ubuntu/nginx:latest
   container_name: nginx_presentacion
   volumes:
     - ./despliegueServers/presentacion/index.html:/var/www/html/presentacion/index.html
     - ./despliegueServers/presentacion/default:/etc/nginx/sites-available/default
   restart: always
   depends_on:
     - cont
   networks:
     - webs



 apache_doc:
     image: ubuntu/apache2:latest
     container_name: apache_doc
     volumes:
       - documentation_doc:/var/www/html/
       - ./despliegueServers/apacheDoc/000-default.conf:/etc/apache2/sites-available/000-default.conf
     restart: always
     depends_on:
       - cont
     networks:
       - webs

 nginx_coverage:
   image: ubuntu/nginx:latest
   container_name: nginx_coverage
   volumes:
     - documentation_jacoco:/var/www/html/
     - ./despliegueServers/nginxCoverage/default:/etc/nginx/sites-available/default
   restart: always
   depends_on:
     - cont
   networks:
     - webs

 nginx_test:
   image: ubuntu/nginx:latest
   container_name: nginx_test
   volumes:
     - documentation_tests:/var/www/html/
     - ./despliegueServers/nginxTest/default:/etc/nginx/sites-available/default
   restart: always
   depends_on:
     - cont
   networks:
     - webs

 reverse_proxy:
   image: ubuntu/nginx:latest
   container_name: reverse_proxy
   volumes:
     - ./despliegueServers/proxy/nginx.conf:/etc/nginx/nginx.conf
     - ./despliegueServers/proxy/sslcerts:/etc/nginx/certs
   ports:
     - "80:80"
     - "443:443"
   restart: always
   depends_on:
     - nginx_presentacion
     - apache_doc
     - nginx_coverage
     - nginx_test
   networks:
     - webs


volumes:
  documentation_doc:
  documentation_jacoco:
  documentation_tests:
networks:
      webs:

```

- en el docker compose hacemos que todos los servidores dependan de el build de la imagen para que cuando secree el build enlacemos su contenido con volumenes para poder copiar el contenido de esos volumenes a cada servidor ya que usaremos servidores apache y servidores nginx
- luego hacemos que el contenedor que levanta el proxy dependa de los servidores y listo ya se estaran sirviendo nuestros test, documentacion y coverage, en paginas web a traves de nuestro proxy inverso.
    - para aprender a hacer un proxy inverso [click aqui](https://github.com/alvarito304/ReverseProxy)

## Netlify

Para desplegar la documentacion a traves de netlify, creamos una carpeta llamada netlify con un index y un css donde daremos acceso a las documentaciones  dentro de la carpeta de netlify tambien pegaremos las carpetas de documentaciones

- **Este es el Resultado** [Mi despliegue avanzado](https://alvaroherrerodespliegueavanzado.netlify.app/) 