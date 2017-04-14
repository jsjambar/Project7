package com.example.jase.bertapp.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Collections;

public class MlabDatabase {

    private MongoClient mongo;
    private MongoDatabase db;
    private MongoCollection<Document> sightCollection;

    private final String host = "ds159880.mlab.com";
    private final int port = 59880;
    private final String databaseName = "project7";
    private final String username = "admin";
    private final String password = "bert";

    private static MlabDatabase instance;

    private MlabDatabase() {
        this.mongo = new MongoClient(new ServerAddress(host, port), Collections.singletonList(MongoCredential.createCredential(username, databaseName, password.toCharArray())));
        this.db = mongo.getDatabase(databaseName);
        this.sightCollection = db.getCollection("sights");
    }

    public MongoClient getMongo() {
        return mongo;
    }

    public MongoDatabase getDatabase() {
        return db;
    }

    public MongoCollection<Document> getSightCollection() {
        return sightCollection;
    }

    public static MlabDatabase getInstance() {
        if (instance == null)
            instance = new MlabDatabase();
        return instance;
    }

    //mongo ds159880.mlab.com:59880/project7 -u admin -p bert

}
