package com.unsekhable.netty.handler;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
public class SensorDataHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;

    public SensorDataHandler() {
        this.mongoClient = MongoClients.create("mongodb://localhost:27017");
        this.database = mongoClient.getDatabase("sensordb");
        this.collection = database.getCollection("sensor_collection");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        // Assuming the sensor data is received as a ByteBuf
        byte[] data = new byte[msg.readableBytes()];
        msg.readBytes(data);
        String sensorData = new String(data);

        // Store data in MongoDB
        Document document = new Document("data", sensorData);
        collection.insertOne(document);

        // Respond back if needed
        ctx.writeAndFlush("Data received and stored in MongoDB\n");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
