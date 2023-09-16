# TimerX
A simple timer & stowatch library for android which allows for building customizable timer or stopwatch, applying custom formats, scheduling actions and more.

TimerX provides a simple api to apply a different format to a timer or a stopwatch. For instance, you can create a stopwatch, specify format as **HH:MM:SS** or **SS.LL** and time in the stopwatch will be formatted accordingly. 

See [wiki](https://github.com/arsvechkarev/TimerX/wiki/Format-syntax) for more detailed explanation.

## Gradle Setup
Make sure you have jitpack added in your top-level build.gradle file:
```groovy
allprojects {
  repositories {
    google()
    maven { url 'https://jitpack.io' }
  }
}
```


And then, add following lines in your module-level build.gradle file:
```groovy
dependencies {
    implementation 'com.github.arsvechkarev:timerx:3.1.0'
}
```


## Examples:
#### Stopwatch:
```kotlin
val stopwatch = buildStopwatch { 
    // Setting the start format of the stopwatch
    startFormat("SS:LL")
    // Setting a tick listener that gets notified when time changes
    onTick { millis: Long, time: CharSequence -> myTextView.text = time }
    // Running an action at a certain time
    actionWhen(10, TimeUnit.SECONDS) { showToast("10s passed") }
    // When the time is equal to one minute, change format to "MM:SS:LL"
    changeFormatWhen(1, TimeUnit.MINUTES, "MM:SS:LL")
}

// Starting the stopwatch
stopwatch.start();
...
```

#### Timer:
```kotlin
val timer = buildTimer {
    // Setting the start format of timer
    startFormat("MM:SS")
    // Setting the start time of the timer
    startTime(60, TimeUnit.SECONDS)
    // Setting a tick listener that gets notified when time changes
    onTick { millis: Long, time: CharSequence -> myTextView.text = time }
    // Run actions at a certain time
    actionWhen(40, TimeUnit.SECONDS) { showToast("40 seconds left") }
    actionWhen(20, TimeUnit.SECONDS) { showToast("20 seconds left") }
    // When the time is equal to ten seconds, change format to "SS:LL"
    changeFormatWhen(10, TimeUnit.SECONDS, "SS:LL")
}
    
// Starting the timer
timer.start();
...
```
