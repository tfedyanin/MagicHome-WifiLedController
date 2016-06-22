package ru.ittim.openhab.ledbinding;

import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

/**
 * Бин, описывающий wifi-контроллер светодиодной ленты
 * Created by Timofey on 21.06.2016.
 */
class LedController {
    private final static int DEFAULT_CONTROLLER_PORT = 5577;
    private final static Logger logger = LogManager.getLogger();

    //сообщение контроллеру, на который приходит ответ длиной 14
    private final static byte[] MSG = {(byte) 0x81, (byte) 0x8a, (byte) 0x8b, (byte) 0x96};

    private final String host;
    private final String mac;
    private final String model;
    private static final int TIMEOUT = 150;

    private Socket socket;

    LedController(String host, String mac, String model) {
        this.host = host;
        this.mac = mac;
        this.model = model;

        try {
            socket = new Socket(host, DEFAULT_CONTROLLER_PORT);
            socket.setSoTimeout(TIMEOUT);
        } catch (IOException e) {
            logger.error("Не удалось создать подключение к контроллеру " + this, DEFAULT_CONTROLLER_PORT);
        }
    }

    public String getHost() {
        return host;
    }

    public String getMac() {
        return mac;
    }

    public String getModel() {
        return model;
    }

    @Override
    public String toString() {
        return "LedController{" +
                "host='" + host + '\'' +
                ", mac='" + mac + '\'' +
                ", model='" + model + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LedController that = (LedController) o;

        return host.equals(that.host) && mac.equals(that.mac) && model.equals(that.model);

    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + mac.hashCode();
        result = 31 * result + model.hashCode();
        return result;
    }

    public void initialRequest() {
        try {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            DataOutputStream dos = new DataOutputStream(out);
            dos.write(MSG);
            byte[] bytes = new byte[14];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            while ((len = in.read(bytes)) > -1) {
                    baos.write(bytes, 0, len);
                if (baos.size()>=14) {
                    break;
                }

            }

            if (baos.size() != 14) {
                logger.error("Получен ответ некорректной длины");
            }
            System.out.println(Hex.encodeHexString(baos.toByteArray()));


        } catch (IOException e) {
            logger.error("Сокет не операбелен", e);
        }
    }

    public static void main(String[] args) {
        LedController controller = new LedController("192.168.1.181", "ACCF239939B4", "HF-LPB100-ZJ200");
        controller.initialRequest();
    }
}
