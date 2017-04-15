package goda.tft.paulgof.mrbeatplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import android.os.Handler;

import goda.tft.paulgof.nsd.*;

public class AudioListActivity extends AppCompatActivity {


    ArrayList<Audio> audioList;
    AudioPlayer audioPlayer = new AudioPlayer();
    //MediaPlayer mp;

    private boolean isRandomised = false;
    boolean isRegistration = false;


    NsdHelper nsdHelper;
    Handler updateHandler;

    public static final String TAG = "MrBeatPlayer";

    AudioStream audioStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) { // start

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        checkPermission();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);

        updateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String chatLine = msg.getData().getString("msg");
                addChatLine(chatLine);
            }
        };

        loadAudio();

        initAudioList();

        audioStream = new AudioStream(updateHandler);

        nsdHelper = new NsdHelper(this);
        nsdHelper.initializeNsd();

    }

    private void checkPermission() { // get permission from device
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }
    }

    private void loadAudio() { // search and add all .mp3 audio on device
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

    private void initAudioList() { // make audioAdapter and connection to audioList
        if (audioList.size() > 0) {

            final AudioAdapter audioAdapter = new AudioAdapter(this, audioList);
            final ListView listView = (ListView) findViewById(R.id.audioList);
            listView.setAdapter(audioAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (isRegistration) {

                        String path = audioList.get(position).getTitle();
                        Toast toast = Toast.makeText(getApplicationContext(), path, Toast.LENGTH_SHORT);
                        toast.show();
                        if (!path.isEmpty()) {
                            audioStream.sendMessage(path);
                        }

                    } else {
                        audioPlayer.playAudio(audioList, position);
                    }

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // make menu tool
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // setOnClickListener for menu tools
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.randItem: // act when click on randItem
                if (!isRandomised) { // check: Was List randomised?
                    audioList = RandomAudio(audioList);
                    initAudioList();
                    Toast toast = Toast.makeText(getApplicationContext(), "Rand on", Toast.LENGTH_SHORT);
                    toast.show();
                    isRandomised = true;
                    return true;
                } else {
                    loadAudio();
                    initAudioList();
                    Toast toast = Toast.makeText(getApplicationContext(), "Rand off", Toast.LENGTH_SHORT);
                    toast.show();
                    isRandomised = false;
                }
                break;
            case R.id.registration:
                if(audioStream.getLocalPort() > -1) {
                    nsdHelper.registerService(audioStream.getLocalPort());
                    isRegistration = true;
                } else {
                    Log.d(TAG, "ServerSocket isn't bound.");
                }
                break;
            case R.id.connecting:
                NsdServiceInfo service = nsdHelper.getChosenServiceInfo();
                if (service != null) {
                    Log.d(TAG, "Connecting.");
                    audioStream.connectToServer(service.getHost(),
                            service.getPort());
                } else {
                    Log.d(TAG, "No service to connect to!");
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public ArrayList<Audio> RandomAudio (ArrayList<Audio> randomAudio) {
        ArrayList<Audio> bufList = new ArrayList<>();
        int[] randomArray;
        UniRandom rand = new UniRandom();
        randomArray = rand.uniRand(randomAudio.size());
        for(int x : randomArray) {
            bufList.add(randomAudio.get(x));
        }
        return bufList;
    }


    public void addChatLine(String line) { //
        Toast toast = Toast.makeText(getApplicationContext(), line, Toast.LENGTH_SHORT);
        toast.show();
        for(int x = 0, n = audioList.size(); x < n; x++) {
            if(audioList.get(x).getTitle().equals(line)) {
                audioPlayer.playAudio(audioList, x);
            }
        }
    }

    @Override
    protected void onPause() {
        if (nsdHelper != null) {
            nsdHelper.stopDiscovery();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nsdHelper != null) {
            nsdHelper.discoverServices();
        }
    }

    @Override
    protected void onDestroy() {
        nsdHelper.tearDown();
        //audioConnection.tearDown();
        super.onDestroy();
    }

}
