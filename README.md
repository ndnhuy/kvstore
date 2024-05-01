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
docker build --build-arg GIT_COMMIT=$(git rev-parse HEAD) -t my_image:$(git rev-parse HEAD) .