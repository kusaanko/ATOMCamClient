package com.github.kusaanko.atomcamclient.api.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertiesManager {
    private static PropertiesManager manager;

    private Properties propertiesSV;
    private Properties propertiesURL;
    private Properties propertiesADDR;

    public PropertiesManager() {
        this.propertiesSV = new Properties();
        this.propertiesURL = new Properties();
        this.propertiesADDR = new Properties();
        try {
            if (this.getClass().getResource("/atom/SV") != null) {
                this.propertiesSV.load(this.getClass().getResourceAsStream("/atom/SV"));
            } else {
                this.propertiesSV.load(Files.newInputStream(Paths.get("HL_SV")));
            }
            if (this.getClass().getResource("/atom/URL") != null) {
                this.propertiesURL.load(this.getClass().getResourceAsStream("/atom/URL"));
            } else {
                this.propertiesURL.load(Files.newInputStream(Paths.get("HL_URL")));
            }
            if (this.getClass().getResource("/atom/ADDR") != null) {
                this.propertiesADDR.load(this.getClass().getResourceAsStream("/atom/ADDR"));
            } else {
                this.propertiesADDR.load(Files.newInputStream(Paths.get("HL_ADDR")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Properties getSV() {
        return this.propertiesSV;
    }

    public Properties getURL() {
        return this.propertiesURL;
    }

    public Properties getADDR() {
        return this.propertiesADDR;
    }

    public static PropertiesManager getInstance() {
        if (manager == null) {
            manager = new PropertiesManager();
        }
        return manager;
    }
}
