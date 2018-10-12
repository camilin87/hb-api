package com.tddapps.handlers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.StreamRecord;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.tddapps.ioc.IocContainer;
import com.tddapps.model.*;
import com.tddapps.model.aws.DynamoDBEventParser;
import lombok.Data;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.tddapps.model.HeartBeatFactory.TEST_REGION_DEFAULT;
import static com.tddapps.utils.DateExtensions.EpochSecondsNow;
import static com.tddapps.utils.DateExtensions.ToReverseUtcMinuteString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HeartBeatChangeTest {
    private final NotificationSender notificationSender = mock(NotificationSender.class);
    private final RequestHandlerHelper requestHandlerHelper = mock(RequestHandlerHelper.class);
    private final HeartBeatNotificationBuilderOneToOneStub notificationBuilder = new HeartBeatNotificationBuilderOneToOneStub();
    private final DynamoDBEventParser eventParser = mock(DynamoDBEventParser.class);
    private final HeartBeatChange handler = new HeartBeatChange(
            notificationBuilder,
            notificationSender,
            IocContainer.getInstance().Resolve(DynamoDBMapper.class),
            requestHandlerHelper,
            eventParser
    );

    private final long ttlNow = EpochSecondsNow();
    private final String ttlNowString = String.format("%d", ttlNow);
    private final String reversedUtcMinuteNowString = ToReverseUtcMinuteString(ttlNow);

    @Deprecated
    @Data
    private static class HeartBeatEvent{
        final String name;
        final String hostId;
        final String ttl;
        final String expirationMinuteUtc;
        final String region;
        final String test;
    }

    @Test
    public void CanBeConstructedUsingTheDefaultConstructor(){
        assertNotNull(new HeartBeatChange());
    }

    @Test
    public void SendsANotificationForEachDeletedOrInsertedRecord() throws DalException {
        when(requestHandlerHelper.filter(any())).then(i -> i.getArgument(0));
        val result = handleRequest(
                new HeartBeatEvent("MODIFY", "host1", ttlNowString, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, "0"),
                new HeartBeatEvent("INSERT", "host2", ttlNowString, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, "0"),
                new HeartBeatEvent("REMOVE", "host3", ttlNowString, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, "0"),
                new HeartBeatEvent("REMOVE", "host4", ttlNowString, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, "0"),
                new HeartBeatEvent("INSERT", "host5", ttlNowString, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, "0"),
                new HeartBeatEvent("INSERT", "host6", ttlNowString, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, "0"),
                new HeartBeatEvent("MODIFY", "host7", ttlNowString, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, "0")
        );

        assertTrue(result);
        verify(notificationSender, times(5))
                .Send(any(Notification.class));
        verify(notificationSender).Send(new Notification("S-host2", "M-host2-Hosts registered"));
        verify(notificationSender).Send(new Notification("S-host3", "M-host3-Hosts missing"));
        verify(notificationSender).Send(new Notification("S-host4", "M-host4-Hosts missing"));
        verify(notificationSender).Send(new Notification("S-host5", "M-host5-Hosts registered"));
        verify(notificationSender).Send(new Notification("S-host6", "M-host6-Hosts registered"));
    }

    @Test
    public void SendsNotificationOnlyForRecordsInTheCurrentRegion() throws DalException {
        when(requestHandlerHelper.filter(any())).then(i -> {
            HeartBeat[] heartBeats = i.getArgument(0);
            return Arrays.stream(heartBeats)
                    .filter(hb -> hb.getRegion().equals(TEST_REGION_DEFAULT))
                    .toArray(HeartBeat[]::new);
        });
        val result = handleRequest(
                new HeartBeatEvent("REMOVE", "host1", ttlNowString, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, "0"),
                new HeartBeatEvent("REMOVE", "host2", ttlNowString, reversedUtcMinuteNowString, "us-test-2", "0"),
                new HeartBeatEvent("REMOVE", "host3", ttlNowString, reversedUtcMinuteNowString, "us-test-2", "0"),
                new HeartBeatEvent("REMOVE", "host4", ttlNowString, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, "0"),
                new HeartBeatEvent("INSERT", "host5", ttlNowString, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, "0"),
                new HeartBeatEvent("INSERT", "host6", ttlNowString, reversedUtcMinuteNowString, "us-test-2", "0"),
                new HeartBeatEvent("INSERT", "host7", ttlNowString, reversedUtcMinuteNowString, "us-test-2", "0"),
                new HeartBeatEvent("INSERT", "host8", ttlNowString, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, "0")
        );

        assertTrue(result);
        verify(notificationSender, times(4))
                .Send(any(Notification.class));
        verify(notificationSender).Send(new Notification("S-host1", "M-host1-Hosts missing"));
        verify(notificationSender).Send(new Notification("S-host4", "M-host4-Hosts missing"));
        verify(notificationSender).Send(new Notification("S-host5", "M-host5-Hosts registered"));
        verify(notificationSender).Send(new Notification("S-host8", "M-host8-Hosts registered"));
    }

    @Test
    public void DoesNotSendNotificationForTestRecords() throws DalException {
        when(requestHandlerHelper.filter(any())).then(i -> i.getArgument(0));
        val result = handleRequest(
                new HeartBeatEvent("INSERT", "host1", ttlNowString, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, "0"),
                new HeartBeatEvent("REMOVE", "host2", ttlNowString, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, "0"),
                new HeartBeatEvent("INSERT", "host3", ttlNowString, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, "0"),
                new HeartBeatEvent("REMOVE", "host4", ttlNowString, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, "0"),
                new HeartBeatEvent("INSERT", "host5", ttlNowString, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, "1"),
                new HeartBeatEvent("REMOVE", "host6", ttlNowString, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, "1")
        );

        assertTrue(result);
        verify(notificationSender, times(4))
                .Send(any(Notification.class));
    }

    @Test
    public void NoNotificationsAreSentOnModifications() throws DalException {
        when(requestHandlerHelper.filter(any())).then(i -> i.getArgument(0));
        val result = handleRequest(
                new HeartBeatEvent("MODIFY", "host1", ttlNowString, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, "0"),
                new HeartBeatEvent("MODIFY", "host2", ttlNowString, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, "0"),
                new HeartBeatEvent("MODIFY", "host5", ttlNowString, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, "0")
        );

        assertTrue(result);
        verify(notificationSender, times(0))
                .Send(any(Notification.class));
    }

    @Test
    public void ReturnsFalseWhenNotificationsCannotBeSent() throws DalException {
        doThrow(new DalException("Send failed"))
                .when(notificationSender)
                .Send(any(Notification.class));

        when(requestHandlerHelper.filter(any())).then(i -> i.getArgument(0));
        val result = handleRequest(
                new HeartBeatEvent("REMOVE", "host1", ttlNowString, reversedUtcMinuteNowString, TEST_REGION_DEFAULT, "0")
        );

        assertFalse(result);
    }

    @Deprecated
    private boolean handleRequest(HeartBeatEvent ... seededEvents){
        List<DynamodbEvent.DynamodbStreamRecord> seededRecords = Arrays.stream(seededEvents)
                .map(e -> {
                    val d = new StreamRecord();
                    d.setKeys(new HashMap<String, AttributeValue>() {{
                        put("host_id", new AttributeValue(e.getHostId()));
                    }});

                    val imageData = new HashMap<String, AttributeValue>() {{
                        put("host_id", new AttributeValue().withS(e.getHostId()));
                        put("ttl", new AttributeValue().withN(e.getTtl()));
                        put("expiration_minute_utc", new AttributeValue().withS(e.getExpirationMinuteUtc()));
                        put("region", new AttributeValue().withS(e.getRegion()));
                        put("test", new AttributeValue().withN(e.getTest()));
                    }};

                    if (e.getName().equals("INSERT")) {
                        d.setNewImage(imageData);
                    }
                    else {
                        d.setOldImage(imageData);
                    }

                    val result = new DynamodbEvent.DynamodbStreamRecord();
                    result.setEventName(e.getName());
                    result.setDynamodb(d);

                    return result;
                })
                .collect(Collectors.toList());

        val input = new DynamodbEvent();
        input.setRecords(seededRecords);

        return handler.handleRequest(input, null);
    }
}
