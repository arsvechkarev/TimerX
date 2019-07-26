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
import com.arsvechkarev.timerx.TimeUnits;
import com.arsvechkarev.timerx.Timer;
import com.arsvechkarev.timerx.TimerBuilder;
import com.arsvechkarev.timerx.TimerTickListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends Fragment {


  private TextView textTimer;
  private Timer timer;


  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_timer, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    textTimer = view.findViewById(com.arsvechkarev.timerxexample.R.id.text_timer);
    timer = new TimerBuilder("SS:LLL")
        .setStartTime(4, TimeUnits.SECONDS)
//        .changeFormatWhen(10, TimeUnits.SECONDS, "SS:LL")
        .setTickListener(new TimerTickListener() {
          @Override
          public void onTick(String time) {
            textTimer.setText(time);
          }

          @Override
          public void onFinish() {
            Toast.makeText(getActivity(), "Done!", Toast.LENGTH_SHORT).show();
//            textTimer.setText("Done!");
          }
        })
        .build();
    textTimer.setText(timer.getFormattedStartTime());
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
            Toast.makeText(getActivity(), "time = " + timer.getTimeIn(TimeUnits.SECONDS),
                Toast.LENGTH_SHORT).show();
          }
        });

    view.findViewById(com.arsvechkarev.timerxexample.R.id.btn_reset)
        .setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            timer.reset();
            textTimer.setText(timer.getFormattedStartTime());
          }
        });
  }
}
