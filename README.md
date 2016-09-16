# UCC

## Borrowed components

The google maps API wrapper from <https://github.com/coreyauger/scalajs-google-maps> is copied to the src dir because there isn't a production version on maven yet. 
(See <https://github.com/coreyauger/scalajs-google-maps/issues/3>)

Map markers

http://www.benjaminkeen.com/google-maps-coloured-markers/

## Structure

The project is split into frontend and backend.

### Backend

### Frontend

### Shared

## Data

All the data comes from Open Data Aarhus (<https://odaa.dk>)


## Development

To decrease turn-around-time this project uses a fix of workbench (link here) and sbt-resolver (another link here) to enable auto-reloading of both frontend and backend code.

Start the project using `sbt "~; frontend/fastOptJS; backend/re-start"`

Then go to: <http://localhost:12345/frontend/target/scala-2.11/index-dev.html>

Note: This will restart both frontend AND backend if any file in either parts are changed.

## Deployment

The project have been configured to be easy to deploy on heroku.

Note: to simplify deployment, the backend will serve both the API and the frontend. If scalability is needed, the frontend could be moved to a CDN.

## TODO

- [ ] Use better style: <https://snazzymaps.com/>
- [ ] Differentiate markers per layer 
- [ ] Full optimized JS
- [ ] Error handling!
- [ ] Test json conversion
- [ ] Test frontend?
- [ ] Create postman dump