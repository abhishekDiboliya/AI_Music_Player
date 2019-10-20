package com.developerabhi123.aimusicplayer;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;



/* created by Abhishek Diboliya */

import java.io.File;
import java.util.ArrayList;

public class SongListActivity extends AppCompatActivity {

    private String[] itemsall;
    private ListView msongsList;
    private ArrayAdapter<String> arrayAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);



        msongsList = findViewById(R.id.songsList);

        appExternalStoragePermission();
    }


    public void appExternalStoragePermission()
    {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response)
                    {
                        displayAudioSongsName();

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



    public ArrayList<File> readAudioFilis(File file)
    {
        ArrayList<File> arrayList = new ArrayList<>();

        File[] allfiles = file.listFiles();

        for(File indiVidualFile : allfiles)
        {
            if(indiVidualFile.isDirectory() && !indiVidualFile.isHidden())
            {
            arrayList.addAll(readAudioFilis(indiVidualFile));
            }
            else
                {
                    if(indiVidualFile.getName().endsWith(".mp3") || indiVidualFile.getName().endsWith(".aac") || indiVidualFile.getName().endsWith(".wav") || indiVidualFile.getName().endsWith(".wma"))
                    {
                        arrayList.add(indiVidualFile);
                    }
                }
        }

        return arrayList;
    }



    private void displayAudioSongsName()
    {
        final ArrayList<File> audioSongs = readAudioFilis(Environment.getExternalStorageDirectory());
        itemsall = new String[audioSongs.size()];


        for(int songsCounter=0; songsCounter<audioSongs.size(); songsCounter++)
        {
            itemsall[songsCounter] = audioSongs.get(songsCounter).getName();
        }

        arrayAdapter = new ArrayAdapter<String>(SongListActivity.this,android.R.layout.simple_list_item_1, itemsall);
        msongsList.setAdapter(arrayAdapter);

        msongsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
             String songNames = msongsList.getItemAtPosition(position).toString();

             Intent intent =new Intent(SongListActivity.this, MainActivity.class);
             Animatoo.animateFade(SongListActivity.this);
             intent.putExtra("song",audioSongs);
             intent.putExtra("name",songNames);
             intent.putExtra("position",position);
             startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu,menu);
        MenuItem item = menu.findItem(R.id.search_view);
        SearchView searchView = (SearchView)item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);

                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }
}
