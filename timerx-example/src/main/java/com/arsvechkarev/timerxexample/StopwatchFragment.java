package com.arsvechkarev.timerxexample;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.arsvechkarev.timerx.Stopwatch;
import com.arsvechkarev.timerx.StopwatchListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class StopwatchFragment extends Fragment {

  private TextView textTimer;
  private Stopwatch stopwatch;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_stopwatch, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    textTimer = view.findViewById(com.arsvechkarev.timerxexample.R.id.text_timer);
    stopwatch = new Stopwatch(new StopwatchListener() {
      @Override
      public void onTimeTick(String time) {
        textTimer.setText(time);
      }
    }, "Format#Start: MM::SS::LL")/*
        .changeFormatWhen(5, TimeUnits.SECONDS, "Format5: SSSSSS")
        .changeFormatWhen(5, TimeUnits.SECONDS, "Format52: LA#LSSS")
        .changeFormatWhen(10, TimeUnits.SECONDS, "Format10: SS#L")*/;

    view.findViewById(
        com.arsvechkarev.timerxexample.R.id.btn_start)
        .setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            stopwatch.start();

//            new Handler().postDelayed(new Runnable() {
//              @Override
//              public void run() {
//                stopwatch.changeFormatWhen(15, TimeUnits.SECONDS, "Format15: SS -> LL#L");
//              }
//            }, 10000);
          }
        });

    view.findViewById(com.arsvechkarev.timerxexample.R.id.btn_stop)
        .setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            stopwatch.stop();
            Toast.makeText(getActivity(), "time = " + stopwatch.getTime(),
                Toast.LENGTH_SHORT).show();
          }
        });

    view.findViewById(com.arsvechkarev.timerxexample.R.id.btn_reset)
        .setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            stopwatch.reset();
            textTimer.setText("0");
          }
        });
  }
}
