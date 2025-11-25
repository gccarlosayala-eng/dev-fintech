# Apache Fineract
<!-- TODO Reactivar cuando haya una instancia de CI-CD funcionando: [![Swagger Validation](https://validator.swagger.io/validator?url=https://sandbox.mifos.community/fineract-provider/swagger-ui/fineract.yaml)](https://validator.swagger.io/validator/debug?url=https://sandbox.mifos.community/fineract-provider/swagger-ui/fineract.yaml) -->
[![Build](https://github.com/apache/fineract/actions/workflows/build-mariadb.yml/badge.svg?branch=develop)](https://github.com/apache/fineract/actions/workflows/build-mariadb.yml)
[![Docker Hub](https://img.shields.io/docker/pulls/apache/fineract.svg?logo=Docker)](https://hub.docker.com/r/apache/fineract)
[![Docker Build](https://github.com/apache/fineract/actions/workflows/publish-dockerhub.yml/badge.svg)](https://github.com/apache/fineract/actions/workflows/publish-dockerhub.yml)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=apache_fineract&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=apache_fineract)

Apache Fineract es una plataforma de banca central de código abierto que proporciona una
base flexible y extensible para una amplia gama de servicios financieros. Al hacer que la
tecnología bancaria robusta esté disponible abiertamente, reduce las barreras para que las
instituciones e innovadores lleguen a poblaciones desatendidas y sin servicios bancarios.

Consulta la [documentación](https://fineract.apache.org/docs/current), el [wiki](https://cwiki.apache.org/confluence/display/FINERACT) o las [FAQ](https://cwiki.apache.org/confluence/pages/viewpage.action?pageId=91554327), si este README no responde lo que estás buscando.


COMUNIDAD
=========

Si estás interesado en contribuir a este proyecto, pero quizás no sabes muy bien cómo y dónde empezar, por favor [únete a nuestra lista de correo de desarrolladores](http://fineract.apache.org/#contribute), escucha nuestras conversaciones, participa en los hilos, o simplemente envíanos un correo de presentación diciendo "¡Hola!"; somos un grupo amigable y esperamos saber de ti. Una alternativa más informal es el [canal de Slack de Fineract](https://app.slack.com/client/T0F5GHE8Y/C028634A61L) (¡gracias, Mifos, por apoyar el canal de Slack!).

Para el wiki de desarrolladores, consulta la [Zona de Contribuidores](https://cwiki.apache.org/confluence/display/FINERACT/Contributor%27s+Zone). Quizás [estos artículos de cómo hacer](https://cwiki.apache.org/confluence/display/FINERACT/How-to+articles) te ayuden a comenzar.

En cualquier caso, visita [nuestro Panel de JIRA](https://issues.apache.org/jira/secure/Dashboard.jspa?selectPageId=12335824) para encontrar tareas en las que trabajar, ver qué están haciendo otros, o abrir nuevos issues.

En el momento que comiences a escribir código, por favor consulta nuestras guías de [CONTRIBUCIÓN](CONTRIBUTING.md), donde encontrarás más información sobre temas como estilo de código, pruebas y pull requests.


REQUISITOS
============
* Mínimo 16GB RAM y CPU de 8 núcleos
* `MariaDB >= 11.5.2` o `PostgreSQL >= 17.0`
* `Java >= 21` (Azul Zulu JVM está probado por nuestro CI en GitHub Actions)

Tomcat (mín. v10) solo es necesario si deseas desplegar el WAR de Fineract en un contenedor de servlets externo separado. No necesitas instalar Tomcat para ejecutar Fineract. Recomendamos el uso del JAR auto-contenido, que incorpora de forma transparente un contenedor de servlets usando Spring Boot.


SEGURIDAD
============
Si crees que has encontrado una vulnerabilidad de seguridad, [háznoslo saber de forma privada](https://fineract.apache.org/#contribute).

Para detalles sobre seguridad durante el desarrollo y despliegue, consulta la documentación [aquí](https://fineract.apache.org/docs/current/#_security).


INSTRUCCIONES
============

Los siguientes pasos asumen que tienes Java instalado, has clonado el repositorio (o descargado y extraído una [versión específica](https://github.com/apache/fineract/releases)) y tienes un [servidor de base de datos](#base-de-datos-y-tablas) (MariaDB o PostgreSQL) en ejecución.

Cómo ejecutar para desarrollo local
---

Ejecuta los siguientes comandos en este orden:
```bash
./gradlew createDB -PdbName=fineract_tenants
./gradlew createDB -PdbName=fineract_default
./gradlew devRun
```

Esto crea dos bases de datos, construye y ejecuta Fineract, que ahora estará escuchando peticiones de API en el puerto 8443 (por defecto).

Confirma que Fineract está listo con, por ejemplo:

```bash
curl --insecure https://localhost:8443/fineract-provider/actuator/health
```

Para probar endpoints autenticados, incluye credenciales en tu petición:

```bash
curl --location \
  https://localhost:8443/fineract-provider/api/v1/clients \
  --header 'Content-Type: application/json' \
  --header 'Fineract-Platform-TenantId: default' \
  --header 'Authorization: Basic bWlmb3M6cGFzc3dvcmQ='
```

Cómo ejecutar para producción
---
Ejecutar Fineract para probarlo es relativamente fácil. Si tienes la intención de usarlo en un entorno de producción, ten en cuenta que un despliegue adecuado puede ser complejo, costoso y consumir mucho tiempo. Las consideraciones incluyen: Seguridad, privacidad, cumplimiento normativo, rendimiento, disponibilidad del servicio, respaldos, y más. El proyecto Fineract no proporciona una guía completa para desplegar Fineract en producción. Podrías necesitar habilidades en aplicaciones Java empresariales y más. Alternativamente, podrías pagar a un proveedor por el despliegue y mantenimiento de Fineract. Encontrarás consejos y trucos para desplegar y asegurar Fineract en nuestra documentación oficial y en el wiki mantenido por la comunidad.


Cómo construir el archivo JAR
---
Construye un archivo JAR moderno, nativo en la nube, completamente auto-contenido:
```bash
./gradlew clean bootJar
```
El JAR se creará en el directorio `fineract-provider/build/libs`.
Como no se nos permite incluir un driver JDBC en el JAR construido, descarga un driver JDBC de tu elección. Por ejemplo:
```bash
wget https://dlm.mariadb.com/4174416/Connectors/java/connector-java-3.5.2/mariadb-java-client-3.5.2.jar
```
Inicia el JAR y especifica el directorio que contiene el driver JDBC usando la opción loader.path, por ejemplo:
```bash
java -Dloader.path=. -jar fineract-provider/build/libs/fineract-provider.jar
```
Esto no requiere un Tomcat externo.

Los detalles de conexión de la base de datos de tenants se configuran [mediante variables de entorno (como con el contenedor Docker)](#instrucciones-para-ejecutar-usando-docker-o-podman), por ejemplo así:
```bash
export FINERACT_HIKARI_PASSWORD=verysecret
...
java -jar fineract-provider.jar
```

Cómo construir el archivo WAR
---
Construye un archivo WAR tradicional:
```bash
./gradlew :fineract-war:clean :fineract-war:war
```
El WAR se creará en el directorio `fineract-war/build/libs`. Posteriormente despliega el WAR en tu Contenedor de Servlets Tomcat.

Recomendamos usar el JAR en lugar del despliegue con archivo WAR, porque es mucho más fácil.


Cómo ejecutar usando Docker o Podman
---

Es posible hacer una instalación 'de un solo toque' de Fineract usando contenedores (también conocido como "Docker").
Esto incluye la base de datos ejecutándose en el contenedor.

Como prerequisitos, debes tener `docker` y `docker-compose` instalados en tu máquina; consulta
[Instalación de Docker](https://docs.docker.com/install/) e [Instalación de Docker Compose](https://docs.docker.com/compose/install/).

Alternativamente, también puedes usar [Podman](https://github.com/containers/libpod)
(por ejemplo, mediante `dnf install podman-docker`), y [Podman Compose](https://github.com/containers/podman-compose/)
(por ejemplo, mediante `pip3 install podman-compose`) en lugar de Docker.

Para ejecutar una nueva instancia de Fineract en Linux simplemente puedes:
```bash
git clone https://github.com/apache/fineract.git
cd fineract
./gradlew :fineract-provider:jibDockerBuild -x test
```
En Windows, haz esto en su lugar:
```cmd
git clone https://github.com/apache/fineract.git --config core.autocrlf=input
cd fineract
gradlew :fineract-provider:jibDockerBuild -x test
```
Instala el driver de logs de Loki e inicia:
```bash
docker plugin install grafana/loki-docker-driver:latest \
  --alias loki --grant-all-permissions
docker compose -f docker-compose-development.yml up -d
```
El back-end de Fineract debería estar ejecutándose en https://localhost:8443/fineract-provider/ ahora.
Espera a que https://localhost:8443/fineract-provider/actuator/health devuelva `{"status":"UP"}`.
Debes ir a https://localhost:8443 y recordar aceptar el certificado SSL autofirmado de la API una vez en tu navegador.

[Docker Hub](https://hub.docker.com/r/apache/fineract) tiene una imagen de contenedor pre-construida de este proyecto, construida continuamente.

Debes especificar la URL JDBC de la base de datos de tenants de MySQL pasándola al contenedor `fineract` mediante variables de
entorno; por favor consulta el [`docker-compose.yml`](docker-compose.yml) para detalles exactos de cómo especificarlas.

Los archivos de logs y la salida de Java Flight Recorder están disponibles en `PROJECT_ROOT/build/fineract/logs`. Si usas IntelliJ entonces puedes hacer doble clic en el archivo `.jfr` y abrirlo con el IDE. También puedes descargar [Azul Mission Control](https://www.azul.com/products/components/azul-mission-control/) para analizar el archivo de Java Flight Recorder.

NOTA: Si tienes problemas con los permisos de archivos y Docker Compose entonces podrías necesitar cambiar los valores de las variables `FINERACT_USER` y `FINERACT_GROUP` en `PROJECT_ROOT/config/docker/env/fineract-common.env`. Puedes averiguar qué valores necesitas poner allí con los siguientes comandos:

```bash
id -u ${USER}
id -g ${GROUP}
```

Por favor asegúrate de no confirmar tus valores cambiados. Los valores predeterminados deberían funcionar para la mayoría de las personas.


Cómo ejecutar en Kubernetes
---

### Clusters Generales

También puedes ejecutar Fineract usando contenedores en un cluster de Kubernetes.
Asegúrate de configurar y conectarte a tu cluster de Kubernetes.
Puedes seguir [esta](https://cwiki.apache.org/confluence/display/FINERACT/Install+and+configure+kubectl+and+Google+Cloud+SDK+on+ubuntu+16.04) guía para configurar un cluster de Kubernetes en GKE. Asegúrate de reemplazar `apache-fineract-cn` con `apache-fineract`

Ahora, por ejemplo, desde tu shell de Google Cloud, ejecuta los siguientes comandos:

```bash
git clone https://github.com/apache/fineract.git
cd fineract/kubernetes
./kubectl-startup.sh
```

Para apagar y reiniciar tu Cluster, ejecuta:
```bash
./kubectl-shutdown.sh
```

### Usando Minikube

Alternativamente, puedes ejecutar fineract en un cluster de kubernetes local usando [minikube](https://minikube.sigs.k8s.io/docs/).
Como prerequisito debes tener `minikube` y `kubectl` instalados en tu máquina; consulta
[Instalación de Minikube y Kubectl](https://kubernetes.io/docs/tasks/tools/install-minikube/).

Para ejecutar una nueva instancia de Fineract en Minikube simplemente puedes:

```bash
git clone https://github.com/apache/fineract.git
cd fineract/kubernetes
minikube start
./kubectl-startup.sh
minikube service fineract-server --url --https
```

Fineract ahora está ejecutándose en la URL impresa, que puedes verificar por ejemplo usando:
```bash
http --verify=no --timeout 240 --check-status get $(minikube service fineract-server --url --https)/fineract-provider/actuator/health
```
Para verificar el estado de tus contenedores en tu cluster local de kubernetes de minikube, ejecuta:
```bash
minikube dashboard
```
Puedes verificar los logs de Fineract usando:
```bash
kubectl logs deployment/fineract-server
```
Para apagar y reiniciar tu cluster, ejecuta:
```bash
./kubectl-shutdown.sh
```


Cómo habilitar Message Broker Externo (ActiveMQ o Apache Kafka)
---

Hay dos casos de uso donde se necesita un message broker externo:
 - Eventos de Negocio Externos / Marco de Eventos Confiables
 - Ejecución de Trabajos por Lotes de Spring particionados

Los Eventos Externos son eventos de negocio, por ejemplo: `ClientCreated`, que pueden ser importantes para sistemas de terceros. Apache Fineract admite ActiveMQ (u otros brokers compatibles con JMS) y endpoints de Apache Kafka para enviar Eventos de Negocio. Por defecto, no se emiten.

En caso de un despliegue grande con millones de cuentas, el trabajo por lotes de Cierre del Día de Negocio de Spring puede ejecutarse varias horas. Para acelerar esta tarea, se admite el particionado remoto del trabajo. El nodo Manager divide el trabajo COB en piezas más pequeñas (sub tareas), que luego pueden ejecutarse en múltiples nodos Worker en paralelo. Los nodos worker son notificados por ActiveMQ o Kafka respecto a sus nuevas sub tareas.

### ActiveMQ

La mensajería basada en JMS está deshabilitada por defecto. En `docker-compose-postgresql-activemq.yml` se muestra un ejemplo donde ActiveMQ está habilitado. En esa configuración se crean una instancia de Spring Batch Manager y dos instancias de Spring Batch Worker.
Los eventos basados en Spring deben deshabilitarse y el manejo de eventos basado en jms debe habilitarse. Además, debe configurarse la URL JMS del broker apropiada.

```
      FINERACT_REMOTE_JOB_MESSAGE_HANDLER_JMS_ENABLED=true
      FINERACT_REMOTE_JOB_MESSAGE_HANDLER_SPRING_EVENTS_ENABLED=false
      FINERACT_REMOTE_JOB_MESSAGE_HANDLER_JMS_BROKER_URL=tcp://activemq:61616
```

Para configuración adicional relacionada con ActiveMQ por favor consulta el `application.properties` donde los parámetros de configuración admitidos están listados con sus valores predeterminados.

### Kafka

El soporte de Kafka también está deshabilitado por defecto. En `docker-compose-postgresql-kafka.yml` se muestra un ejemplo donde Kafka auto-hospedado está habilitado tanto para Eventos Externos como para la ejecución de Trabajos Remotos de Spring Batch.

Durante el desarrollo, Fineract fue probado con brokers de Kafka PLAINTEXT sin autenticación y con AWS MSK usando autenticación IAM. El [archivo JAR](https://github.com/aws/aws-msk-iam-auth/releases) adicional requerido para autenticación IAM ya está agregado al classpath.
Un ejemplo de configuración de MSK se puede encontrar en `docker-compose-postgresql-kafka-msk.yml`.

La lista completa de propiedades relacionadas con Kafka admitidas está documentada en la [documentación de la Plataforma Fineract](https://fineract.apache.org/docs/current/).


BASE DE DATOS Y TABLAS
===================

Puedes ejecutar la versión requerida del servidor de base de datos en un contenedor, en lugar de tener que instalarlo, así:

    docker run --name mariadb-11.5 -p 3306:3306 -e MARIADB_ROOT_PASSWORD=mysql -d mariadb:11.5.2

y detenerlo y destruirlo así:

    docker rm -f mariadb-11.5

Ten en cuenta que esta base de datos de contenedor mantiene su estado dentro del contenedor y no en el sistema de archivos del host. Se pierde cuando destruyes (rm) este contenedor. Esto típicamente está bien para desarrollo. Consulta [Advertencias: Dónde Almacenar Datos en la documentación del contenedor de base de datos](https://hub.docker.com/_/mariadb) sobre cómo hacerlo persistente en lugar de efímero.


MySQL/MariaDB y zona horaria UTC
---
Con la versión `1.8.0` introdujimos un manejo mejorado de fecha y hora en Fineract. La fecha y hora se almacena en UTC, y la zona horaria UTC se aplica incluso en el driver JDBC, por ejemplo para MySQL:

```
serverTimezone=UTC&useLegacyDatetimeCode=false&sessionVariables=time_zone='-00:00'
```

Si usas MySQL como base de datos de Fineract, se recomienda altamente la siguiente configuración:

* Ejecutar la aplicación en UTC (la línea de comandos predeterminada en nuestra imagen Docker ya tiene los parámetros necesarios configurados)
* Ejecutar el servidor de base de datos MySQL en UTC (si usas servicios administrados como AWS RDS, entonces esto debería ser el predeterminado de todos modos, pero sería bueno verificarlo)

En caso de que Fineract y MySQL no se ejecuten en UTC, MySQL podría guardar valores de fecha y hora de forma diferente a PostgreSQL

Escenario de ejemplo: Si la instancia de Fineract se ejecuta en zona horaria: GMT+2, y la fecha y hora local es 2022-08-11 17:15 ...
* ... entonces PostgreSQL guarda el LocalDateTime tal cual: 2022-08-11 17:15
* ... y MySQL guarda el LocalDateTime en UTC: 2022-08-11 15:15
* ... pero cuando leemos la fecha y hora desde PostgreSQL o desde MySQL, ambos sistemas nos dan el mismo valor: 2022-08-11 17:15 GMT+2

Si una instancia de Fineract usada previamente no se ejecutó en UTC (compatibilidad hacia atrás), todas las fechas anteriores serán leídas incorrectamente por MySQL. Esto puede causar problemas cuando ejecutas los scripts de migración de base de datos.

Recomendación: Desplaza todas las fechas en tu base de datos por el offset de zona horaria que usó tu instancia de Fineract.


CONFIGURACIÓN DEL POOL DE CONEXIONES
=======

Por favor verifica `application.properties` para ver qué configuraciones del pool de conexiones pueden ajustarse. Las variables de entorno asociadas tienen el prefijo `FINERACT_HIKARI_*`. Puedes encontrar más información sobre configuraciones específicas del pool de conexiones en el [repositorio de Github de HikariCP](https://github.com/brettwooldridge/HikariCP?tab=readme-ov-file#gear-configuration-knobs-baby).

NOTA: Mantenemos compatibilidad hacia atrás hasta uno de los próximos lanzamientos para asegurar que las cosas funcionen como se espera. Las variables de entorno con prefijo `fineract_tenants_*` aún pueden usarse para configurar la conexión de base de datos, pero recomendamos encarecidamente usar `FINERACT_HIKARI_*` con más opciones.


VERSIONES
============

Una versión de lanzamiento se deriva del control de versiones. La versión incluirá `-SNAPSHOT` a menos que la rama actual parezca una rama de lanzamiento o de mantenimiento de lanzamiento. Consulta la configuración de `gitVersioning` en `build.gradle` para detalles.

El último lanzamiento estable puede verse en la rama develop: [Último Lanzamiento en Develop](https://github.com/apache/fineract/tree/develop "Último Lanzamiento").

El progreso de este proyecto puede verse en la navegación izquierda bajo [esta página del wiki](https://cwiki.apache.org/confluence/display/FINERACT/Fineract+Releases)


LICENCIA
============

Este proyecto está licenciado bajo [Apache License Version 2.0](https://github.com/apache/fineract/blob/develop/APACHE_LICENSETEXT.md).

La biblioteca cliente del Driver JDBC Connector/J de [MariaDB](https://www.mariadb.org) está licenciada bajo LGPL.
La biblioteca se usa a menudo en desarrollo cuando se ejecutan pruebas de integración que usan la biblioteca Liquibase. Sin embargo, ese driver JDBC
no se distribuye con el producto Fineract y no es necesario para usar el producto.
Si eres un desarrollador y te opones a usar el driver JDBC Connector/J licenciado bajo LGPL,
simplemente no ejecutes las pruebas de integración que usan la biblioteca Liquibase y usa otro driver JDBC.
Como se discutió en [LEGAL-462](https://issues.apache.org/jira/browse/LEGAL-462), este proyecto por lo tanto
cumple con la [política de licencias de terceros de Apache Software Foundation](https://www.apache.org/legal/resolved.html).


API DE LA PLATAFORMA
============

Fineract no proporciona una interfaz de usuario, pero proporciona una API. Ejecutando Fineract localmente, la documentación de Swagger puede accederse en `https://localhost:8443/fineract-provider/swagger-ui/index.html`. Una versión en vivo puede accederse mediante [este Sandbox](https://sandbox.mifos.community/fineract-provider/swagger-ui/index.html) (no hospedado por nosotros).

Apache Fineract admite generación de código cliente usando [Swagger Codegen](https://github.com/swagger-api/swagger-codegen) basado en la [Especificación OpenAPI](https://swagger.io/specification/). Para más instrucciones sobre cómo generar código cliente, verifica [esta sección](https://fineract.apache.org/docs/current/#_generate_api_client) de la documentación de Fineract. [Este video](https://www.youtube.com/watch?v=FlVd-0YAo6c) documenta el uso del Swagger-UI.
