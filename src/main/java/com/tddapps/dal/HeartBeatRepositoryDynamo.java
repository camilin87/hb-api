package com.tddapps.dal;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HeartBeatRepositoryDynamo implements HeartBeatRepository {
    private static final Logger LOG = LogManager.getLogger(HeartBeatRepositoryDynamo.class);

    private final DynamoDBMapperFactory mapperFactory;

    public HeartBeatRepositoryDynamo(DynamoDBMapperFactory mapperFactory){
        this.mapperFactory = mapperFactory;
    }

    @Override
    public void Save(HeartBeat heartBeat) throws DalException {
        try {
            mapperFactory.getMapper().save(heartBeat);
        } catch (AmazonClientException e){
            LOG.error("HeartBeat Save Error", e);
            throw new DalException(e.getMessage());
        }
    }

    @Override
    public HeartBeat[] All() throws DalException {
        try {
            return mapperFactory.getMapper()
                    .scan(HeartBeat.class, new DynamoDBScanExpression())
                    .toArray(new HeartBeat[0]);
        } catch (AmazonClientException e){
            LOG.error("HeartBeat All Error", e);
            throw new DalException(e.getMessage());
        }
    }
}
