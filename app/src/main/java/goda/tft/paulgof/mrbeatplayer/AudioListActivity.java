package goda.tft.paulgof.mrbeatplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class AudioListActivity extends FragmentActivity {


    ArrayList<Audio> audioList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        checkPermission();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);

        loadAudio();

        initAudioList();

        //ListView listView = (ListView) findViewById(R.id.audioList);
        //adapter = new SimpleCursorAdapter(this, R.layout.audio_info, null,
        //        new String[] { MediaStore.Audio.Media.TITLE },
        //        new int[] {
        //                R.id.textView }, 0);
        //listView.setAdapter(adapter);
        //getSupportLoaderManager().initLoader(0, null, this);
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
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

                // Save to audioList
                audioList.add(new Audio(data, title, album, artist));
            }
        }
        cursor.close();
    }

    private void initAudioList() {
        if (audioList.size() > 0) {

            AudioAdapter audioAdapter = new AudioAdapter(this, audioList);

            ListView listView = (ListView) findViewById(R.id.audioList);

            listView.setAdapter(audioAdapter);

            //RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
            //RecyclerView_Adapter adapter = new RecyclerView_Adapter(audioList, getApplication());
            //recyclerView.setAdapter(adapter);
            //recyclerView.setLayoutManager(new LinearLayoutManager(this));
            //recyclerView.addOnItemTouchListener(new CustomTouchListener(this, new onItemClickListener() {
            //    @Override
            //    public void onClick(View view, int index) {
            //        playAudio(index);
            //    }
            //}));

        }
    }

    //@Override
    //public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    //    return new CursorLoader(this,
    //            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    //            new String[] { MediaStore.Audio.Media._ID,
    //                    MediaStore.Audio.Media.TITLE },
    //            null, null,
    //            null);
    //}
    //@Override
    //public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    //    adapter.swapCursor(data);
    //}
    //@Override
    //public void onLoaderReset(Loader<Cursor> loader) {
    //    adapter.swapCursor(null);
    //}


}
