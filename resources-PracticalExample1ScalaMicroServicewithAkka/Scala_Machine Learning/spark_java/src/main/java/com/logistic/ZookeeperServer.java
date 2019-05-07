package com.logistic;

import org.apache.commons.io.FileUtils;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

/**
 *
 * Simple ZK for local Kafka Broker
 */
public class ZookeeperServer {

    private static final Random RANDOM = new Random();

    private static final String projectRootPath = System.getProperty("user.dir");



    public static void main(String[] args) {


        Properties    startupProperties = new Properties();
        startupProperties.put("clientPort", 2181);
        startupProperties.put("dataDir", "embeeded-zk");
        startupProperties.put("ticktime", 500);
        try {
            File file = new File("embeeded-zk") ;
            if(FileUtils.getFile(file).exists()) {
                System.out.println("delete  zooKeeperServer output directory");
                FileUtils.forceDelete(file); // delete  zooKeeperServer output directory
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        constructTempDir("embeeded-zk");


        QuorumPeerConfig quorumConfiguration = new QuorumPeerConfig();

        try {
            quorumConfiguration.parseProperties(startupProperties);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        ZooKeeperServerMain zooKeeperServer = new ZooKeeperServerMain();
        final ServerConfig configuration = new ServerConfig();
        configuration.readFrom(quorumConfiguration);
        new Thread() {
            public void run() {
                try {
                    zooKeeperServer.runFromConfig(configuration);
                } catch (IOException e) {
                    System.out.println("ZooKeeper Failed "+ e);
                }
            }
        }.start();

    }



    public static File constructTempDir(String dirPrefix) {
        File file = new File(projectRootPath, dirPrefix);
        if (!file.mkdirs()) {
            throw new RuntimeException("could not create temp directory: " + file.getAbsolutePath());
        }
        file.deleteOnExit();
        return file;
    }




}
