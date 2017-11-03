package com.niewj.common.util.mongodb;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MongoDbHelper {

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * @param data
     * @param collectionName
     */
    public void save(Object data, String collectionName) {
        mongoTemplate.insert(data, collectionName);
    }


}
