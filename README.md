# TimerX
A simple timer & stowatch library for android which allows to set format for time, schedule actions and more.

TimerX provides simple api to apply different format to timer or stopwatch. For example, you can create stopwatch, specify format as **HH:MM:SS** or **SS.LL** and time in stopwatch will formats according to your format. 

Format syntax explanation and more exapmles here.

## Gradle Setup
In your module-level build.gradle file:
```groovy
dependencies {
      implementation 'com.arsvechkarev:timerx:0.7.6'
}
```


## Some examples:
#### Stopwatch:
```java
Stopwatch stopwatch = new StopwatchBuilder()
      // Set start format of time
      .startFormat("MM:SS")
      // Set tick listener that receives formatted time
      .tickListener(time -> textViewStopwatch.setText(time)) 
      // When time will be equal to one hour, change format to "HH:MM:SS"
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

## Documentation
Detailed documentation here
