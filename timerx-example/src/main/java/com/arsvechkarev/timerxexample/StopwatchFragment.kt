package com.arsvechkarev.timerxexample


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_stopwatch.btn_reset
import kotlinx.android.synthetic.main.fragment_stopwatch.btn_set_time
import kotlinx.android.synthetic.main.fragment_stopwatch.btn_start
import kotlinx.android.synthetic.main.fragment_stopwatch.btn_stop
import kotlinx.android.synthetic.main.fragment_stopwatch.text_time
import timerx.Stopwatch
import timerx.buildStopwatch
import java.util.concurrent.TimeUnit

/**
 * Example of using a stopwatch
 */
class StopwatchFragment : Fragment() {
  
  private lateinit var stopwatch: Stopwatch
  
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_stopwatch, container, false)
  }
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    
    stopwatch = buildStopwatch {
      startFormat("0s -> SS:LLL")
      changeFormatWhen(5, TimeUnit.SECONDS, "5s -> SS:LLL")
      changeFormatWhen(10, TimeUnit.SECONDS, "10s -> SS:LL")
      changeFormatWhen(15, TimeUnit.SECONDS, "15s -> SS:LLL")
      changeFormatWhen(20, TimeUnit.SECONDS, "20s -> MM:SS")
      actionWhen(5, TimeUnit.SECONDS) { showToast("5s passed") }
      actionWhen(10, TimeUnit.SECONDS) { showToast("10s passed") }
      actionWhen(20, TimeUnit.SECONDS) { showToast("20s passed") }
      onTick { millis, formattedTime ->
        text_time.text = formattedTime
        Log.i("Stopwatch", "Current time = $millis")
      }
    }
    
    text_time.text = stopwatch.formattedStartTime
    
    btn_start.setOnClickListener { stopwatch.start() }
    
    btn_stop.setOnClickListener {
      stopwatch.stop()
      showToast("Current time in milliseconds = " + stopwatch.currentTimeInMillis)
    }
    
    btn_set_time.setOnClickListener {
      stopwatch.setTime(25, TimeUnit.SECONDS)
      // Since call to setTimeTo() does not result in invoking onTick() callback,
      // we are setting time to textView manually so that it appears there even if
      // stopwatch is paused now
      text_time.text = stopwatch.currentFormattedTime
    }
    
    btn_reset.setOnClickListener {
      text_time.text = stopwatch.formattedStartTime
      stopwatch.reset()
    }
  }
  
  override fun onDestroyView() {
    super.onDestroyView()
    stopwatch.release()
  }
}