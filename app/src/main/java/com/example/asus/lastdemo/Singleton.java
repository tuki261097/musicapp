package com.example.asus.lastdemo;

import android.content.Context;
import android.widget.Toast;

public class Singleton {
    private  static Singleton singleton;
    private Context context;

    private Singleton(Context context) {
        this.context =  context;
    }

    public void toast(){
        Toast.makeText(context, "Bài hát đang phát", Toast.LENGTH_SHORT).show();
    }

    public static Singleton getInstance(Context context) {
        if (singleton == null) {
            singleton = new Singleton(context);
        }
        return singleton;
    }
}
