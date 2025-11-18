package com.wavesplatform;


import com.wavesplatform.network.client.NetworkSender;
import com.wavesplatform.network.TrafficLogger;

import io.netty.channel.Channel;
import scala.collection.JavaConverters;
import scala.concurrent.Await;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class TxSender {

    public static byte[] getOnlyUsed(ByteBuffer buf) {
        byte[] bytes = new byte[buf.position()];
        buf.position(0);
        buf.get(bytes);
        return bytes;
    }

    public static void send(byte[] txBytes, InetSocketAddress node, char chainId) {
        List<Object> emptyList = new ArrayList<>();
        scala.collection.immutable.Set<Object> emptyObj = JavaConverters.asScalaIteratorConverter(emptyList.iterator()).asScala().toSet();

        final NetworkSender client = new NetworkSender(new TrafficLogger.Settings(emptyObj, emptyObj), chainId, "test-client", 0L, ExecutionContext.global());

        byte msgCode = 31;

        List<Object> rawBytesList = new ArrayList<>();
        RawBytes rb = new RawBytes(msgCode, txBytes);
        rawBytesList.add(rb);
        scala.collection.immutable.Seq<Object> message = JavaConverters.asScalaIteratorConverter(rawBytesList.iterator()).asScala().toSeq();

        try {
            Await.result(client.connect(node).flatMap(
                            channel -> client.send(channel, message),
                            ExecutionContext.global()),
                    Duration.Inf());
            client.close();
        } catch (InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public static void send(Transaction tx, InetSocketAddress node, char chainId) {
        send(tx.toBytes(), node, chainId);
    }

    public static void sendAndWait(byte[] txBytes, InetSocketAddress node, char chainId) {
        List<Object> emptyList = new ArrayList<>();
        scala.collection.immutable.Set<Object> emptyObj = JavaConverters.asScalaIteratorConverter(emptyList.iterator()).asScala().toSet();

        final com.wavesplatform.network.client.NetworkClient client = new NetworkClient(new TrafficLogger.Settings(emptyObj, emptyObj), chainId, "wavesD", 0L, ExecutionContext.global());

        byte msgCode = 31;

        List<Object> rawBytesList = new ArrayList<>();
        RawBytes rb = new RawBytes(msgCode, txBytes);
        rawBytesList.add(rb);
        scala.collection.immutable.Seq<Object> message = JavaConverters.asScalaIteratorConverter(rawBytesList.iterator()).asScala().toSeq();

        try {
            Future<Channel> test = client.connect(node);

            test.map(channel -> {
                client.send(channel, message);
                return channel;
            }, ExecutionContext.global());


            while (!test.isCompleted()) {
                Thread.sleep(100);
            }
            client.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
