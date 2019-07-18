package com.arsvechkarev.timerviewexample;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.arsvechkarev.timerview.Stopwatch;
import com.arsvechkarev.timerview.TimeTickListener;

public class MainActivity extends AppCompatActivity {

  private TextView textTimer;
  private Stopwatch stopwatch;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    textTimer = findViewById(R.id.text_timer);
    stopwatch = new Stopwatch(new TimeTickListener() {
      @Override
      public void onTimeTick(String time) {
        textTimer.setText(time);
      }
    }, "lala");

    findViewById(R.id.btn_start).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        stopwatch.start();
      }
    });

    findViewById(R.id.btn_stop).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        stopwatch.stop();
        Toast.makeText(MainActivity.this, "time = " + stopwatch.getTime(),
            Toast.LENGTH_SHORT).show();
      }
    });

    findViewById(R.id.btn_reset).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        stopwatch.reset();
        textTimer.setText("0");
      }
    });
  }
}
