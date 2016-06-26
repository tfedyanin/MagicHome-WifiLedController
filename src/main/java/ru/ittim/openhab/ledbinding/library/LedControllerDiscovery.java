package ru.ittim.openhab.ledbinding.library;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * Обнаружение wifi-контроллеров светодиодной ленты
 * Created by Timofey on 21.06.2016.
 */
public class LedControllerDiscovery {
    private static final int DISCOVERY_PORT = 48899;
    private static final String DISCOVERY_ADDRESS = "255.255.255.255";
    private static final int DISCOVERY_TIMEOUT_MILLIS = 1000;
    private static final String DISCOVERY_DATA = "HF-A11ASSISTHREAD";

    private static Logger logger = LogManager.getLogger();

    private DatagramSocket socket;

    private LedControllerDiscovery() throws SocketException {
        socket = new DatagramSocket();
        socket.setSoTimeout(DISCOVERY_TIMEOUT_MILLIS);
    }

    /**
     * Метод посылает широковещательную рассылку 255.255.255.255 на порт 48899 и слушает UDP ответы, в которых
     * содержится информация об обнаруженных устройствах
     * @return набор обнаруженных устройств (может быть пустым)
     * @throws IOException
     */
    private Set<LedController> discoveryLedControllers() throws IOException {
        if (sendDiscovery()) {
            return listenDiscovery();
        }
        logger.error("Не удалось отправить широковещательную расслку для обнаружения wifi-контроллеров. " +
                "Возможна ошибка в создании UDP-сокета");
        throw new IOException("Не удалось отправить широковещательную расслку для обнаружения wifi-контроллеров. " +
                "Возможна ошибка в создании UDP-сокета");
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
                logger.debug("Обнаружение завершено. Получено " + datagramPackets.size() + " ответов");
                break;
            } catch (IOException e) {
                logger.error("В ходе обнаружения возникла ошибка", e);
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
                logger.error("Пакет с адреса " + packet.getSocketAddress() + " содержит неизветный формат данных: " +
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
            logger.error("Что-то пошло не так....", e);
        }
    }
}
