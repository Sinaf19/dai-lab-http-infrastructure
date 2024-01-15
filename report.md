# Report for lab 4

Authors: Rachel Tranchida, Quentin Surdez


# Table of contents
1. [Static Web site](#static-web-site)
2. [Docker Compose](#docker-compose)
3. [HTTP API server](#http-api-server)
4. [Revers proxy with Traefik](#reverse-proxy-with-traefik)
5. [Scalability and load balancing](#scalability-and-load-balancing)
6. [Load balancing with round-robin and sticky sessions](#load-balancing-with-round-robin-and-sticky-sessions)
7. 


## Static Web site

TODO Explain the content of nginx.conf :(((


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


## TODO

Check if the replicas are working and if sticky sessions and round-robin are working as well.
It should but I haven't tested it yet :)