package com.arsvechkarev.timerxexample;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.arsvechkarev.timerx.Stopwatch;
import com.arsvechkarev.timerx.TimeTickListener;
import com.arsvechkarev.timerx.TimeUnits;

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
    }, "Format#Start: MM::SS::")
        .changeFormatWhen(10, TimeUnits.SECONDS, "Format10: MM:SS")
        .changeFormatWhen(5, TimeUnits.SECONDS, "Format5: SSSSSS")
        .changeFormatWhen(15, TimeUnits.SECONDS, "Format15: SS#L");

    findViewById(R.id.btn_start).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        stopwatch.start();

//        new Handler().postDelayed(new Runnable() {
//          @Override
//          public void run() {
//            stopwatch.changeFormatWhen(20, TimeUnits.SECONDS, "Format20: SS -> LL#L");
//          }
//        }, 19000);
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
