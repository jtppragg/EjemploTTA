package eus.ehu.tta.ejemplo.model;

import eus.ehu.tta.ejemplo.model.backend.Backend;
import eus.ehu.tta.ejemplo.model.backend.FirebaseBackend;

public final class Locator {
    //private static final Backend backend = new EhuBackend();
    //private static final Backend backend = new MockBackend();
    private static final Backend backend = new FirebaseBackend();

    public static Backend getBackend() {
        return backend;
    }
}
