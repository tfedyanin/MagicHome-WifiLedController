package ru.ittim.openhab.ledbinding.library;

import java.util.Set;

public interface ControllerFinder {
    Set<LedController> getControllers();
}
