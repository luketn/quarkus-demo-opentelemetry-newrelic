package com.example;

import com.mongodb.client.MongoClient;
import io.micrometer.core.annotation.Counted;
import org.bson.Document;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/sales")
public class ExampleResource {

    @Inject
    MongoClient mongoClient;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Counted(value = "sales", description = "How many sales have been made.")
    public SalesWrapper sales() {
        ArrayList<Sales> documents = mongoClient
                .getDatabase("mongodbVSCodePlaygroundDB")
                .getCollection("sales", Sales.class)
                .find()
                .into(new ArrayList<>());
        return new SalesWrapper(documents);
    }


}