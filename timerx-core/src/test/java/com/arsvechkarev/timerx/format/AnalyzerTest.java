package com.arsvechkarev.timerx.format;

import static org.junit.Assert.assertSame;

import com.arsvechkarev.timerx.TimeUnits;
import com.arsvechkarev.timerx.exceptions.IllegalSymbolsCombinationException;
import com.arsvechkarev.timerx.exceptions.IllegalSymbolsPositionException;
import com.arsvechkarev.timerx.exceptions.NoNecessarySymbolsException;
import org.junit.Test;

@SuppressWarnings("SpellCheckingInspection")
public class AnalyzerTest {

  @Test
  public void positiveTest1() {
    Semantic semantic = Analyzer.check("HH:MM:SS.LLL");
    assertSame(semantic.minimumUnit(), TimeUnits.R_MILLISECONDS);
  }

  @Test
  public void positiveTest2() {
    Semantic semantic = Analyzer.check("HhSMM");
    assertSame(semantic.minimumUnit(), TimeUnits.SECONDS);
  }

  @Test
  public void positiveTest3() {
    Semantic semantic = Analyzer.check("ssMMmSSmLLl");
    assertSame(semantic.minimumUnit(), TimeUnits.R_MILLISECONDS);
  }

  @Test
  public void positiveTest4() {
    Semantic semantic = Analyzer.check("H/\\*)MMMM");
    assertSame(semantic.minimumUnit(), TimeUnits.MINUTES);
  }

  @Test
  public void positiveTest5() {
    Semantic semantic = Analyzer.check("MMMM%^:SS#$&*");
    assertSame(semantic.minimumUnit(), TimeUnits.SECONDS);
  }

  @Test
  public void positiveTest6() {
    Semantic semantic = Analyzer.check(":SS::LL::#$&*");
    assertSame(semantic.minimumUnit(), TimeUnits.R_MILLISECONDS);
  }

  @Test
  public void positiveTest7() {
    Semantic semantic = Analyzer.check("SSS");
    assertSame(semantic.minimumUnit(), TimeUnits.SECONDS);
  }

  @Test
  public void positiveTestWithEscaping1() {
    Semantic semantic = Analyzer.check("H#HMM#M:SS#S:LL#E#Ls##");
    assertSame(semantic.minimumUnit(), TimeUnits.R_MILLISECONDS);
  }

  @Test
  public void positiveTestWithEscaping2() {
    Semantic semantic = Analyzer.check("HH#H - MM#MSS@#S");
    assertSame(semantic.minimumUnit(), TimeUnits.SECONDS);
  }

  @Test
  public void positiveTestWithEscaping3() {
    Semantic semantic = Analyzer.check("ssM#MmmSS#Sh#LL#h");
    assertSame(semantic.minimumUnit(), TimeUnits.R_MILLISECONDS);
  }

  @Test
  public void positiveTestWithEscaping4() {
    Semantic semantic = Analyzer.check("H####H/#M#M#MM");
    assertSame(semantic.minimumUnit(), TimeUnits.MINUTES);
  }

  @Test
  public void positiveTestWithEscaping5() {
    Semantic semantic = Analyzer.check("MM#####M#M%^:SS#$&*");
    assertSame(semantic.minimumUnit(), TimeUnits.SECONDS);
  }

  @Test
  public void positiveTestWithEscaping6() {
    Semantic semantic = Analyzer.check(":SS#S==:##LL::#$&*");
    assertSame(semantic.minimumUnit(), TimeUnits.R_MILLISECONDS);
  }

  @Test
  public void positiveTestWithEscaping7() {
    Semantic semantic = Analyzer.check("#S#SS#S#S");
    assertSame(semantic.minimumUnit(), TimeUnits.SECONDS);
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