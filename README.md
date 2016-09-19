# UCC

Created by Peter Kristensen

- [Linkedin](https://www.linkedin.com/in/pekri)
- [GitHub](https://github.com/ptxmac/)

## Uber Coding Challenge

[Open Data Aarhus](https://odaa.dk) (ODAA) is a project trying to release data about the aarhus area to the public. 
ODAA is only focused on the data and not the use or visualization of it.
  
A subset of the data are location related and can be visualized on a map. 

This project is a attempt to do exactly that and bring the data closer to the public 
(This project is currently just a proof of concept, but shows the general idea)


## Borrowed components

The google maps API wrapper from [scalajs-google-maps](https://github.com/coreyauger/scalajs-google-maps)
 is copied as a source file because there isn't a production version on maven yet. 
(See [scalajs-google-maps / Issue #3](https://github.com/coreyauger/scalajs-google-maps/issues/3))

Map marker icons from <http://www.benjaminkeen.com/google-maps-coloured-markers/>

## Stack

I'm mostly a backend developer but this project was most fun when having the map, so it can be 
considered full-stack with little to no focus on UX / design.  

## Architechture and Design

The project is made up of a backend and frontend that communicated thru a very simple REST API.

Both backend and frontend are written in [Scala](http://scala-lang.org), and shares a little bit of code. 


### Backend

The backend is build on [Akka HTTP](http://doc.akka.io/docs/akka/2.4.10/scala/http/introduction.html). 
Akka is a Erlang-style actors system for Scala and Akka HTTP is a HTTP implementation on top of Akka.
  
The backend loads the datasets and starts serving API requests.

There's currently only two API calls:

- `/api/v1/datasets` Returns a list of known datasets and a tiny bit of meta data (name and icon)
- `/api/v1/datasets/<id>` Returns all elements for a given dataset.

### Frontend

The frontend is a Single-page application written in [Scala.js](https://scala-js.org). 

Scala.js is a Scala compiler that generates JavaScript - similar to TypeScript, CoffeeScript etc. Scala.js is a very 
complete system, and the number of features that can't be used from Scala is minimal.

Writing the frontend in a type-safe language opens up for some intersting features. 
E.g. both the HTML and CSS code is generated using Scala DSL libraries 
([ScalaTags](http://www.lihaoyi.com/scalatags/) and [ScalaCSS](https://japgolly.github.io/scalacss/book/))  
and are actually type-checked by the compiler.

### Shared

Since both frontend and backend are written in the same language it's possible to share common code between 
them. This is done my having the API replys as `case class`'s (Similar to a struct). Using [upickle](http://www.lihaoyi.com/upickle-pprint/upickle/) 
these are converted to and from JSON objects.

This way the compiler can help validating correct use of the API.

### Limitations 

- This version keeps all the datasets in memory     
  - The biggest dataset have about 1500 entries and doesn't warrent a whole database
- When requesting a dataset, the whole dataset is sent, not just the visible area
  - Sending the visuable coordiantes i nthe request will enable the backend to filter the data, but the client would need to update if the user moves the map.

## Data

All the data comes from Open Data Aarhus (<https://odaa.dk>) with little or no modifications (some datasets are converted from xslt to csv)

Currently I'm using the following datasets:

- Public toilets
- Trash cans
- Truck parking spots


## Development

To decrease turn-around-time this project uses a fix of workbench (link here) and sbt-resolver (another link here) to enable auto-reloading of both frontend and backend code.

Start the project using `sbt "~; frontend/fastOptJS; backend/re-start"`

Then go to: <http://localhost:12345/frontend/target/scala-2.11/index-dev.html>

Note: This will restart both frontend AND backend if any file in either parts are changed.

## Deployment

The project have been configured to be easy to deploy on heroku.

Note: to simplify deployment, the backend will serve both the API and the frontend. If scalability is needed, the frontend could be moved to a CDN.

The application is currently deployed at: https://tranquil-ridge-98607.herokuapp.com/

## Future features

- [ ] Use better style: <https://snazzymaps.com/>
- [ ] Only load visible area

