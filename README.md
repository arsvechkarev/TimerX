# TimerX
A simple timer & stowatch library for android which allows you build customizable timer or stopwatch, set custom formats, and schedule actions.

TimerX provides a simple api to apply different format to timer or stopwatch. For instance, you can create stopwatch, specify format as **HH:MM:SS** or **SS.LL** and time in stopwatch will be formating according to this format. 

See javadoc for more detailed explanation.

## Gradle Setup
Add following lines in your module-level build.gradle file:
```groovy
dependencies {
      implementation 'com.arsvechkarev:timerx:1.0.0'
}
```


## Some examples:
#### Stopwatch:
```java
Stopwatch stopwatch = new StopwatchBuilder()
      // Set start format of time
      .startFormat("MM:SS")
      // Set tick listener for displaying time
      .tickListener(time -> textViewStopwatch.setText(time)) 
      // When time is equal to one hour, change format to "HH:MM:SS"
      .changeFormatWhen(1, TimeUnit.HOURS, "HH:MM:SS")
      .build();
      
// Start stopwatch
stowatch.start()
...
```

#### Timer:
```java
Timer timer = new TimerBuilder()
      // Set start time
      .startTime(5, TimeUnit.MINUTES)
      // Set start format of time
      .startFormat("MM:SS")
      // Set tick listener that receives formatted time
      .tickListener(time -> textViewTimer.setText(time))
      // Executing action when 30 seсonds remain
      .actionWhen(30, TimeUnit.SECONDS, () -> {
          Toast.makeText(context, "30 seconds left!", Toast.LENGTH_SHORT).show();
      })
      .build();
      
// Start timer
timer.start()
...
```
