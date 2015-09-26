// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimbus.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Denis on 22-Apr-15
 * <File Description>
 */
public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);
        Button sayHelloButton = (Button)findViewById(R.id.btn_say_hello);
        sayHelloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((App)getApplication()).bus().publish(new NotificationEvent("Hello, World"));
            }
        });
    }
}
