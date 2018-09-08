package com.tddapps.controllers;

import com.tddapps.actions.response.TextMessage;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;
import static com.tddapps.utils.EqualityAssertions.*;


public class HttpJsonResponseTest {
    @Test
    public void PredefinedResponses(){
        shouldBeEqual(
                new HttpJsonResponse<>(200, TextMessage.OK),
                HttpJsonResponse.Success(TextMessage.OK)
        );

        shouldBeEqual(
                new HttpJsonResponse<>(400, TextMessage.create("missing parameter")),
                HttpJsonResponse.BadRequest(TextMessage.create("missing parameter"))

        );

        shouldBeEqual(
                new HttpJsonResponse<>(400, TextMessage.create("missing parameter")),
                HttpJsonResponse.BadRequestWithMessage("missing parameter")
        );

        shouldBeEqual(
                new HttpJsonResponse<>(500, TextMessage.create("database error")),
                HttpJsonResponse.ServerError(TextMessage.create("database error"))

        );

        shouldBeEqual(
                new HttpJsonResponse<>(500, TextMessage.create("database error")),
                HttpJsonResponse.ServerErrorWithMessage("database error")
        );
    }

    @Test
    public void CanBeCompared(){
        val response1 = new HttpJsonResponse<TextMessage>(200, TextMessage.OK);
        shouldBeEqual(response1, response1);

        val response1Clone = new HttpJsonResponse<TextMessage>(200, TextMessage.OK);
        shouldBeEqual(response1, response1Clone);

        shouldNotBeEqual(null, response1);

        shouldBeEqual(
                new HttpJsonResponse<>(200, TextMessage.OK),
                new HttpJsonResponse<>(200, TextMessage.OK)
        );
        shouldBeEqual(
                new HttpJsonResponse<>(500, "blah"),
                new HttpJsonResponse<>(500, "blah")
        );

        shouldNotBeEqual(
                new HttpJsonResponse<>(500, "foo"),
                new HttpJsonResponse<>(500, "bar")
        );
        shouldNotBeEqual(
                new HttpJsonResponse<>(501, "blah"),
                new HttpJsonResponse<>(500, "blah")
        );

        shouldBeEqual(
                new HttpJsonResponse<>(200, null),
                new HttpJsonResponse<>(200, null)
        );

        shouldNotBeEqual(
                new HttpJsonResponse<>(200, TextMessage.OK),
                new HttpJsonResponse<>(200, null)
        );
        shouldNotBeEqual(
                new HttpJsonResponse<>(200, null),
                new HttpJsonResponse<>(200, TextMessage.OK)
        );
    }

    @Test
    public void CreatesDecentStringRepresentations(){
        assertEquals(
                "HttpJsonResponse(400) TextMessage:sample",
                HttpJsonResponse.BadRequestWithMessage("sample").toString()
        );

        assertEquals(
                "HttpJsonResponse(100) null",
                new HttpJsonResponse<String>(100, null).toString()
        );

        assertEquals(
                "HttpJsonResponse(100) 234",
                new HttpJsonResponse<Integer>(100, 234).toString()
        );
    }
}
