# relaxedbase

This project was created in the Relaxdays Code Challenge Vol. 1.

See https://sites.google.com/relaxdays.de/hackathon-relaxdays/startseite for more information.

Our participant IDs in the challenge were: CC-VOL1-27, CC-VOL1-61

Demo video under releases.

To build:

```
./mvnw -Pprod verify jib:dockerBuild
```

Then run:

```
docker-compose -f src/main/docker/app.yml up -d
```
