package eus.ehu.bi.tta.ejemplo.pl.model;

import eus.ehu.bi.tta.ejemplo.bl.backend.Backend;
import eus.ehu.bi.tta.ejemplo.bl.backend.MockBackend;

public final class Locator {
    private static final UserModel userModel = new UserModel();
    //private static final Backend backend = new EhuBackend();
    private static final Backend backend = new MockBackend();

    public static UserModel getUserModel() {
        return userModel;
    }

    public static Backend getBackend() {
        return backend;
    }
}
