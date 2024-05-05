# docker build

```
docker build -t demo-orders .
```

# testing

curl -d 'value1' -X POST http://localhost:8001/kvstore/key1
curl http://localhost:8001/kvstore/key1

# access in minikube env

eval $(minikube -p minikube docker-env)

# build docker image with commit hash as version

docker build -t kvstore-k8s:$(git rev-parse HEAD) .

# TDD

1. ~~put/get/delete key value~~
2. ~~write put and delete into commit log~~
3. ~~a cluster that a service can join or leave~~
4. ~~replicate data change to other members in same cluster~~
5. ~~publish put/delete write to message broker~~
6. ~~consume put/delete write from message broker~~