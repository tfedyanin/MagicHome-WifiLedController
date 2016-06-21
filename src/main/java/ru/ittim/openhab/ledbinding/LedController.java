package ru.ittim.openhab.ledbinding;

/**
 * Бин, описывающий wifi-контроллер светодиодной ленты
 * Created by Timofey on 21.06.2016.
 */
class LedController {
    private final String host;
    private final String mac;
    private final String model;

    LedController(String host, String mac, String model) {
        this.host = host;
        this.mac = mac;
        this.model = model;
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
}
