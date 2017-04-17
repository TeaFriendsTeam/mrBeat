package goda.tft.paulgof.mrbeatplayer;

import java.util.ArrayList;
import java.util.Random;

/**
 * UniRandom нужен для присваивания элементам списка
 * рандомной уникальной позиции. "Смешивает список".
 */



public class UniRandom {

    public ArrayList<Audio> randomAudio (ArrayList<Audio> randomAudio) {
        ArrayList<Audio> bufList = new ArrayList<>();
        int[] randomArray;
        UniRandom rand = new UniRandom();
        randomArray = rand.uniRand(randomAudio.size());
        for(int x : randomArray) {
            bufList.add(randomAudio.get(x));
        }
        return bufList;
    }

    private int[] uniRand(int diapason){
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
