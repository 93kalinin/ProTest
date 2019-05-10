package android.coursework.protest.Creation;

import java.io.Serializable;

public class IdMaker implements Serializable {

    public static String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHI"
            + "JKLMNOPQRSTUVWXYZ1234567890";
    private String lastReturnedId = "aaaa";

    private IdMaker() {}

    public String getNext() {
        while (true) {
            int index = 0;
            if (alphabet.charAt(index) = "0")
        }
    }
}
