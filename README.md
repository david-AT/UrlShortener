# Web Engineering 2020-2021 / URL Shortener

[![Build Status](https://travis-ci.com/david-AT/UrlShortener.svg?branch=master)](https://travis-ci.com/david-AT/UrlShortener)

The __project__ is a [Spring Boot](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/) application that offers a set of basic functionalities:

* __Short URL creation service__:  `POST /` creates a shortened URL from a URL in the request parameter `url`.
* __Redirection service__: `GET /{id}` redirects the request to a URL associated with the parameter `id`.
* __Database service__: Persistence and retrieval of `ShortURL` and `Click` objects.

New functionalities added:

* __Check accesible url__: Before shortening the URL it is checked that it returns a `200` response.
* __Database info__: In the endpoint `/actuator/info` you can see information about the number of clicks that each shortened URL has.
* __User Agents__: View information on current user agents.
* __Short CSV__: The application allows shortening the URLs of a `CSV`, returning another with the results (scalable with `Websockets`).
* __Get QR code__: The application can generate a `QR` code asynchronously when a URL is shortened.

Link to the presentation made in class -> [presentacionProyecto_GrupoI_IW.pdf](presentacionProyecto_GrupoI_IW.pdf)

Link to the final presentation -> [presentacionFinalProyecto_GrupoI_IW.pdf](presentacionFinalProyecto_GrupoI_IW.pdf)

The application can be run in Linux and macOS as follows:

```
$ ./gradlew bootRun
```
or in Windows

```
$ gradle.bat bootRun
```

Gradle will compile project and then run it. Now you have a shortener service running at port 8080. 

The APP main page is accessible from the browser a the URL:
```
localhost:8080
```

The `open API` documentation of the APIs is accessible at the URL ([web reference](https://www.baeldung.com/spring-rest-openapi-documentation)):
```
localhost:8080/openapi.html
```

To check the correct operation from the terminal you can do:

```bash
$ curl -v -d "url=http://www.unizar.es/" -X POST http://localhost:8080/link
> POST / HTTP/1.1
> User-Agent: curl/7.37.1
> Host: localhost:8080
> Accept: */*
> Content-Length: 25
> Content-Type: application/x-www-form-urlencoded
>
* upload completely sent off: 25 out of 25 bytes
< HTTP/1.1 201 Created
< Server: Apache-Coyote/1.1
< Location: http://localhost:8080/6bb9db44
< Content-Type: application/json;charset=UTF-8
< Transfer-Encoding: chunked
<
* Connection #0 to host localhost left intact
{"hash":"6bb9db44","target":"http://www.unizar.es/","uri":"http://localhost:8080/6bb9db44",
"sponsor":null,"created":"2019-09-10","owner":"112b6444-0a05-4e48-a13f-27ddf23349e2","mode":307,
"safe":true,"ip":"0:0:0:0:0:0:0:1","country":null}%
```

Now, we can navigate to the shortened URL.

```bash
$ curl -v http://localhost:8080/6bb9db44
> GET /6bb9db44 HTTP/1.1
> User-Agent: curl/7.37.1
> Host: localhost:8080
> Accept: */*
>
< HTTP/1.1 307 Temporary Redirect
< Server: Apache-Coyote/1.1
< Location: http://www.unizar.es/
< Content-Length: 0
<
```
