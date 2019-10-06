# Spring Boot + Sqlg Demo

A simple microservice application, demonstrating graph manipulations with Apache TinkerGraph, using MariaDB as a storage for graph data.

## project structure

***demo-control.sh*** - helper script which can be used to build the project and deploy into docker swarm. Run this script without arguments to get more help. If maven is not available in the PATH, script should be adjusted with correct path to "mvn" command.

***docker-compose.yml*** - docker stack descriptor, contains both MariaDB and application services

***sources/storage-service*** - a service based on SpringBoot and [sqlg](https://github.com/pietermartin/sqlg). Provides universal API (via gRPC) for basic graph manipulations (creation of vertices, edges, performing BFS).

***sources/train-stations-service*** - a SpringBoot-based service which allows to create graph of train stations and routes between them. Provides RESTful API (and Swagger UI for simple testing).

## project build & run

- demo-control.sh make
- demo-control.sh make-docker
- demo-control.sh deploy

## project urls

- ***http://localhost:8777*** - url of adminer, simple database browser
- ***http://localhost:8778/v2/api-docs*** - swagger 2.0 specification of train-stations-service
- ***http://localhost:8778/swagger-ui.html*** - swagger ui of train-stations-service

Replace localhost with correct docker host if needed.

## demo data

To create demo data, run the following script: ***create-demo-data.sh***. Replace localhost with correct docker host if needed.
This will create 5 stations:
- Station 1
- Station 2
- Station 3
- Station 4
- Station 5

And 5 routes:
- From ***3*** to ***1***, with cost ***38.5***
- From ***3*** to ***2***, with cost ***41.5***
- From ***1*** to ***5***, with cost ***10***
- From ***5*** to ***4***, with cost ***5***
- From ***4*** to ***2***, with cost ***10***

Open swagger ui and execute operation "findRoutes" for stations "Station 1" and "Station 2". If everything goes well, you will get 2 routes, first route is a shortest one, but second route is cheapest:

- 1 -> 3 -> 2, total cost ***80***
- 1 -> 5 -> 4 -> 2, total cost ***25***

## what is missing

- Vertex name in graph must be unique, to be able to perform path search. But there is no such check on Vertex creation yet, so don't create stations with equal names or strange things will happen. It is still possible to delete all vertices via api, ie creation of duplicated vertices is almost safe.

- gRPC specification should be put into external library. At the moment, both services has own copy of ***StorageService.proto***

- error handling should be improved

- database connection in storage-service is lost from time to time. Should be something with connection pool configuration
