package com.slimgears.slimbus.example;

import android.content.Context;
import android.widget.Toast;

import com.slimgears.slimbus.Subscribe;

/**
 * Created by Denis on 25/09/2015.
 *
 */
public class ToasterNotifier {
    private final Context context;

    public ToasterNotifier(Context context) {
        this.context = context;
    }

    @Subscribe
    public void onNotification(NotificationEvent event) {
        Toast.makeText(context, event.text, Toast.LENGTH_LONG).show();
    }
}
