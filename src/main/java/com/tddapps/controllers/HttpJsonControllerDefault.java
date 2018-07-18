package com.tddapps.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.tddapps.utils.JsonNodeHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

public class HttpJsonControllerDefault implements HttpJsonController {
    private final HttpJsonAction action;

    private static final Logger LOG = LogManager.getLogger(HttpJsonControllerDefault.class);

    public HttpJsonAction getAction() {
        return action;
    }

    public HttpJsonControllerDefault(HttpJsonAction action) {
        this.action = action;
    }

    @Override
    public HttpJsonResponse process(Map<String, Object> input){
        try {
            String requestBody = readBodyFrom(input);
            LOG.debug(String.format("Body: %s", requestBody));

            if (requestBody.trim().isEmpty()){
                return HttpJsonResponse.BadRequestWithMessage("Empty Request Body");
            }

            JsonNode jsonBody = JsonNodeHelper.parse(requestBody);
            Object parsedBody = action.parse(jsonBody);
            return action.process(parsedBody);

        } catch (IOException e) {
            LOG.warn("Invalid json in request body", e);
            return HttpJsonResponse.BadRequestWithMessage("Invalid json in request body");
        }
        catch (ActionBodyParseException e) {
            LOG.warn("Action parsing failed", e);
            return HttpJsonResponse.BadRequestWithMessage(e.getMessage());
        }
        catch (ActionProcessException e) {
            LOG.error("Action processing failed", e);
            return HttpJsonResponse.ServerErrorWithMessage(e.getMessage());
        }
    }

    private String readBodyFrom(Map<String, Object> input){
        Object bodyObject = input.getOrDefault("body", "");

        if (bodyObject == null){
            return "";
        }

        return bodyObject.toString();
    }
}
