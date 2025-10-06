#!/bin/bash
docker build -f ./Dockerfile -t proj-liweida-service .
docker compose -p proj-liweida-service --env-file $1.env --compatibility up -d
