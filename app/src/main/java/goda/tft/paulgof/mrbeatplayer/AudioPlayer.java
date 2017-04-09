package goda.tft.paulgof.mrbeatplayer;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class AudioPlayer {
    MediaPlayer mp;

    public void playAudio(final ArrayList<Audio> audioArrayList, final int position) {
        releaseMP();
        try {
            mp = new MediaPlayer();
            mp.setDataSource(audioArrayList.get(position).getData());
            mp.prepare();
            mp.start();
        } catch (IOException e) {}
    }

    public void audioRepeat (final ArrayList<Audio> audioArrayList, final int position){
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

    public void allAudioPlay (final ArrayList<Audio> audioArrayList, final int position){
       releaseMP();
        try {
            mp = new MediaPlayer();
            mp.setDataSource(audioArrayList.get(position).getData());
            mp.prepare();
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    int x = position;
                    x++;
                    if (x == audioArrayList.size()) x = 0;
                    allAudioPlay(audioArrayList, x);
                }
            });
        } catch (IOException e) {}
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
