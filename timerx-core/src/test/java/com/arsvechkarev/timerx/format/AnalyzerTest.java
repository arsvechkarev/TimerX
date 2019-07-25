package com.arsvechkarev.timerx.format;

import com.arsvechkarev.timerx.exceptions.IllegalSymbolsCombinationException;
import com.arsvechkarev.timerx.exceptions.IllegalSymbolsPositionException;
import com.arsvechkarev.timerx.exceptions.NoNecessarySymbolsException;
import org.junit.Test;

@SuppressWarnings("SpellCheckingInspection")
public class AnalyzerTest {

  @Test
  public void positiveTest1() {
    Analyzer.check("HH:MM:SS.LLL");
  }

  @Test
  public void positiveTest2() {
    Analyzer.check("HhSMM");
  }

  @Test
  public void positiveTest3() {
    Analyzer.check("ssMMmSSmLLl");
  }

  @Test
  public void positiveTest4() {
    Analyzer.check("H/\\*)MMMM");
  }

  @Test
  public void positiveTest5() {
    Analyzer.check("MMMM%^:SS#$&*");
  }

  @Test
  public void positiveTest6() {
    Analyzer.check(":SS::LL::#$&*");
  }

  @Test
  public void positiveTest7() {
    Analyzer.check("SSS");
  }

  @Test
  public void positiveTestWithEscaping1() {
    Analyzer.check("H#HMM#M:SS#S:LL#E#Ls##");
  }

  @Test
  public void positiveTestWithEscaping2() {
    Analyzer.check("HH#H - MM#MSS@#S");
  }

  @Test
  public void positiveTestWithEscaping3() {
    Analyzer.check("ssM#MmmSS#Sh#LL#h");
  }

  @Test
  public void positiveTestWithEscaping4() {
    Analyzer.check("H####H/#M#M#MM");
  }

  @Test
  public void positiveTestWithEscaping5() {
    Analyzer.check("MM#####M#M%^:SS#$&*");
  }

  @Test
  public void positiveTestWithEscaping6() {
    Analyzer.check(":SS#S==:##LL::#$&*");
  }

  @Test
  public void positiveTestWithEscaping7() {
    Analyzer.check("#S#SS#S#S");
  }

  @Test(expected = NoNecessarySymbolsException.class)
  public void negativeTestWithNoElements() {
    Analyzer.check("qwerty lol! ###");
  }

  @Test(expected = NoNecessarySymbolsException.class)
  public void negativeTestWithAllCommentedElements() {
    Analyzer.check("#H#Hs#S#L");
  }

  @Test(expected = IllegalSymbolsPositionException.class)
  public void negativeTestWithIncorrectPositions1() {
    Analyzer.check("H#HH");
  }

  @Test(expected = IllegalSymbolsPositionException.class)
  public void negativeTestWithIncorrectPositions2() {
    Analyzer.check("HH:MM:SSqwertyH");
  }

  @Test(expected = IllegalSymbolsPositionException.class)
  public void negativeTestWithIncorrectPositions3() {
    Analyzer.check("HH#HSSS %^&*sS");
  }

  @Test(expected = IllegalSymbolsPositionException.class)
  public void negativeTestWithIncorrectPositions4() {
    Analyzer.check("LLasfdLH^&sdHasdL");
  }

  @Test(expected = IllegalSymbolsPositionException.class)
  public void negativeTestWithIncorrectPositions5() {
    Analyzer.check("M#M#H#H098/M");
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination1() {
    Analyzer.check("HH:MM:L");
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination2() {
    Analyzer.check("HH:SS:L");
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination3() {
    Analyzer.check("HH:SS");
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination4() {
    Analyzer.check("LLLL:H");
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination5() {
    Analyzer.check(":M#ME#::LL");
  }
}