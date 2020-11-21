# xite-url-shortener
 
### Local Setup
1. Install psql
2. From terminal run `psql` it would open sql editor
3. Run the below commands to generate the schema
```
CREATE DATABASE shorturl;

\c shorturl;

# Create table, if db migrate doesn't generate table.
CREATE TABLE "urls" (
"short_url" TEXT NOT NULL,
"original_url" TEXT NOT NULL,
CONSTRAINT "urls_pk" PRIMARY KEY ("short_url")
);
```
4. Test the project `sbt test`
5. Run the project `sbt run`
6. From terminal, may call services

#### Create Short Url
`curl -i -d '{"shortUrl": "", "originalUrl": "http://www.gmail.com"}' -H "Content-Type: application/json" -X POST http://127.0.0.1:53248/shorturl `  

##### Response 
```
HTTP/1.1 200 OK
Content-Type: application/json
Date: Sat, 21 Nov 2020 16:07:58 GMT
Content-Length: 82

{"shortUrl":"http://127.0.0.1:53248/shorturl/-lu1390","originalUrl":"http://www.gmail.com"}
```

#### All short urls and original urls
`curl -i -X GET http://127.0.0.1:53248/shorturls`
##### Response
```
HTTP/1.1 200 OK
Content-Type: text/plain; charset=UTF-8
Date: Sat, 21 Nov 2020 16:05:40 GMT
Transfer-Encoding: chunked

[{"shortUrl":"http://127.0.0.1:53248/shorturl/-p7hec0","originalUrl":"http://www.google.com"},{"shortUrl":"http://127.0.0.1:53248/shorturl/395sfx","originalUrl":"http://www.yahoo.com"}]
```

#### Use Short Url in Browser
`http://127.0.0.1:53248/shorturl/-p7hec0`
##### Response
It would redirect to the original url