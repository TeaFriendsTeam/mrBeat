package goda.tft.paulgof.mrbeatplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AudioListActivity extends AppCompatActivity {


    ArrayList<Audio> audioList;
    AudioPlayer audioPlayer = new AudioPlayer();
    private boolean isRandomed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        checkPermission();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);

        loadAudio();

        initAudioList();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }
    }

    private void loadAudio() {
        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            audioList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

                // Save to audioList
                audioList.add(new Audio(data, title, album, artist));
            }
        }
        cursor.close();
    }

    private void initAudioList() {
        if (audioList.size() > 0) {

            final AudioAdapter audioAdapter = new AudioAdapter(this, audioList);
            final ListView listView = (ListView) findViewById(R.id.audioList);
            listView.setAdapter(audioAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //mp.setAudioSessionId(audioList.get(position).getId());
                    audioPlayer.playAudio(audioList, position);
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public ArrayList<Audio> RandomAudio (ArrayList<Audio> randomAudio){
        ArrayList<Audio> bufList = new ArrayList<>();
        int[] randomArray;
        UniRandom rand = new UniRandom();
        randomArray = rand.unirand(randomAudio.size());
        for(int x : randomArray) {
            bufList.add(randomAudio.get(x));
        }
        return bufList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menuTitle:
                if (!isRandomed) {
                    audioList = RandomAudio(audioList);
                    initAudioList();
                    Toast toast = Toast.makeText(getApplicationContext(), "Rand on", Toast.LENGTH_SHORT);
                    toast.show();
                    isRandomed = true;
                    return true;
                } else {
                    loadAudio();
                    initAudioList();
                    Toast toast = Toast.makeText(getApplicationContext(), "Rand off", Toast.LENGTH_SHORT);
                    toast.show();
                    isRandomed = false;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
