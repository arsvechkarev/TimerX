package com.arsvechkarev.timerxexample;


import static java.util.concurrent.TimeUnit.SECONDS;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import timerx.Timer;
import timerx.TimerBuilder;

/**
 * Example of using timer
 */
public class TimerFragment extends Fragment {

  private Timer timer;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_timer, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Button buttonStart = view.findViewById(R.id.btn_start);
    Button buttonStop = view.findViewById(R.id.btn_stop);
    Button buttonReset = view.findViewById(R.id.btn_reset);

    timer = new TimerBuilder()
        .startFormat("SS:LL")
        .startTime(25, SECONDS)
        .actionWhen(18, SECONDS, () -> showToast("18s left"))
        .actionWhen(10, SECONDS, () -> showToast("10s left"))
        .actionWhen(5, SECONDS, () -> showToast("5s left"))
        .changeFormatWhen(10, SECONDS, "10: SS:LLL")
        .changeFormatWhen(5, SECONDS, "5: SS:LLLLL")
        .onTick(time -> {
          Log.d("qwerty", "time = " + time);
        })
        .build();

    buttonStart.setOnClickListener(v -> {
      timer.start();
    });

    buttonStop.setOnClickListener(v -> {
      timer.stop();
      showToast("Remaining time in seconds = " + timer.getRemainingTimeIn(SECONDS));
    });

    buttonReset.setOnClickListener(v -> {
      timer.reset();
    });

  }

  private void showToast(String text) {
    Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
  }
}
