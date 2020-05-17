# pgrServer

![Alt text](Route.png?raw=true)

Introduction
------------
pgrServer is a routing service that is able to use pgRouting topologies 
to load data into a JGraphT graph for very fast searches even with dense networks.

The graph is created at startup when the topology is read from a PostgreSQL database. This graph though can be re-created at regular intervals by making a service request, for networks that have dynamic costs. 
 
And similar to pgRouting, this application is not road navigation centric. This application can be used for a wide variety of networks: i.e. utilities (fiber optic lines), water systems, etc.
 
As of this version, the following search algorithms are included as a service:

* Dijkstra ( for dense networks )
* A-Star ( for dense networks )
* Bellman-Ford ( for sparse networks )
* BFS ( for sparse networks )
* Johnson ( for sparse networks )
* Floyd-Warshall ( for sparse networks )

Requirements
------------
* PostgreSQL > 9.4
* PostGIS
* PgRouting (to create a topology)
* Maven
* Tomcat Application Server for deployment


Preparing the Topology
----------------------

* Create a topology table. Refer to pgRouting's Documentations on the __pgr_createTopology__ function.


* Ensure that there is an index on an unique id field and a spatial inddex on the geometry field of the topology table.


* Create a View Table __pgrserver__ that will contain the following fields:
id, source, target, cost, geom.
  
```sql
CREATE VIEW pgrserver AS SELECT id,node_from AS source,node_to AS target,cost,wkb_geometry AS geom FROM kanto ; 
```
 
Building the Application
------------------------

* Edit __src/main/resources/application.properties__ and modify the PostgreSQL Database  URL and login parameters.


* Create a WAR file that can be deployed to a Tomcat Server.

```
    mvn clean install -DskipTests
```

* Or run and test the application with the built-in Tomcat container.

```
    export SERVER_SERVLET_CONTEXT_PATH=/pgrServer
    mvn spring-boot:run
```

Display the List of APIs
-----------------------

The list of APIs can be viewed by displaying the Swagger page:

```html
http://localhost:8080/pgrServer/swagger-ui.html
```

Reload the Graph
---------------

To reload the graph if the __cost__ has changed, send a POST request with the authcode parameter value. The authcode value can be set by updating the installed pgrs_auth table in the PostgreSQL database. 

```shell
curl -X POST -F "authcode=abc12345" "http://localhost:8080/pgrServer/api/graphreload"
```
