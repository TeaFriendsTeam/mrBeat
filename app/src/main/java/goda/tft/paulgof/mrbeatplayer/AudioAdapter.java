package goda.tft.paulgof.mrbeatplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;


public class AudioAdapter extends ArrayAdapter<Audio> {
    public AudioAdapter(Context context, ArrayList<Audio> audios) {
        super(context, 0, audios);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
