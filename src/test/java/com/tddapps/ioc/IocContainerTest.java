package com.tddapps.ioc;

import com.tddapps.actions.HeartBeatPostAction;
import com.tddapps.actions.NotificationCalculatorAction;
import com.tddapps.actions.StatusGetAction;
import com.tddapps.dal.DynamoDBMapperFactory;
import com.tddapps.dal.DynamoDBMapperFactoryWithTablePrefix;
import com.tddapps.dal.HeartBeatRepository;
import com.tddapps.dal.HeartBeatRepositoryDynamo;
import com.tddapps.infrastructure.InMemoryKeysCacheWithExpiration;
import com.tddapps.infrastructure.KeysCache;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IocContainerTest {
    @Test
    public void DependenciesAreNotSingletonByDefault(){
        HeartBeatRepository repository1 = IocContainer.getInstance().Resolve(HeartBeatRepository.class);
        HeartBeatRepository repository2 = IocContainer.getInstance().Resolve(HeartBeatRepository.class);

        assertFalse(repository1 == repository2);
    }

    @Test
    public void RegisterDependencies(){
        assertTrue(IocContainer.getInstance().Resolve(HeartBeatRepository.class) instanceof HeartBeatRepositoryDynamo);
    }

    @Test
    public void RegistersDynamoDBMapperFactoryAsASingleton(){
        assertTrue(IocContainer.getInstance().Resolve(DynamoDBMapperFactory.class) instanceof DynamoDBMapperFactoryWithTablePrefix);

        DynamoDBMapperFactory factory1 = IocContainer.getInstance().Resolve(DynamoDBMapperFactory.class);
        DynamoDBMapperFactory factory2 = IocContainer.getInstance().Resolve(DynamoDBMapperFactory.class);

        assertTrue(factory1 == factory2);
    }

    @Test
    public void RegistersInMemoryKeysCacheWithExpirationAsASingleton(){
        assertTrue(IocContainer.getInstance().Resolve(KeysCache.class) instanceof InMemoryKeysCacheWithExpiration);

        KeysCache cache1 = IocContainer.getInstance().Resolve(KeysCache.class);
        KeysCache cache2 = IocContainer.getInstance().Resolve(KeysCache.class);

        assertTrue(cache1 == cache2);
    }

    @Test
    public void RegistersActions(){
        assertNotNull(IocContainer.getInstance().Resolve(HeartBeatPostAction.class));
        assertNotNull(IocContainer.getInstance().Resolve(StatusGetAction.class));
        assertNotNull(IocContainer.getInstance().Resolve(NotificationCalculatorAction.class));
    }
}
