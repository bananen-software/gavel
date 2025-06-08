# gavel

> I swing this sword like a gavel in the truest court

- Mindforce - Excalibur

Gavel is a toolkit that can be used to gather quick insights into the quality of Software Architectures, without
bringing in a lot of tools.

## Supported metrics

* Code Hotspots
* Component Dependency Metrics
* Component Visibility
* Cumulative Component Dependency
* Depth of Inheritance Tree
* File Complexity History
* Relational Cohesion
* Author Complexity History
* Change Coupling

## Status

Project is still a rough WIP mostly a project for Hacktober '24.

There are still a lot of missing tests for everything and the architecture is currently very incidental.

Hopefully I'll be able to clean this up at some point in the future.

## Running the application

You'll need a PostgreSQL database on your local machine that stores the analysis results.
The database can e.g. be started in a Docker container with this command:

`docker run --name gavel-postgres -e POSTGRES_PASSWORD=mysecretpassword -p 5432:5432 -d postgres:17-alpine`

Some things are currently hardcoded in the backend, so you might have to adjust the code to match your local workspace.
As described under Status the project is still a WIP and very hacky.

