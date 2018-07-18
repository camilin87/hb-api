package com.tddapps.actions;

import java.util.Objects;

import static com.tddapps.utils.StringExtensions.*;

public class HeartBeatPostActionInput {
    public static final int DEFAULT_INTERVAL_MS = 10*60*1000;

    private final String hostId;
    private final int intervalMs;

    public HeartBeatPostActionInput(String hostId, int intervalMs) {
        this.hostId = hostId;
        this.intervalMs = intervalMs;
    }

    @Override
    public String toString() {
        return String.format(
                "%s, intervalMs: %d, hostId: %s",
                getClass().getSimpleName(),
                getIntervalMs(),
                EmptyWhenNull(hostId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(intervalMs, hostId);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HeartBeatPostActionInput)){
            return false;
        }

        HeartBeatPostActionInput that = (HeartBeatPostActionInput)obj;

        if (this.intervalMs != that.intervalMs){
            return false;
        }

        return EmptyWhenNull(this.hostId).equals(EmptyWhenNull(that.hostId));
    }

    public String getHostId() {
        return hostId;
    }

    public int getIntervalMs() {
        return intervalMs;
    }
}
