# relaxedbase

This project was created in the Relaxdays Code Challenge Vol. 1. 
See https://sites.google.com/relaxdays.de/hackathon-relaxdays/startseite for more information. 
My participant ID in the challenge was: CC-VOL1-27

To build:

```
./mvnw -Pprod verify jib:dockerBuild
```

Then run:

```
docker-compose -f src/main/docker/app.yml up -d
```
