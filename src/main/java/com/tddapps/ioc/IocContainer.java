package com.tddapps.ioc;

import com.tddapps.model.heartbeats.HeartBeatJsonConverter;
import com.tddapps.model.heartbeats.HeartBeatParser;
import com.tddapps.model.heartbeats.HeartBeatRepository;
import com.tddapps.model.heartbeats.RequestHandlerHelper;
import com.tddapps.model.heartbeats.internal.HeartBeatSerializer;
import com.tddapps.model.heartbeats.internal.RequestHandlerHelperCurrentRegion;
import com.tddapps.model.infrastructure.internal.EnvironmentSettingsReader;
import com.tddapps.model.infrastructure.internal.InMemoryKeysCacheWithExpiration;
import com.tddapps.model.infrastructure.KeysCache;
import com.tddapps.model.infrastructure.SettingsReader;
import com.tddapps.model.internal.aws.*;
import com.tddapps.model.notifications.*;
import com.tddapps.model.notifications.internal.NotificationBuilderGrouped;
import com.tddapps.model.notifications.internal.SingleNotificationBuilder;
import com.tddapps.utils.NowReader;
import com.tddapps.utils.internal.NowReaderImpl;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;

import static org.picocontainer.Characteristics.CACHE;

public class IocContainer {
    private static final IocContainer sharedInstance = new IocContainer();

    private final PicoContainer resolver;

    public static IocContainer getInstance(){
        return sharedInstance;
    }

    private IocContainer(){
        resolver = RegisterBindings();
    }

    public <T> T Resolve(Class<T> type){
        return resolver.getComponent(type);
    }

    private PicoContainer RegisterBindings() {
        return new DefaultPicoContainer()
                .addComponent(HeartBeatParser.class, HeartBeatSerializer.class)
                .addComponent(HeartBeatJsonConverter.class, HeartBeatSerializer.class)
                .addComponent(HeartBeatRepository.class, HeartBeatRepositoryDynamo.class)
                .addComponent(DynamoDBEventParser.class, DynamoDBEventParserMarshaller.class)
                .addComponent(NotificationSender.class, NotificationSenderSns.class)
                .addComponent(SettingsReader.class, EnvironmentSettingsReader.class)
                .addComponent(NowReader.class, NowReaderImpl.class)
                .addComponent(RequestHandlerHelper.class, RequestHandlerHelperCurrentRegion.class)
                .addComponent(HeartBeatNotificationBuilder.class, SingleNotificationBuilder.class)
                .addComponent(HeartBeatChangeEventNotificationBuilder.class, NotificationBuilderGrouped.class)
                .addAdapter(new AmazonDynamoDBFactory())
                .as(CACHE).addAdapter(new DynamoDBMapperFactory())
                .as(CACHE).addComponent(KeysCache.class, InMemoryKeysCacheWithExpiration.class);
    }
}
