package utils;

import java.io.Serializable;
import java.util.Scanner;

/**
 * Class that contains the current mode of the server (interactive\script) and the current scanner for
 * reading command requests
 */
public class Mode implements Serializable {
    private boolean scriptMode;
    private Scanner s;

    public Mode() {
        s = new Scanner(System.in);
        scriptMode = false;
    }

    public boolean getScriptMode() {
        return scriptMode;
    }

    public void setScriptMode(boolean scriptMode) {
        this.scriptMode = scriptMode;
    }

    public Scanner getScanner() {
        return s;
    }

    public void setScanner(Scanner s) {
        this.s = s;
    }
}

