package com.tddapps.model.notifications.internal;

import com.tddapps.model.heartbeats.HeartBeat;
import com.tddapps.model.notifications.NotificationMetadata;
import com.tddapps.utils.NowReader;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.tddapps.model.heartbeats.test.HeartBeatFactory.TEST_REGION_DEFAULT;
import static com.tddapps.utils.DateExtensions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SingleNotificationBuilderTest {
    private final NowReader nowReaderMock = mock(NowReader.class);
    private final SingleNotificationBuilder builder = new SingleNotificationBuilder(nowReaderMock);

    private String utcNowFormatted;

    @BeforeEach
    void Setup(){
        val seededDate = UtcNowPlusMs(1000);
        utcNowFormatted = ToUtcString(seededDate);

        when(nowReaderMock.ReadUtc())
                .thenReturn(seededDate);
    }

    @Test
    void DoesNotSendNotificationWhenNoHeartBeatsAreProvided(){
        assertEquals(0, builder.build(new NotificationMetadata(""), new HeartBeat[0]).length);
    }

    @Test
    void SendsNotificationForASingleHeartBeat() {
        val hb1 = new HeartBeat("host1", EpochSecondsNow(), ToReverseUtcMinuteString(EpochSecondsNow()), TEST_REGION_DEFAULT, false);

        val notifications = builder.build(new NotificationMetadata("deleted"), new HeartBeat[] { hb1 });
        assertEquals(1, notifications.length);
        val notification = notifications[0];

        assertEquals("deleted [host1]", notification.getSubject());
        val expectedBody = "deleted [host1]\n" +
                "\n" +
                hb1.toString() +
                "\n" +
                "--" +
                "\n" +
                "Notification Built: " + utcNowFormatted +
                "\n" +
                "--";
        assertEquals(expectedBody, notification.getMessage());
    }

    @Test
    void SendsOneNotificationForMultipleHeartBeats() {
        val hb1 = new HeartBeat("host1", EpochSecondsNow(), ToReverseUtcMinuteString(EpochSecondsNow()), TEST_REGION_DEFAULT, false);
        val hb2 = new HeartBeat("host2", EpochSecondsNow(), ToReverseUtcMinuteString(EpochSecondsNow()), TEST_REGION_DEFAULT, false);

        val notifications = builder.build(new NotificationMetadata("deleted"), new HeartBeat[] { hb1, hb2 });
        assertEquals(1, notifications.length);
        val notification = notifications[0];

        assertEquals("deleted [host1, host2]", notification.getSubject());
        val expectedBody = "deleted [host1, host2]\n" +
                "\n" +
                hb1.toString() +
                "\n" +
                hb2.toString() +
                "\n" +
                "--" +
                "\n" +
                "Notification Built: " + utcNowFormatted +
                "\n" +
                "--";
        assertEquals(expectedBody, notification.getMessage());
    }
}
