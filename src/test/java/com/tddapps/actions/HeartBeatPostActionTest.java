package com.tddapps.actions;

import com.fasterxml.jackson.databind.JsonNode;
import com.tddapps.controllers.BodyParseException;
import com.tddapps.utils.JsonNodeHelper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class HeartBeatPostActionTest {
    private final HeartBeatPostAction action = new HeartBeatPostAction();
    private final String MAXIMUM_LENGTH_ALLOWED_STRING = StringUtils.leftPad("", 100, "0");

    @Test
    public void ReadsTheHostId(){
        HeartBeatPostActionInput input = parseShouldSucceed("{\"hostId\": \"superHost1\"}");

        assertEquals("superHost1", input.getHostId());
    }

    @Test
    public void ReadsTheMaximumLengthHostId(){
        HeartBeatPostActionInput input = parseShouldSucceed(String.format(
                "{\"hostId\": \"%s\"}", MAXIMUM_LENGTH_ALLOWED_STRING
        ));
        assertEquals(MAXIMUM_LENGTH_ALLOWED_STRING, input.getHostId());
    }

    @Test
    public void ParsingFailsWhenHostIdIsMissing(){
        parseShouldThrow("{}");
        parseShouldThrow("{\"hostId\": \"\"}");
        parseShouldThrow("{\"hostId\": \"   \"}");
    }

    @Test
    public void ParsingFailsWhenHostIdIsNotAlphanumeric(){
        parseShouldThrow("{\"hostId\": \"-!@#$$%^%^ &^&\"}");
    }

    @Test
    public void ParsingFailsWhenHostIdIsTooLong(){
        parseShouldThrow(String.format(
                "{\"hostId\": \"X%s\"}", MAXIMUM_LENGTH_ALLOWED_STRING
        ));
    }

    private void parseShouldThrow(String body){
        try {
            parseInternal(body);
            fail("Expected Exception to have been thrown");
        } catch (BodyParseException e) {
            assertNotNull(e);
        }
    }

    private HeartBeatPostActionInput parseShouldSucceed(String body){
        try {
            return parseInternal(body);
        } catch (BodyParseException e) {
            fail("Parse should not have thrown", e);
            return null;
        }
    }

    private HeartBeatPostActionInput parseInternal(String body) throws BodyParseException{
        JsonNode seededBody = null;
        try {
            seededBody = JsonNodeHelper.parse(body);
        } catch (IOException e) {
            fail("Parsing seeded body shouldn't throw", e);
        }
        return action.parse(seededBody);
    }
}
