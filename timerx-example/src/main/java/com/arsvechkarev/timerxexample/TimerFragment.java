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
import java.util.concurrent.TimeUnit;
import timerx.common.Action;
import timerx.common.TimeTickListener;
import timerx.timer.TimerBuilder;
import timerx.timer.TimerImpl;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends Fragment {

  private TextView textTime;
  private TimerImpl timer;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_timer, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    textTime = view.findViewById(com.arsvechkarev.timerxexample.R.id.text_time);
    timer = new TimerBuilder()
        .startFormat("20: MM:SSSSSS")
        .startTime(20, TimeUnit.SECONDS)
        .actionWhen(15, TimeUnit.SECONDS, new Action() {
          @Override
          public void run() {
            Toast.makeText(getContext(),
                "15s: " + timer.getRemainingTimeIn(TimeUnit.SECONDS), Toast.LENGTH_SHORT)
                .show();
          }
        })
        .actionWhen(10, TimeUnit.SECONDS, new Action() {
          @Override
          public void run() {
            Toast.makeText(getContext(),
                "10s: " + timer.getRemainingTimeIn(TimeUnit.SECONDS), Toast.LENGTH_SHORT)
                .show();
          }
        })
        .actionWhen(5, TimeUnit.SECONDS, new Action() {
          @Override
          public void run() {
            Toast.makeText(getContext(),
                "5s: " + timer.getRemainingTimeIn(TimeUnit.SECONDS), Toast.LENGTH_SHORT)
                .show();
          }
        })
        .changeFormatWhen(15, TimeUnit.SECONDS, "15: MM:SS")
        .changeFormatWhen(10, TimeUnit.SECONDS, "10: SS:LLLLL")
        .changeFormatWhen(5, TimeUnit.SECONDS, "5: SS:LLLLL")
        .onTick(new TimeTickListener() {
          @Override
          public void onTick(String time) {
            textTime.setText(time);
          }
        })
        .build();

    textTime.setText(timer.getFormattedStartTime());

    view.findViewById(
        com.arsvechkarev.timerxexample.R.id.btn_start)
        .setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            timer.start();
          }
        });

    view.findViewById(com.arsvechkarev.timerxexample.R.id.btn_stop)
        .setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            timer.stop();
            Toast.makeText(getActivity(),
                "rem time = " + timer.getRemainingTimeIn(TimeUnit.SECONDS),
                Toast.LENGTH_SHORT).show();
          }
        });

    view.findViewById(com.arsvechkarev.timerxexample.R.id.btn_reset)
        .setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            timer.reset();
            textTime.setText(timer.getFormattedStartTime());
          }
        });
  }
}
