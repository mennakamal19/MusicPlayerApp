package com.example.musicplay;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private String[] itemsAll;
    private ListView songList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        songList = findViewById(R.id.song_list);
        appExternalStoragePermission();
    }
    public void appExternalStoragePermission()
    {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response)
                    {
                        dispalyAudioSongsName();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response)
                    {

                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token)
                    {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> readOnlyAudioSongs(File file)
    {
        ArrayList<File>arrayList = new ArrayList<>();
        File[] allFills = file.listFiles();
        for(File individualFile : allFills)
        {
            if(individualFile.isDirectory() && !individualFile.isHidden())
            {
                arrayList.addAll(readOnlyAudioSongs(individualFile));
            }
            else
            {
                if (individualFile.getName().endsWith(".mp3")||individualFile.getName().endsWith(".aac")||individualFile.getName().endsWith(".wav")||individualFile.getName().endsWith(".wma"))
                {
                    arrayList.add(individualFile);
                }
            }
        }
        return arrayList;
    }

    private  void dispalyAudioSongsName()
    {
        final ArrayList<File>audioSongs = readOnlyAudioSongs(Environment.getExternalStorageDirectory());
        itemsAll = new String[audioSongs.size()];
        for(int songcounter = 0 ; songcounter < audioSongs.size();songcounter++)
        {
            itemsAll[songcounter] = audioSongs.get(songcounter).getName();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,itemsAll);
        songList.setAdapter(arrayAdapter);
        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String songname = songList.getItemAtPosition(position).toString();
                Intent intent = new Intent(MainActivity.this,SmartPlayerActivity.class);
                intent.putExtra("song",audioSongs);// pass el song nafsha
                intent.putExtra("songname",songname);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
    }
}