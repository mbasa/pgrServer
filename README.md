# pgrServer

   Dijkstra Shortest Path Search
![Alt text](pics/Route.png?raw=true)

   Driving Distance Isochrone
![Alt text](pics/DrivingDist.png?raw=true)

   Traveling Salesperson Problem
![Alt text](pics/TSP.png?raw=true)

   All Directed Path
![Alt text](pics/AllDirectedPath.png?raw=true)


   Vehicle Routing Problem (VRP)
![Alt text](pics/VRP.png?raw=true)

Introduction
------------
pgrServer is a routing service that is able to use pgRouting topologies
to load data into a JGraphT graph for very fast searches even with dense networks 
such as the OpenStreetMap (OSM) data set. 

The graph is created at startup when the topology is read from a PostgreSQL database. This graph though can be re-created at regular intervals by making a service request, for networks that have dynamic costs.

And similar to pgRouting, this application is not road navigation centric. This application can be used for a wide variety of networks: i.e. utilities (fiber optic lines), water systems, etc.

As of this version, the following search algorithms are included as a service:

* Dijkstra ( for dense networks )
* A-Star ( for dense networks )
* ContractionHierarchyBidirectionalDijkstra ( for dense networks * )
* ClosestFirstIterator ( for Driving Distance Isochrone creation )
* NearestNeighborHeuristicTSP ( for Traveling Salesperson Problem )
* All Directed Paths ( for sparse networks or short distance search)
* Bellman-Ford ( for sparse networks )
* BFS ( for sparse networks )
* Johnson ( for sparse networks )
* Floyd-Warshall ( for sparse networks )

(*Note: Initial call to a ContractionHierarchyBidirectionalDijkstra request will
take time since a contraction graph will be created first. Subsequent calls will
result in a much faster response.)

pgrServer is also able to solve **Vehicle Routing Problems (VRP)** using the JSprit VRP engine in order to find the optimal set of routes for a fleet of vehicles to traverse orders from a set of customers. 


When to use pgrServer
---------------------

* When a web service is required to serve route data. pgrServer can be used to easily serve data to a variety of web or mobile application clients.


* When the network is very dense and pgRouting struggles with long distance searches. pgrServer stores the entire graph into memory at start and can do route searches within the entire graph.


* When performance is paramount. pgrServer can return routes within ~50 kilometer searches in milliseconds even in very dense networks.   


* When the cost (weight) of the graph is dynamic. PgrServer can be used to load
and link updated costs residing in a table or view into an existing graph without 
having to reload the graph. Also, the data in the table or view does not have to include all the costs of the graph, only the changed costs, since pgrServer will use the existing cost if no link can be established.


 
Requirements
------------
* PostgreSQL > 9.4
* PostGIS
* PgRouting or Osm2Po (for topology creation)
* Maven
* Tomcat Application Server for deployment

Docker
-----------

For convenience, a Docker image can be built for this project. There are a few environment variables that can be optionally set in order for the Docker image to work properly:

- `POSTGRES_HOST`: the host IP/fully qualified domain. Default `localhost`.
- `POSTGRES_PORT`: the port designated for the Postgres instance. Default `5432`.
- `POSTGRES_DB`  : the database you. Default `pgr`.
- `POSTGRES_USER`: the Postgres user name. Default `postgres`.
- `POSTGRES_PASS`: the Postgres password. Default `postgres`.

**Note**, it is still necessary to [prepare the topology](#preparing-the-topology) with pgRouting or [osm2po](https://osm2po.de). Also, the docker usage is advisable for testing or development purposes only. **It is highly advisable not to run a production server with the Docker image.**

```
# build the image and spin up the container(s)
docker build -t mbasa/pgrserver:latest .
docker-compose up -d
```

From here, the application can now be tested by 
[displaying the List of APIs](#display-the-list-of-apis). 


Preparing the Topology
----------------------

* Create a topology table. Refer to pgRouting's Documentations on the __pgr_createTopology__ function. 


* It is also possible to use OSM2PO to create a topology table using OSM pbf data files. Refer to this [pgrServer WIKI](https://github.com/mbasa/pgrServer/wiki/Importing-Data-Using-Osm2Po) for more information on how to use OSM2PO to create topologies for pgrServer.  


* Ensure that there is an index on an __unique id__ field, an index on the __source__ field, an index on the __target__ field, and a spatial index on the __geometry__ field of the topology table.


* Create a View or Table __pgrserver__ based on the topology table that will contain the following fields:
`id`, `source`, `target`, `cost`, `reverse_cost`, `length`, `geom`.

```sql
CREATE VIEW pgrserver AS SELECT id,node_from AS source,node_to AS target,cost, reverse_cost, length, wkb_geometry AS geom FROM kanto ;
```

**Note:**

1. the `length` column has to be in **meters**(m) units.
  
2. the `reverse_cost` value has to be **greater than** the `cost` in order for the edge to be 
considered as a one-way street.



Getting the Application
-----------------------

The latest *WAR* file of the application can be downloaded from each stable release
of this repository, and can either be placed in a Tomcat Application Server or
run stand alone via the command:

```
java -jar pgrServer.war
```

![Alt text](pics/Startup.png?raw=true)



Building the Application
------------------------

* Edit __src/main/resources/application.properties__ and modify the PostgreSQL Database  URL and login parameters.


* Create a WAR file that can be deployed to a Tomcat Server.

```
    mvn clean install -DskipTests
```

* Or run and test the application with the built-in Tomcat container.

```
    mvn spring-boot:run
```

Display the List of APIs
-----------------------

The list of APIs can be viewed by displaying the Swagger page:

```html
http://localhost:8080/pgrServer/
```

![Alt text](pics/Swagger.png?raw=true)

Dynamic Costs
-------------

PgrServer can use dynamic costs from an existing table or view in PostgreSQL, and
link it into the graph without having to do a reload to create truly dynamic
routes. The table or view must contain the following fields: `id`, `source`, `target`,
`cost`.

```sql
CREATE VIEW dynamic_cost AS select id,source,target,length + 1000 as cost from pgrserver ;
```

This table or view name can be passed as a parameter for the following APIs:

* dijkstra_dyncost
* astar_dyncost

![Alt text](pics/Dynamic_Cost.png?raw=true)


Reload the Graph
---------------

To reload the graph if the complete __cost__ has changed, send a POST request with the authcode parameter value. The authcode value can be set by updating the installed pgrs_auth table in the PostgreSQL database.

```shell
curl -X POST -F "authcode=abc12345" "http://localhost:8080/pgrServer/api/graphreload"
```

Viewing the Data
----------------

A demo application, *[pgrServerDemo](http://github.com/mbasa/pgrServerDemo)* , has been created
to easily display selected features of pgrServer.


Also, pgrServer returns a GeoJSON object for the created route or driving distance polygon, hence any application that supports GeoJSON can be used to view the results.

To quickly view the results, GeoJSONLint web service can be used:

```html
http://www.geojsonlint.com
```

It is also possible to use the result as a Vector Layer in __QGIS__ by doing:

```
Layer -> Add Layer -> Add Vector Layer
```

and set the protocol to `HTTP` and add the URL request of pgrServer.

Tomcat Deployment
-----------------

Since this is a memory intensive application, configuring Tomcat to use more memory will be necessary, especially with very large data sets ( greater than or equal to 9 million edges ) to avoid __OutOfMemoryError__ problems. The argument settings below can be used to let Tomcat allocate up to 8GB of memory for its usage:

```
-Xms2048m -Xmx8192m -server
```

Refer to Tomcat documentation for more information on memory allocation. 


When using the spring-boot application's Tomcat, the following can be performed to pass the 
required memory parameters: 

```
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xms2048m -Xmx8192m"
```
