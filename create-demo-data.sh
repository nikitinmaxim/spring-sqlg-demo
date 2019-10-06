#!/bin/bash

curl -X POST --header "Content-Type:application/json" -d "Station 1" http://localhost:8778/api/stations
curl -X POST --header "Content-Type:application/json" -d "Station 2" http://localhost:8778/api/stations
curl -X POST --header "Content-Type:application/json" -d "Station 3" http://localhost:8778/api/stations
curl -X POST --header "Content-Type:application/json" -d "Station 4" http://localhost:8778/api/stations
curl -X POST --header "Content-Type:application/json" -d "Station 5" http://localhost:8778/api/stations

curl -X POST --header "Content-Type:application/json" -d "{\"cost\":38.5,\"station1\":\"Station 3\",\"station2\":\"Station 1\"}" http://localhost:8778/api/routes
curl -X POST --header "Content-Type:application/json" -d "{\"cost\":41.5,\"station1\":\"Station 3\",\"station2\":\"Station 2\"}" http://localhost:8778/api/routes
curl -X POST --header "Content-Type:application/json" -d "{\"cost\":10,\"station1\":\"Station 1\",\"station2\":\"Station 5\"}" http://localhost:8778/api/routes
curl -X POST --header "Content-Type:application/json" -d "{\"cost\":5,\"station1\":\"Station 5\",\"station2\":\"Station 4\"}" http://localhost:8778/api/routes
curl -X POST --header "Content-Type:application/json" -d "{\"cost\":10,\"station1\":\"Station 4\",\"station2\":\"Station 2\"}" http://localhost:8778/api/routes
