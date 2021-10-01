# gaia-runner

gaia-runner is the service that runs the Terraform jobs in Gaia.

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=gaia-app%3Arunner&metric=alert_status)](https://sonarcloud.io/dashboard?id=gaia-app%3Arunner)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=gaia-app%3Arunner&metric=coverage)](https://sonarcloud.io/dashboard?id=gaia-app%3Arunner)
![Docker Pulls](https://img.shields.io/docker/pulls/gaiaapp/runner)

## What is it?

Gaia is a web application to import and run your Terraform modules. See [Gaia on Github](https://github.com/gaia-app/gaia).

This service works in conjonction with Gaia.

It features : 
* running modules (plan/apply/destroy) in a dedicated docker container
* publish state to Gaia

## Documentation

Go to [docs.gaia-app.io](https://docs.gaia-app.io) for the full documentation.

## Requirements

Gaia-runner needs :
 * a docker daemon (used to run Terraform itself)
 * a running Gaia instance

## Quick start

See the documentation at [https://docs.gaia-app.io/](https://docs.gaia-app.io/getting-started/quick-start/) for quick-start instructions.

## Contributors

Gaia is made with ❤️ in  🇫🇷 by [Cyril DUBUISSON](https://github.com/cdubuisson) and [Julien WITTOUCK](https://github.com/juwit)
