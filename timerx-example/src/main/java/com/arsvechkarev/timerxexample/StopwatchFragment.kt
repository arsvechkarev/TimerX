package com.arsvechkarev.timerxexample


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_stopwatch.btn_reset
import kotlinx.android.synthetic.main.fragment_stopwatch.btn_start
import kotlinx.android.synthetic.main.fragment_stopwatch.btn_stop
import kotlinx.android.synthetic.main.fragment_stopwatch.text_time
import timerx.StopwatchBuilder
import java.util.concurrent.TimeUnit

/**
 * Example of using a stopwatch
 */
class StopwatchFragment : Fragment() {
  
  
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_stopwatch, container, false)
  }
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    
    val timer = StopwatchBuilder()
        .startFormat("SS:LLL")
        .actionWhen(5, TimeUnit.SECONDS) { showToast("5s passed") }
        .actionWhen(10, TimeUnit.SECONDS) { showToast("10s passed") }
        .actionWhen(20, TimeUnit.SECONDS) { showToast("20s passed") }
        .changeFormatWhen(5, TimeUnit.SECONDS, "SSs LLms")
        .changeFormatWhen(10, TimeUnit.SECONDS, "MM#M - SS#S")
        .onTick { time ->
          text_time.text = time
        }
        .build()
    
    text_time.text = timer.formattedStartTime
    
    btn_start.setOnClickListener { timer.start() }
    
    btn_stop.setOnClickListener {
      timer.stop()
      showToast("Current time in seconds = " + timer.getTimeIn(TimeUnit.SECONDS))
    }
    
    btn_reset.setOnClickListener {
      text_time.text = timer.formattedStartTime
      timer.reset()
    }
  }
}
