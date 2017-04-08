package goda.tft.paulgof.mrbeatplayer;

public class UnicalRandom extends Rand{
    public int[] unical (int diapazon){
        Rand ran = new Rand();
        int[] randArray = new int[diapazon];
        for (int i = 0; i < diapazon; i++){
            randArray[i] = ran.randach(diapazon);

            for (int j = 0; j < i - 1; j++){
                if (randArray[j] == randArray[i]){
                    i--;
                    break;
                }
            }
        }
        return randArray;
    }
}
