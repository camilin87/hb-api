package com.tddapps.model;

public interface HeartBeatRepository {
    void Save(HeartBeat[] heartBeat) throws DalException;
    void Delete(HeartBeat[] heartBeats) throws DalException;
    HeartBeat[] Read(String expirationMinuteUtc, long ttl, int maxCount) throws DalException;
}
