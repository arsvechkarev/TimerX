package com.arsvechkarev.timerx.format;

public class Analyzer {

  public static Semantic checkFormat(String format) {
    Semantic semantic = new Semantic(format);
    Validator.check(semantic);
    return semantic;
  }

}
