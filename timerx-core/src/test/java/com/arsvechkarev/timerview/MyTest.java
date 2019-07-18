package com.arsvechkarev.timerview;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;


// TODO: Temporary class, delete in future
public class MyTest {

  private static final String PATTERN_HAS_HOURS = "(?<!#)H+";
  private static final String PATTERN_HAS_MINUTES = "(?<!#)M+";
  private static final String PATTERN_HAS_SECONDS = "(?<!#)S+";
  private static final String PATTERN_HAS_MILLIS = "(?<!#)L+";


  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void tesst() {
//    Pattern pattern = Pattern.compile(PATTERN_HAS_HOURS);
//    Matcher matcher = pattern.matcher(input);
//
//    int count = 0;
//    while (matcher.find()) {
//      count++;
//    }
//
//    System.out.println("find: " + count);
//    checkPattern(null, null);

//    String s2 = "fdsHfds";
//    String s3 = "sflMM";
//    String s4 = "M";
//    String s5 = "LLLLLLLLLL";
//    String s6 = "LLLLfasfdas #L";

//    String s2 = "fdsHfds";
//    new TimeFormatter(s3);
//    new TimeFormatter(s4);
//    new TimeFormatter(s5);
//    new TimeFormatter(s6);
  }

  @Test
  public void qwerty() {
    String s = "H";
    new TimeFormatter(s);
  }

  @Test
  public void checkPattern() {
    Pattern pattern = Pattern.compile("(?<!#)H+");
    Matcher matcher = pattern.matcher("fdsHfds");
    int counter = 0;
    while (matcher.find()) {
      counter++;
      if (counter > 1) {
        throwEx();
      }
    }
  }

  private void throwEx() {
    throw new IllegalArgumentException("Input format is incorrect");
  }


  @Test
  public void test() {
    String s = "HH:MM:SS";

    System.out.println(
        Pattern.matches("[^H]*[H]*[^H]*", s));

    Pattern pattern = Pattern.compile("[^H]*[H]*[^H]*");
    Matcher matcher = pattern.matcher(s);
    matcher.end("fd");

    while (matcher.find()) {
      System.out.println("found: '" + matcher.group() + "' at (" + matcher.start()
          + "," + matcher.end() + ")");
    }
  }


  @Test
  public void test3() {

    String regeg = "(?<!#)H+";
    String inpup = "HHH ##H";

    System.out.println(inpup.replaceAll(regeg, "lala").replaceAll("#H", "H"));
//    System.out.println(inpup.replaceAll("#H", "H"));

    if (true) {
      return;
    }

    String regex2 = "(?<!#)H+";
    String input = "HHH #H";
    Pattern pattern = Pattern.compile(regex2);
    Matcher matcher = pattern.matcher(input);

    int count = 0;
    while (matcher.find()) {
      count++;
      //if (count > 1) break;
    }

    if (count > 1) {
      System.out.println("more than 1: " + count);
    } else {
      if (count == 0) {
        System.out.println("none");
      } else if (count == 1) {
        System.out.println("exactly 1");
      }
    }
  }

  @Test
  public void test2() {
    String s = "H\\H";
    boolean containsAny = Pattern.matches("[^\\\\]*[HMSL].*", s);
    System.out.println(containsAny);
    System.out.println("---");
    if (containsAny) {
      boolean a = Pattern.matches(PATTERN_HAS_HOURS, s);
      boolean b = Pattern.matches(PATTERN_HAS_MINUTES, s);
      boolean c = Pattern.matches(PATTERN_HAS_SECONDS, s);
      boolean d = Pattern.matches(PATTERN_HAS_MILLIS, s);
      System.out.println(a);
      System.out.println(b);
      System.out.println(c);
      System.out.println(d);
      System.out.println("=======");
      // is correct?
      System.out.println((a && b && c && d));
    }

//
//
//    boolean a = Pattern.matches(PATTERN_HAS_HOURS, s);
//    boolean b = Pattern.matches(PATTERN_HAS_MINUTES, s);
//    boolean c = Pattern.matches(PATTERN_HAS_SECONDS, s);
//    boolean d = Pattern.matches(PATTERN_HAS_MILLIS, s);
//    System.out.println(a);
//    System.out.println(b);
//    System.out.println(c);
//    System.out.println(d);
//    System.out.println("=======");
//
//    // is correct?
//    System.out.println(  (a || b || c || d)  );

  }
}