package ru.ittim.openhab.ledbinding.library;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Discovery controllers in LAN
 * Created by Timofey on 21.06.2016.
 */
public class DiscoveryFinder implements ControllerFinder {
    /**
     * From reverse-engineering
     */
    private static final int DISCOVERY_PORT = 48899;
    /**
     * From reverse-engineering
     */
    private static final String DISCOVERY_DATA = "HF-A11ASSISTHREAD";

    private static Logger logger = LoggerFactory.getLogger(DiscoveryFinder.class);

    /**
     * Address for broadcast datagram. For example, for network 192.168.0.xxx (with netmask 255.255.255.0)
     * this address is 192.168.0.255
     */
    private final String discoveryAddress;

    /**
     * Timeout for wait response from controllers
     */
    private final int scanTimeout;

    /**
     * Number of discovery attempts
     */
    private final int attempts;


    private DiscoveryFinder(String discoveryAddress, int scanTimeout, int attempts) {
        this.discoveryAddress = discoveryAddress;
        this.scanTimeout = scanTimeout;
        this.attempts = attempts;
    }

    /**
     * Discover all devices in provided broadcast group and log info
     *
     * @param args broadcast address, if not provided - default value 192.168.1.255
     */
    public static void main(String[] args) {
        if (args.length!=1){
            logger.info("Provide discovery broadcast address as first argument. By default used 192.168.1.255");
        }
        DiscoveryFinder discovery = new DiscoveryFinderBuilder()
                .discoveryAddress(args.length != 1 ? args[0] : "192.168.1.255")
                .build();
        Set<LedController> controllers = discovery.getControllers();
        controllers.forEach(it->logger.info(it.toString()));
    }

    /**
     * This method broadcast to broadcast address and listen answers (with given timeout), which contains information about detected
     * devices. This process repeats for a given number of attempts
     *
     * @return set of detected devices
     */
    @Override
    public Set<LedController> getControllers() {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            socket.setSoTimeout(scanTimeout);
            final HashSet<LedController> result = new HashSet<>();
            for (int i = 0; i < attempts; i++) {
                logger.info("Discovery attempt {}", i);
                if (sendDiscovery(socket)) {
                    result.addAll(listenDiscovery(socket));
                } else {
                    String message = "Unable send broadcast. " +
                            "Maybe error in socket creation";
                    logger.error(message);
                    throw new RuntimeException(message);
                }
            }
            return result;
        } catch (SocketException e) {
            String message = "Unable send broadcast. " +
                    "Maybe error in socket creation";
            logger.error(message);
            throw new RuntimeException(message, e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Set<LedController> listenDiscovery(DatagramSocket socket) {
        ArrayList<DatagramPacket> datagramPackets = new ArrayList<>();
        logger.debug("Listening discovery responses for {}", discoveryAddress);
        while (true) {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                datagramPackets.add(packet);
                logger.debug("Received discovery answer #{}", datagramPackets.size());
            } catch (SocketTimeoutException e) {
                logger.info("Discovery is completed. Received {} answers", datagramPackets.size());
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
        logger.debug("Parsing {} response(s)", packets.size());
        for (DatagramPacket packet : packets) {
            String str = new String(packet.getData());
            String[] tokens = str.split(",");
            if (tokens.length != 3) {
                logger.error("Answer from {} has wrong payload: {}", packet.getSocketAddress(), str);
            } else {
                final LedController controller = new LedController(tokens[0], tokens[1], tokens[2]);
                logger.info("Found {}", str);
                controllers.add(controller);
            }
        }
        return controllers;
    }

    private boolean sendDiscovery(DatagramSocket socket) throws IOException {
        if (socket != null && socket.isBound()) {
            byte[] buf = DISCOVERY_DATA.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length,
                    new InetSocketAddress(discoveryAddress, DISCOVERY_PORT));
            logger.debug("Sending discovery message to {}", discoveryAddress);
            socket.send(packet);
            return true;
        }
        logger.debug("Datagram socket is null or not bound");
        return false;
    }

    public static final class DiscoveryFinderBuilder {
        /**
         * Address for broadcast datagram. For example, for network 192.168.0.xxx (with netmask 255.255.255.0)
         * this address is 192.168.0.255
         */
        private String discoveryAddress;

        /**
         * Timeout for wait response from controllers
         */
        private int scanTimeout = 2_000;

        /**
         * Number of discovery attempts
         */
        private int attempts = 2;

        public DiscoveryFinderBuilder() {
        }

        public static DiscoveryFinderBuilder aDiscoveryFinder() {
            return new DiscoveryFinderBuilder();
        }

        public DiscoveryFinderBuilder discoveryAddress(String discoveryAddress) {
            this.discoveryAddress = discoveryAddress;
            return this;
        }

        public DiscoveryFinderBuilder scanTimeout(int scanTimeout) {
            this.scanTimeout = scanTimeout;
            return this;
        }

        public DiscoveryFinderBuilder attempts(int attempts) {
            this.attempts = attempts;
            return this;
        }

        public DiscoveryFinder build() {
            if (discoveryAddress == null) {
                throw new IllegalStateException("Discovery address is mandatory field");
            }
            return new DiscoveryFinder(discoveryAddress, scanTimeout, attempts);
        }
    }
}
