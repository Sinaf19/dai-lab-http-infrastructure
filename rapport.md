# Report for lab 4

Authors: Rachel Tranchida, Quentin Surdez


# Table of contents
1. [Static Web site](#static-web-site)
2. [Docker Compose](#docker-compose)
3. [HTTP API server](#http-api-server)
4. [Revers proxy with Traefik](#reverse-proxy-with-traefik)
5. [Scalability and load balancing](#scalability-and-load-balancing)
6. [Load balancing with round-robin and sticky sessions](#load-balancing-with-round-robin-and-sticky-sessions)
7. [Securing Traefik with HTTPS](#securing-traefik-with-https)
8. [Management UI](#management-ui)
9. [Integration API](#integration-api---static-web-site)


## Static Web site

We have made a static website by writing a `Dockerfile` and a `nginx.conf`. This allows us to deploy a static website
on a specific port with a configuration different than the default one.

Here's our configuration for nginx:

```yaml
server {
    listen 80;
    server_name memesarefun.ch;
    root /usr/share/nginx/html;

    location / {
        try_files $uri $uri/ =404;
    }
}
```

And here's our configuration for our `Dockerfile`: 

```yaml
FROM nginx
LABEL authors="quentinsurdez"

EXPOSE 80
COPY ./static /usr/share/nginx/html
COPY ./conf/nginx.conf /etc/nginx/conf.d/default.conf
```


## Docker Compose

To be able to build the image with the `docker compose build`
command, we have used a very simple configuration.

Here's the content of our `compose.yaml` file:

```yaml
services:
  web:
    build: .
    ports:
      - '7080:80'
```

The argument `build: .` build the docker image with what's inside the current directory.

## HTTP API server

Our API is quite simple but supports all CRUD operations. The purpose of our API is to respond to requests made by users about memes. We have some memes stored in a `ConcurrentHashMap` and the users can update, delete, post, or get our stored memes.

The content of a meme is as following:
- It has a name
- It has a content
- It has an url for the image

The format in which our memes are stored is JSON. It has been chosen for its versatility and its easiness to work with.

The tools we have used are `Insomnia` and the `Unirest` library.

A new service called `api` has been added to our compose file.

Here's the configuration of our service:

```yaml
  api:
    image: web_api
    container_name: web_api_container
    ports:
      - '9292:9292'
```

For facility of development, we have used the name of an image and we have given the created container a name. However to have the expected code, we should have `build: ./api` so that it will build from what's inside the directory of interest.


## Reverse proxy with Traefik

After spending quite some time reading the traefik documentation, we have been able to create a new service in the compose file named `reverse_proxy`. As its name suggests it is a reverse proxy.

Here's the configuration of our reverse proxy:

```yaml
  reverse_proxy:
    image: traefik:2.3.0
    command:
      - --log.level=INFO
      - --api.insecure=true
      - --entrypoints.web.address=:80
      - --providers.docker

    volumes:
      - /var/run/docker.sock:/var/run/docker.sock

    labels:
      traefik.http.routers.dash.rule: Host(`dash.localhost`)
      traefik.http.routers.dash.service: api@internal

    ports:
      - '80:80'
```

We have activated the api so that the traefik dashboard is activated. It is quite simple to access it. You need to run the docker compose file and once it is run, you just need to go to http://dash.localhost on your web browser and you will see the dashboard.

A reverse proxy is quite useful as it first protects the servers from the web. In deed, it hides
the servers behind itself. It also receives all the requests and distribute them on the servers according to a specific load-balancing strategy.

## Scalability and load balancing

The one thing to add for this to work properly is the replicas field in the services.

Here's an example of the configuration:

```yaml
  website:
    image: web_static
    container_name: web_static_website
    labels:
      traefik.enable: true
      traefik.http.routers.website.rule: Host(`localhost`)
    deploy:
      replicas: 5
    depends_on:
      - reverse_proxy
```

Then the replicas can be changed via this command `docker compose up -d --scale <instance_name>=<count>`

## Load balancing with round-robin and sticky sessions

Traefik strategy for load balancing is by default round-robin. This can be seen in the logs where each call to a server has a different IP address.

```

2024-01-15T09:54:52.913614279Z time="2024-01-15T09:54:52Z" level=debug msg="vulcand/oxy/roundrobin/rr: Forwarding this request to URL" ForwardURL="http://172.25.0.5:80" 


2024-01-15T09:55:07.036123371Z time="2024-01-15T09:55:07Z" level=debug msg="vulcand/oxy/roundrobin/rr: Forwarding this request to URL"  ForwardURL="http://172.25.0.3:80"

```

Here we have truncated requests made from different instances of web browsers on the same computer.

We can see that the requests are forwarded to different servers for the static web.

The behavior will look quite alike for the api servers. However the same client will always be answered by the same server.

## Securing Traefik with HTTPS

We have rummaged through the Traefik documentation to know exactly what needed to be done. We have added a configuration file named `traefik.yaml` and we mount it to a directory in our service `reverse_proxy`

Here's the complete configuration:

```yaml
# Traefik configuration file

tls:
  certificates:
    - certFile: /etc/traefik/certificates/server.cert
      keyFile: /etc/traefik/certificates/server.key

providers:
  docker:
    endpoint: "unix:///var/run/docker.sock"


entryPoints:
  web:
    address: ":80"

  websecure:
    address: ":443"

api:
  dashboard: true
  insecure: true
```

We also self-signed keys to have certificates. These are stored in the directory `cert` and are mounted in a directory within the `reverse_proxy` service.

Here's the configuration of our docker compose file:

```yaml
  reverse_proxy:
    image: traefik:2.8.0

    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      # Mounting the certificate and the key
      - ./cert:/etc/traefik/certificates
      # Mounting the configuration file
      - ./traefik.yaml:/etc/traefik/traefik.yaml

    labels:
      traefik.http.routers.dash.entrypoints: "websecure"
      traefik.http.routers.dash.rule: Host(`dash.localhost`)
      traefik.http.routers.dash.tls: true

    ports:
      - '80:80'
      - '443:443'
```

We have only added the tls line and the entrypoints line to other services with the value you see up here.

## Management UI

We have chosen the portainer ui. It is very easy to implement in our docker compose.
We only need to add a service in our docker compose.

Here's the configuration:

```yaml
  portainer:
    image: portainer/portainer-ce:latest
    container_name: portainer
    restart: unless-stopped
    security_opt:
      - no-new-privileges:true
    volumes:
      - /etc/localtime:/etc/localtime:ro
      - /var/run/docker.sock:/var/run/docker.sock:ro
      - ./portainer-data:/data
    labels:
      traefik.enable: true
      traefik.http.routers.portainer.rule: Host(`portainer.localhost`)
      traefik.http.routers.portainer.entrypoints: web
      traefik.http.services.portainer.loadbalancer.server.port: 9000

    depends_on:
      - reverse_proxy
```

We then need to register with the admin user with the password `adminadminadmin`.

Then we can choose what container we want to supervise.

## Integration API - static Web site

We have added a little script to call our API in the `index.html` file.

One function that calls the api is called every 5 seconds to fetch all images and put them in the website.