package com.arsvechkarev.timerxexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val sectionsPagerAdapter = SectionsPagerAdapter(
      supportFragmentManager)
    val viewPager = findViewById<ViewPager>(R.id.view_pager)
    viewPager.adapter = sectionsPagerAdapter
    val tabs = findViewById<TabLayout>(R.id.tabs)
    tabs.setupWithViewPager(viewPager)
  }
  
  private inner class SectionsPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
  
    override fun getItem(position: Int): Fragment {
      return if (position == 0)
        StopwatchFragment()
      else
        TimerFragment()
    }
  
    override fun getPageTitle(position: Int): CharSequence? {
      return if (position == 0)
        "Stopwatch"
      else
        "Timer"
    }
    
    override fun getCount(): Int {
      return 2
    }
  }
}
