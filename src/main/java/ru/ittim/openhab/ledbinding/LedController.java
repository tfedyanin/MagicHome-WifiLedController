package ru.ittim.openhab.ledbinding;

import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Бин, описывающий wifi-контроллер светодиодной ленты
 * Created by Timofey on 21.06.2016.
 */
class LedController {
    private final static int DEFAULT_CONTROLLER_PORT = 5577;
    private final static Logger logger = LogManager.getLogger();

    //сообщение контроллеру, на который приходит ответ длиной 14
    private final static byte[] MSG = {(byte) 0x81, (byte) 0x8a, (byte) 0x8b, (byte) 0x96};
    private static final int TIMEOUT = 1000;

    private final String host;
    private final String mac;
    private final String model;
    private ControllerType type;
    private PowerState power;
    private FunctionalModeRgb mode;
    private LedStripType strip;
    private ControllerChannels channels;

    private Socket socket;

    LedController(String host, String mac, String model, LedStripType strip) {
        this.host = host;
        this.mac = mac;
        this.model = model;
        this.type = ControllerType.UNKNOWN;
        this.power = PowerState.UNKNOWN;
        this.mode = FunctionalModeRgb.UNKNOWN;
        this.strip = strip;

        try {
            socket = new Socket(host, DEFAULT_CONTROLLER_PORT);
            socket.setSoTimeout(TIMEOUT);
        } catch (IOException e) {
            logger.error("Не удалось создать подключение к контроллеру " + this, DEFAULT_CONTROLLER_PORT);
        }
    }

    public LedController(String host, String mac, String model) {
        this(host, mac, model, LedStripType.UNKNOWN);
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

    public ControllerType getType() {
        return type;
    }

    public PowerState getPower() {
        return power;
    }

    public FunctionalModeRgb getMode() {
        return mode;
    }

    @Override
    public String toString() {
        return "LedController{" +
                "host='" + host + '\'' +
                ", mac='" + mac + '\'' +
                ", model='" + model + '\'' +
                ", type=" + type +
                ", power=" + power +
                ", mode=" + mode +
                ", strip=" + strip +
                ", channels=" + channels +
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

    /**
     * [a b c d e f j h i j k l m n]
     * 00 - a - always 0x81 (-0x7f)
     * 01 - b - always 0x25
     * 02 - c - on/off
     * 03 - d - mode
     * 04 - e - f(speed) [31 -1] -> [0 100]
     * 05 - f - 01
     * 06 - g - red [0-255]*brightness
     * 07 - h - green
     * 08 - i - blue
     * 09 - j - ww channel
     * 10 - k - 01
     * 11 - l - cw chanenl
     * 12 - m - f0 when RGB, RGBW, RGBWW and
     * 13 - n - ??
     *
     * @return
     */
    public boolean init() {
        try {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            DataOutputStream dos = new DataOutputStream(out);
            dos.write(MSG);
            dos.flush();
            byte[] bytes = new byte[14];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            while ((len = in.read(bytes)) > -1) {
                baos.write(bytes, 0, len);
                if (baos.size() >= 14) {
                    break;
                }
            }

            if (baos.size() != 14) {
                logger.error("Получен ответ некорректной длины на запрос состояний.");
                return false;
            }
            byte[] response = baos.toByteArray();
            if ((response[0] != -0x7f) || (response[1] != 0x25)) {
                logger.error("Получен ответ некорректной длины. Инициализация контроллера не удалась.");
                return false;
            }

            this.power = PowerState.get(response[2]);
            this.mode = FunctionalModeRgb.get(response[3]);
            //todo Соответствие реальной ленты состоянию контроллера
            this.type = ControllerType.get(response[12]);
            this.channels = new ControllerChannels(response[6], response[7], response[8], response[9], response[11]);
            System.out.println(Hex.encodeHexString(response));
            return true;


        } catch (IOException e) {
            logger.error("Сокет не операбелен", e);
            return false;
        }
    }

    public boolean setPowerState(PowerState state) {
        try {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            DataOutputStream dos = new DataOutputStream(out);
            byte[] command = state.getCommand();
            dos.write(command);
            dos.flush();
            byte[] bytes = new byte[command.length];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            while ((len = in.read(bytes)) > -1) {
                baos.write(bytes, 0, len);
                if (baos.size() == command.length) {
                    break;
                }
            }
            boolean b = Arrays.equals(baos.toByteArray(), command);
            // TODO: 24.06.2016 Дополнительная обработка и логирование
            return b;
        } catch (IOException e) {
            logger.error("Сокет не операбелен", e);
            return false;
        }
    }

    public boolean turnOn() {
        return setPowerState(PowerState.ON);
    }

    public boolean turnOff() {
        return setPowerState(PowerState.OFF);
    }


    public static void main(String[] args) throws InterruptedException {
        LedController controller = new LedController("192.168.1.181", "ACCF239939B4", "HF-LPB100-ZJ200", LedStripType.RGB);
        controller.init();
        System.out.println(controller);
        controller.turnOff();


    }
}
