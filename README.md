# FINT Altinn Service

Lots of love from Arkivlaget <3 To be continued.

## Depends on

- **[Altinn Ebevis Service](https://github.com/fintlabs/fint-altinn-ebevis-service)**:
  Handles request for consent as well as retrieval of tax sertificate from Skatteetaten and bankruptcy certificate from
  Brønnøysundregisteret.

- **[FLYT Altinn Gateway](https://github.com/fintlabs/fint-flyt-altinn-gateway)**:
  Handles further transfer to FINT Flyt to record the taxi application in the archive systems.

## Development

### Database

Postgres is used as database. Using [flyway][] for database migrations.

[flyway]: https://documentation.red-gate.com/flyway/getting-started-with-flyway/quickstart-guides/quickstart-gradle

### Get instances from Altinn

If you don't want to wait for the scheduled task, you can run `POST` request to
fetch instances from Altinn: `/beta/api/event/push`


### Logs

```bash
stern 'altinn-(ebevis|service|gateway)' --since 5m --namespace default,bfk-no
```