package ru.ittim.openhab.ledbinding.library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaticIpFinder {
    private final List<String> addresses ;

    public StaticIpFinder(List<String> addresses) {
        this.addresses = addresses;
    }


    public static final class StaticIpFinderBuilder {
        private List<String> addresses = new ArrayList<>();

        public StaticIpFinderBuilder addAll(List<String> addresses) {
            this.addresses.addAll(addresses);
            return this;
        }

        public StaticIpFinderBuilder addAll(String... addresses) {
            this.addresses.addAll(Arrays.asList(addresses));
            return this;
        }

        public StaticIpFinderBuilder add(String address) {
            this.addresses.add(address);
            return this;
        }

        public StaticIpFinder build() {
            return new StaticIpFinder(addresses);
        }

    }

}
