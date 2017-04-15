package goda.tft.paulgof.mrbeatplayer;

import android.media.MediaPlayer;
import goda.tft.paulgof.nsd.*;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class AudioPlayer {

    MediaPlayer mp;
    private int positionFlag = -1;
    private boolean pauseFlag = false;

    public void playAudio(final ArrayList<Audio> audioArrayList, final int position) {
        if(position == positionFlag) {
            pauseTool();
        } else {
            setPositionFlag(position);
            pauseFlag = false;
            releaseMP();
            try {
                mp = new MediaPlayer();
                mp.setDataSource(audioArrayList.get(position).getData());
                mp.prepare();
                mp.start();
            } catch (IOException e) {
            }
        }
    }

    public void AudioRepeat (final ArrayList<Audio> audioArrayList, final int position){
        releaseMP();
        try {
            mp = new MediaPlayer();
            mp.setDataSource(audioArrayList.get(position).getData());
            mp.prepare();
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mp.start();
                }
            });
        } catch (IOException e) {}
    }

    public void pauseTool() {
        if(pauseFlag) {
            mp.start();
            setPauseFlag(false);
        } else {
            mp.pause();
            setPauseFlag(true);
        }
    }

    public void setPauseFlag(boolean pauseFlag) {
        this.pauseFlag = pauseFlag;
    }

    public void setPositionFlag(int positionFlag) {
        this.positionFlag = positionFlag;
    }


    private void releaseMP() {
        if (mp != null) {
            try {
                mp.release();
                mp = null;
            } catch (Exception e) {}
        }
    }
}
