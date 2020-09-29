# TimerX
A simple timer & stowatch library for android which allows you build customizable timer or stopwatch, set custom formats, and schedule actions.

TimerX provides a simple api to apply different format to a timer or a stopwatch. For instance, you can create stopwatch, specify format as **HH:MM:SS** or **SS.LL** and time in the stopwatch will be formatted according to this format. 

See [wiki](https://github.com/arsvechkarev/TimerX/wiki/Format-syntax) for more detailed explanation.

## Gradle Setup
Make sure you have jcenter added in your top-level build.gradle file:
```groovy
allprojects {
  repositories {
    google()
    jcenter() // Make sure you have this line
  }
}
```


And then, add following lines in your module-level build.gradle file:
```groovy
dependencies {
      implementation 'com.arsvechkarev:timerx:2.1.0'
}
```


## Examples:
#### Stopwatch:
```java
Stopwatch stopwatch = new StopwatchBuilder()
      // Set the initial format
      .startFormat("MM:SS")
      // Set the tick listener for displaying time
      .tickListener(time -> textViewStopwatch.setText(time)) 
      // When time is equal to one hour, change format to "HH:MM:SS"
      .changeFormatWhen(1, TimeUnit.HOURS, "HH:MM:SS")
      .build();
      
// Start the stopwatch
stowatch.start()
...
```

#### Timer:
```java
Timer timer = new TimerBuilder()
      // Set start time
      .startTime(5, TimeUnit.MINUTES)
      // Set the initial format
      .startFormat("MM:SS")
      // Set the tick listener that receives formatted time
      .tickListener(time -> textViewTimer.setText(time))
      // Run an action when the remaining time is 30 seсonds
      .actionWhen(30, TimeUnit.SECONDS, () -> {
          Toast.makeText(context, "30 seconds left!", Toast.LENGTH_SHORT).show();
      })
      .build();
      
// Start the timer
timer.start()
...
```
