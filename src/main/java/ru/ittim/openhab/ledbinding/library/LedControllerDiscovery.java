package ru.ittim.openhab.ledbinding.library;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * Discovery controllers in LAN
 * Created by Timofey on 21.06.2016.
 */
public class LedControllerDiscovery {
    /**
     * From reverse-engineering
     */
    private static final int DISCOVERY_PORT = 48899;
    /**
     * From reverse-engineering
     */
    private static final String DISCOVERY_ADDRESS = "255.255.255.255";
    private static final int DISCOVERY_TIMEOUT_MILLIS = 1000;
    /**
     * From reverse-engineering
     */
    private static final String DISCOVERY_DATA = "HF-A11ASSISTHREAD";

    private static Logger logger = LogManager.getLogger();

    private DatagramSocket socket;

    private LedControllerDiscovery() throws SocketException {
        socket = new DatagramSocket();
        socket.setSoTimeout(DISCOVERY_TIMEOUT_MILLIS);
    }

    /**
     * This method broadcast to 255.255.255.255:48899 and listen answers, which contains information about detected
     * devices
     * @return set of detected devices
     * @throws IOException
     */
    private Set<LedController> discoveryLedControllers() throws IOException {
        if (sendDiscovery()) {
            return listenDiscovery();
        }
        String message = "Unable send broadcast. " +
                "Maybe error in socket creation";
        logger.error(message);
        throw new IOException(message);
    }

    private Set<LedController> listenDiscovery() {
        byte[] buf = new byte[256];
        ArrayList<DatagramPacket> datagramPackets = new ArrayList<>();
        while (true){
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                datagramPackets.add(packet);
            } catch (SocketTimeoutException e) {
                logger.debug("Discovery is completed. Received " + datagramPackets.size() + " answers");
                break;
            } catch (IOException e) {
                logger.error("Discovery is failed", e);
                break;
            }
        }
        return parsePackets(datagramPackets);
    }

    private Set<LedController> parsePackets(List<DatagramPacket> packets) {
        Set<LedController> controllers = new HashSet<>();
        for (DatagramPacket packet : packets) {
            String str = new String(packet.getData());
            String[] tokens = str.split(",");
            if (tokens.length != 3) {
                logger.error("Answer from " + packet.getSocketAddress() + " has wrong payload: " +
                str);
            } else {
                controllers.add(new LedController(tokens[0], tokens[1], tokens[2]));
            }
        }
        return controllers;
    }

    private boolean sendDiscovery() throws IOException {
        if (socket != null && socket.isBound()) {
            byte[] buf = DISCOVERY_DATA.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length,
                    new InetSocketAddress(DISCOVERY_ADDRESS, DISCOVERY_PORT));
            socket.send(packet);
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            LedControllerDiscovery discovery = new LedControllerDiscovery();
            Set<LedController> controllers = discovery.discoveryLedControllers();
            controllers.forEach(System.out::println);
        } catch (IOException e) {
            logger.error("Something is wrong....", e);
        }
    }
}
