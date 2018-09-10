#!/usr/bin/bash

mvn clean install && \
    sls sam export --output .sam_template.yml --dynamoDbEndpointOverride="http://localhost:8000" && \
    (trap 'kill 0' SIGINT; \
        sls dynamodb start --stage dev & \
        ( \
            sam local start-api -t .sam_template.yml \
        ) \
    )
