# UCC

## Borrowed components

The google maps API wrapper from <https://github.com/coreyauger/scalajs-google-maps> is copied 




## Structure

The project is split into frontend and backend.

### Backend

### Frontend

### Shared

## Development

To decrease turn-around-time this project uses a fix of workbench (link here) and sbt-resolver (another link here) to enable auto-reloading of both frontend and backend code.

Start the project using `sbt "~; frontend/fastOptJS; backend/re-start"`

Then go to: <http://localhost:12345/frontend/target/scala-2.11/index-dev.html>

Note: This will restart both frontend AND backend if any file in either parts are changed.

## Deployment

Heroku? 

## TODO

- [ ] Use better style: <https://snazzymaps.com/>