package com.tddapps.dal;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.Date;
import java.util.Objects;

import static com.tddapps.utils.DateExtensions.AreAlmostEquals;
import static com.tddapps.utils.DateExtensions.ToUtcString;
import static com.tddapps.utils.StringExtensions.EmptyWhenNull;

@DynamoDBTable(tableName = "heartbeats")
public class HeartBeat {
    @DynamoDBHashKey(attributeName = "host_id")
    private String hostId;

    @DynamoDBAttribute(attributeName = "is_test")
    private boolean isTest;

    @DynamoDBAttribute(attributeName = "expiration_utc_datetime")
    private Date expirationUtc;

    public HeartBeat(){ }
    public HeartBeat(String hostId, Date expirationUtc){
        this(hostId, expirationUtc, false);
    }
    public HeartBeat(String hostId, Date expirationUtc, boolean isTest){
        this.hostId = hostId;
        this.expirationUtc = expirationUtc;
        this.isTest = isTest;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public Date getExpirationUtc() {
        return expirationUtc;
    }

    public void setExpirationUtc(Date expirationUtc) {
        this.expirationUtc = expirationUtc;
    }

    public boolean isTest() {
        return isTest;
    }

    public void setTest(boolean test) {
        isTest = test;
    }

    @Override
    public String toString() {
        return String.format(
                "%s, expirationUtc: %s, hostId: %s, isTest: %s",
                getClass().getSimpleName(),
                ToUtcString(getExpirationUtc(), "null"),
                EmptyWhenNull(hostId),
                isTest
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(isTest, hostId, expirationUtc);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HeartBeat)){
            return false;
        }

        HeartBeat that = (HeartBeat)obj;

        if (!this.almostEquals(that)){
            return false;
        }

        return this.expirationUtc.equals(that.expirationUtc);
    }

    public boolean almostEquals(HeartBeat that) {
        if (that == null){
            return false;
        }

        if(this.isTest != that.isTest){
            return false;
        }

        if (!EmptyWhenNull(this.hostId).equals(EmptyWhenNull(that.hostId))){
            return false;
        }

        return AreAlmostEquals(this.expirationUtc, that.expirationUtc);
    }
}
