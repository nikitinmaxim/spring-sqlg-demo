#!/bin/bash

operation=${1}

. ./env.sh

make-all() {
  cd ./sources/storage-service
  mvn clean verify

  cd ../train-stations-service
  mvn clean verify
}

make-all-docker() {
  cd ./sources/storage-service
  docker build -t demo-storage-service:1.0 .

  cd ../train-stations-service
  docker build -t demo-train-stations-service:1.0 .
}

deploy-stack() {
    printf "Deploying demo stack\n"
    docker stack deploy -c docker-compose.yml demo
}

undeploy-stack() {
    printf "Undeploying demo stack\n"
    docker stack rm demo
}

case $operation in
    make)
        make-all
    ;;

    make-docker)
        make-all-docker
    ;;

    deploy)
        deploy-stack
    ;;

    undeploy)
        undeploy-stack
    ;;

    *)
        printf "Usage: $(basename $BASH_SOURCE) <make/make-docker/deploy/undeploy>\n"
    ;;
esac
