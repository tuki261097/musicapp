package com.example.asus.lastdemo;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import static com.example.asus.lastdemo.MusicPlayer.PLAYER_PLAY;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener, MusicPlayer.OnCompletionListener {

    private TextView tvTitle, tvSinger, tvTimeUpdate, tvTimeTotal;
    private ImageView ivShuffle, ivPrevious, ivPlay, ivNext, ivRepeat;
    private ListView lvPlayList;
    private SeekBar sbTime;
    private int timeUpdate, timeTotal, timeCurrent;
    private ArrayList<String> paths; // lưu tất cả đường dẫn của các bài hát
    private SongAdapter adapter;
    private MusicPlayer musicPlayer;
    private boolean isRunning, isRepeat, isShuffle;
    private int UPDATE_TIME = 1;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Anhxa();
        setList();
        eventClick();
    }

    private void setList() {
        addList();
        adapter = new SongAdapter(App.getContext(), paths);
        lvPlayList.setAdapter(adapter);
        musicPlayer = new MusicPlayer();
        musicPlayer.setOnCompletionListener(this);
    }

    private void Anhxa() {
        lvPlayList = (ListView) findViewById(R.id.listviewmusic);
        tvTitle = (TextView) findViewById(R.id.textviewtitle);
        tvSinger = (TextView) findViewById(R.id.textviewsinger);
        tvTimeUpdate = (TextView) findViewById(R.id.textviewtimeupdate);
        sbTime = (SeekBar) findViewById(R.id.seekbarsong);
        tvTimeTotal = (TextView) findViewById(R.id.textviewtimetotal);
        ivShuffle = (ImageView) findViewById(R.id.iconshuffle);
        ivPrevious = (ImageView) findViewById(R.id.iconprevious);
        ivPlay = (ImageView) findViewById(R.id.iconplay);
        ivNext = (ImageView) findViewById(R.id.iconnext);
        ivRepeat = (ImageView) findViewById(R.id.iconrepeat);
    }

    private void eventClick() {
        lvPlayList.setOnItemClickListener(this);
        ivShuffle.setOnClickListener(this);
        ivPrevious.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivRepeat.setOnClickListener(this);
        sbTime.setOnSeekBarChangeListener(this);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATE_TIME) {
                timeCurrent = musicPlayer.getTimeCurrent();
                tvTimeUpdate.setText(getTimeFormat(timeCurrent));
                sbTime.setProgress(timeCurrent);
            }
        }
    };

    private void addList() {
        paths = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MusicTest";
        File file = new File(path);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            String s = files[i].getName();
            if (s.endsWith(".mp3")) {
                paths.add(files[i].getAbsolutePath());
            }
        }
    }

    private void playMusic(String path) {
        if (musicPlayer.getState() == PLAYER_PLAY) {
            musicPlayer.stop();
        }
        musicPlayer.setup(path);
        musicPlayer.play();
        ivPlay.setImageResource(R.drawable.pause);

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(paths.get(position));
        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        tvSinger.setText(artist);
        tvTitle.setText(title);
        isRunning = true;

        tvTimeTotal.setText(getTimeFormat(musicPlayer.getTimeTotal()));

        sbTime.setMax(musicPlayer.getTimeTotal());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    Message message = new Message();
                    message.what = UPDATE_TIME;
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }).start();
    }

    private String getTimeFormat(long time) {
        String tm = "";
        int s;
        int m;
        int h;
        //giây
        s = (int) (time % 60);
        m = (int) ((time - s) / 60);
        if (m >= 60) {
            h = m / 60;
            m = m % 60;
            if (h > 0) {
                if (h < 10)
                    tm += "0" + h + ":";
                else
                    tm += h + ":";
            }
        }
        if (m < 10)
            tm += "0" + m + ":";
        else
            tm += m + ":";
        if (s < 10)
            tm += "0" + s;
        else
            tm += s + "";
        return tm;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iconnext:
                nextMusic();
                break;

            case R.id.iconplay:
                if (musicPlayer.getState() == PLAYER_PLAY) {
                    ivPlay.setImageResource(R.drawable.play);
                    musicPlayer.pause();
                    Toast.makeText(this, "Đang dừng", Toast.LENGTH_SHORT).show();
                } else {
                    ivPlay.setImageResource(R.drawable.pause);
                    musicPlayer.play();
                    Toast.makeText(this, "Bài hát đang phát", Toast.LENGTH_SHORT).show();
                    //Singleton.getInstance(MainActivity.this);

                }
                break;

            case R.id.iconprevious:
                previousMusic();
                break;

            case R.id.iconrepeat:
                repeatMusic();
                break;

            default:
                break;
        }
    }

    private void repeatMusic() {
        if(isRepeat = false){
            ivRepeat.setImageResource(R.drawable.repeat_white);
        }
        else {
            ivRepeat.setImageResource(R.drawable.repeat);
        }
    }

    private void previousMusic() {
        position--;
        if (position < 0) {
            position = paths.size() - 1;
        }
        String path = paths.get(position);
        playMusic(path);
    }

    private void nextMusic() {
        position++;
        if (position >= paths.size()) {
            position = 0;
        }
        String path = paths.get(position);
        playMusic(path);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.position = position;
        String path = paths.get(position);
        playMusic(path);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (timeCurrent != progress && timeCurrent != 0)
            musicPlayer.seek(sbTime.getProgress() * 1000);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void OnEndMusic() {
        nextMusic();
        Log.d("chanh", "vào đây");
    }
}
