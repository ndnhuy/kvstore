# docker build
```
docker build -t demo-orders .
```

# testing
curl -d 'value1' -X POST http://localhost:8001/kvstore/key1
curl http://localhost:8001/kvstore/key1