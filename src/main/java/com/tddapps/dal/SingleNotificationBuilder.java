package com.tddapps.dal;

import java.util.Arrays;

public class SingleNotificationBuilder implements HeartBeatNotificationBuilder {
    @Override
    public Notification[] build(HeartBeat[] heartBeats) {
        if (isEmpty(heartBeats)){
            return NoNotifications();
        }

        String hostnames = Arrays.stream(heartBeats)
                .map(HeartBeat::getHostId)
                .reduce((a, b) -> String.format("%s, %s", a, b))
                .orElse("");

        String heartBeatDetails = Arrays.stream(heartBeats)
                .map(HeartBeat::toString)
                .reduce((a, b) -> String.format("%s\n%s", a, b))
                .orElse("");

        String subject = String.format("Hosts missing [%s]", hostnames);

        String message = String.format("%s\n\n%s\n--", subject, heartBeatDetails);

        return new Notification[]{
                new Notification(subject, message)
        };
    }

    private boolean isEmpty(HeartBeat[] heartBeats) {
        return heartBeats == null || heartBeats.length == 0;
    }

    private Notification[] NoNotifications() {
        return new Notification[0];
    }
}
