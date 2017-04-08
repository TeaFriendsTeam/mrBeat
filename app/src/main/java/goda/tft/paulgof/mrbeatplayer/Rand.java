package goda.tft.paulgof.mrbeatplayer;

import java.util.Random;

public class Rand {
    public static int randach(int maxVal) {
        Random rand = new Random();
        return rand.nextInt(maxVal) ;
    }
}
