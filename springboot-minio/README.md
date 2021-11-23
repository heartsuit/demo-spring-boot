## 测试：

1. 桶管理；
2. 对象管理；
3. 对象预签名；
4. 桶策略管理；

## SpringBoot集成MinIO

基于MinIOn客户端，主要实现了桶管理，对象管理，对象预签名等服务接口。

以RESTful API对外提供文件上传、下载、删除操作接口；

使用PostMan测试文件上传、下载、删除接口：头信息：Content-Type:multipart/form-data

```bash
# 上传
curl --location --request POST 'localhost:8090/minio/uploadFile' \
--header 'Content-Type: multipart/form-data' \
--form 'file=@"/C:/Users/nxq01/Downloads/springboot-minio-master.zip"'

# 下载
curl --location --request POST 'localhost:8090/minio/downloadFile' \
--form 'bucketName="heartsuit"' \
--form 'originalName="springboot-minio-master.zip"' \
--form 'filePath="2021-11-23/92cf3f69-501b-41de-83ae-f67e5a57f35f.zip"'

# 删除
curl --location --request POST 'localhost:8090/minio/deleteFile' \
--header 'Content-Type: multipart/form-data' \
--form 'bucketName="heartsuit"' \
--form 'filePath="2021-11-23/92cf3f69-501b-41de-83ae-f67e5a57f35f.zip"'
```

## MinIO在Docker下单实例运行

```
docker run -p 9000:9000 \
  --name minio1 \
  -v /opt/minio/data-single \
  -e "MINIO_ACCESS_KEY=AKIAIOSFODNN7EXAMPLE" \
  -e "MINIO_SECRET_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY" \
  minio/minio server /data
```
  
## MinIO在Docker Compose下集群（4实例）运行
 
- 参考官方文档：https://docs.min.io/docs/deploy-minio-on-docker-compose.html

- 配置

通告docker-compose在一台主机上，运行四个MinIOn实例，并由Nginx进行反向代理，负载均衡对外统一提供服务

涉及的两个配置：docker-compose.yaml，nginx.conf。

docker-compose.yaml

```yaml
version: '3.7'

# starts 4 docker containers running minio server instances.
# using nginx reverse proxy, load balancing, you can access
# it through port 9000.
services:
  minio1:
    image: minio/minio:RELEASE.2020-11-10T21-02-24Z
    volumes:
      - data1-1:/data1
      - data1-2:/data2
    expose:
      - "9000"
    environment:
      MINIO_ACCESS_KEY: minio
      MINIO_SECRET_KEY: minio123
    command: server http://minio{1...4}/data{1...2}
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9000/minio/health/live"]
      interval: 30s
      timeout: 20s
      retries: 3

  minio2:
    image: minio/minio:RELEASE.2020-11-10T21-02-24Z
    volumes:
      - data2-1:/data1
      - data2-2:/data2
    expose:
      - "9000"
    environment:
      MINIO_ACCESS_KEY: minio
      MINIO_SECRET_KEY: minio123
    command: server http://minio{1...4}/data{1...2}
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9000/minio/health/live"]
      interval: 30s
      timeout: 20s
      retries: 3

  minio3:
    image: minio/minio:RELEASE.2020-11-10T21-02-24Z
    volumes:
      - data3-1:/data1
      - data3-2:/data2
    expose:
      - "9000"
    environment:
      MINIO_ACCESS_KEY: minio
      MINIO_SECRET_KEY: minio123
    command: server http://minio{1...4}/data{1...2}
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9000/minio/health/live"]
      interval: 30s
      timeout: 20s
      retries: 3

  minio4:
    image: minio/minio:RELEASE.2020-11-10T21-02-24Z
    volumes:
      - data4-1:/data1
      - data4-2:/data2
    expose:
      - "9000"
    environment:
      MINIO_ACCESS_KEY: minio
      MINIO_SECRET_KEY: minio123
    command: server http://minio{1...4}/data{1...2}
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9000/minio/health/live"]
      interval: 30s
      timeout: 20s
      retries: 3

  nginx:
    image: nginx:1.19.2-alpine
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
    ports:
      - "9000:9000"
    depends_on:
      - minio1
      - minio2
      - minio3
      - minio4

## By default this config uses default local driver,
## For custom volumes replace with volume driver configuration.
volumes:
  data1-1:
  data1-2:
  data2-1:
  data2-2:
  data3-1:
  data3-2:
  data4-1:
  data4-2:
```

nginx.conf

```conf
user  nginx;
worker_processes  auto;

error_log  /var/log/nginx/error.log warn;
pid        /var/run/nginx.pid;


events {
    worker_connections  1024;
}


http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    keepalive_timeout  65;

    #gzip  on;

    # include /etc/nginx/conf.d/*.conf;

    upstream minio {
        server minio1:9000;
        server minio2:9000;
        server minio3:9000;
        server minio4:9000;
    }

    server {
        listen       9000;
        listen  [::]:9000;
        server_name  localhost;

         # To allow special characters in headers
         ignore_invalid_headers off;
         # Allow any size file to be uploaded.
         # Set to a value such as 1000m; to restrict file size to a specific value
         client_max_body_size 0;
         # To disable buffering
         proxy_buffering off;

        location / {
            proxy_set_header Host $http_host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;

            proxy_connect_timeout 300;
            # Default is HTTP/1, keepalive is only enabled in HTTP/1.1
            proxy_http_version 1.1;
            proxy_set_header Connection "";
            chunked_transfer_encoding off;

            proxy_pass http://minio;
        }
    }
}
```

- 启动

```bash
前台运行（可直观地查看日志）：docker-compose up
后台运行：docker-compose up -d
停止服务：docker-compose down
```

- 集群运行后，可通过停止不同数量的实例观察可用性：可下载、可上传。

> A stand-alone MinIO server would go down if the server hosting the disks goes offline. In contrast, a distributed MinIO setup with m servers and n disks will have your data safe as long as m/2 servers or m*n/2 or more disks are online.
  
> For example, an 16-server distributed setup with 200 disks per node would continue serving files, up to 4 servers can be offline in default configuration i.e around 800 disks down MinIO would continue to read and write objects.
```
MinIO官方建议起码要搭建一个四快盘的集群，具体配置几台机器看自己需求确定，比如：
一台机器四块硬盘
二台机器两块硬盘
四台机器一块硬盘

有三块在线，读取写入都可以进行，有两块在线，可以保证能读取，但不能写入，若只剩下一块，则读写都不可进行
```

- 查看服务日志

通过`docker exec -it cd34c345960c /bin/bash`无法进入容器，报错：OCI runtime exec failed: exec failed: container_linux.go:349: starting container process caused "exec: \"/\": permission denied": unknown

解决：docker exec -it cd34c345960c /bin/bash 改为：docker exec -it cd34c345960c sh 或者：docker exec -it cd34c345960c /bin/sh

docker exec -it 12935e3c6264 bash