package goda.tft.paulgof.mrbeatplayer;

import java.util.Random;

public class UniRandom {
    public int[] uniRand(int diapason){
        Rand ran = new Rand();
        int[] randArray = new int[diapason];
        for (int i = 0; i < diapason; i++){
            randArray[i] = ran.randach(diapason);

            for (int j = 0; j < i; j++){
                if (randArray[j] == randArray[i]){
                    i--;
                    break;
                }
            }
        }
        return randArray;
    }
}

class Rand {
    public static int randach(int maxVal) {
        Random rand = new Random();
        return rand.nextInt(maxVal) ;
    }
}
