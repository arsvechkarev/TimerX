package com.arsvechkarev.timerview;

import com.arsvechkarev.timerview.exceptions.IllegalSymbolsCombinationException;
import com.arsvechkarev.timerview.exceptions.IllegalSymbolsPositionException;
import com.arsvechkarev.timerview.exceptions.NoNecessarySymbolsException;
import org.junit.Test;

public class ValidatorTest {

  @Test
  public void positiveTest1() {
    String format = "HH:MM:SS";
    Validator.check(format);
  }

  @Test
  public void positiveTest2() {
    String format = "LLLL";
    Validator.check(format);
  }

  @Test
  public void positiveTest3() {
    String format = "ssMMmmHHhh";
    Validator.check(format);
  }

  @Test
  public void positiveTest4() {
    String format = "SS/LLLL";
    Validator.check(format);
  }

  @Test
  public void positiveTest5() {
    String format = "MMMM%^:SS#$&*";
    Validator.check(format);
  }

  @Test
  public void positiveTestWithEscaping1() {
    String format = "HH#H:MM#M:SSs##";
    Validator.check(format);
  }

  @Test
  public void positiveTestWithEscaping2() {
    String format = "#LLLL#H#L";
    Validator.check(format);
  }

  @Test
  public void positiveTestWithEscaping3() {
    String format = "ssM#MmmH#Hh##h";
    Validator.check(format);
  }

  @Test
  public void positiveTestWithEscaping4() {
    String format = "S####S/#L#L#LL";
    Validator.check(format);
  }

  @Test
  public void positiveTestWithEscaping5() {
    String format = "MM#####M#M%^:SS#$&*";
    Validator.check(format);
  }

  @Test(expected = NoNecessarySymbolsException.class)
  public void negativeTestWithNoElements() {
    String format = "qwerty lol! ###";
    Validator.check(format);
  }

  @Test(expected = IllegalSymbolsPositionException.class)
  public void negativeTestWithIncorrectPositions1() {
    String format = "H#HH";
    Validator.check(format);
  }

  @Test(expected = IllegalSymbolsPositionException.class)
  public void negativeTestWithIncorrectPositions2() {
    String format = "HH:MM:SSqwertyH";
    Validator.check(format);
  }

  @Test(expected = IllegalSymbolsPositionException.class)
  public void negativeTestWithIncorrectPositions3() {
    String format = "HH#HSSS %^&*sS";
    Validator.check(format);
  }

  @Test(expected = IllegalSymbolsPositionException.class)
  public void negativeTestWithIncorrectPositions4() {
    String format = "LLasfdLH^&sdHasdL";
    Validator.check(format);
  }

  @Test(expected = IllegalSymbolsPositionException.class)
  public void negativeTestWithIncorrectPositions5() {
    String format = "M#M#H#H098/M";
    Validator.check(format);
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination1() {
    String format = "HH:MM:L";
    Validator.check(format);
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination2() {
    String format = "HH:SS:L";
    Validator.check(format);
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination3() {
    String format = "HH:SS";
    Validator.check(format);
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination4() {
    String format = "LLLL:H";
    Validator.check(format);
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination5() {
    String format = ":M#ME#::LL";
    Validator.check(format);
  }
}