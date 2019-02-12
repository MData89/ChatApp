package dk.md89.chatapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity
{
    private ImageView splash_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState)                                              // when app starts it shows a splash screen with fade out from splashtransition.xml in anim directory.
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splash_logo = (ImageView) findViewById(R.id.splash_logo);

        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.splashtransition);
        splash_logo.startAnimation(myanim);

        final Intent i = new Intent(this, MainActivity.class);

        Thread timer = new Thread()
        {
            public void run()
            {
                try
                {
                    sleep(5000);
                }

                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                finally
                {
                    startActivity(i);
                    finish();
                }
            }
        };
                timer.start();
    }

}
