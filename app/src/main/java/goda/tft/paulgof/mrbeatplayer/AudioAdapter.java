package goda.tft.paulgof.mrbeatplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class AudioAdapter extends ArrayAdapter<Audio> {
    public AudioAdapter(Context context, ArrayList<Audio> audios) {
        super(context, 0, audios);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Audio audio = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.audio_info, parent, false);
        }

        TextView Title = (TextView) convertView.findViewById(R.id.titleText);
        TextView  Artist= (TextView) convertView.findViewById(R.id.artistText);

        Title.setText(audio.getTitle());
        Artist.setText(audio.getArtist());

        return convertView;
    }
}
