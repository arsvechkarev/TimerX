package com.arsvechkarev.timerx.format;

import com.arsvechkarev.timerx.exceptions.IllegalSymbolsCombinationException;
import com.arsvechkarev.timerx.exceptions.IllegalSymbolsPositionException;
import com.arsvechkarev.timerx.exceptions.NoNecessarySymbolsException;
import org.junit.Test;

@SuppressWarnings("SpellCheckingInspection")
public class ValidatorTest {

  @Test
  public void positiveTest1() {
    Validator.check(new Semantic("HH:MM:SS.LLL"));
  }

  @Test
  public void positiveTest2() {
    Validator.check(new Semantic("HhSMM"));
  }

  @Test
  public void positiveTest3() {
    Validator.check(new Semantic("ssMMmSSmLLl"));
  }

  @Test
  public void positiveTest4() {
    Validator.check(new Semantic("H/\\*)MMMM"));
  }

  @Test
  public void positiveTest5() {
    Validator.check(new Semantic("MMMM%^:SS#$&*"));
  }

  @Test
  public void positiveTest6() {
    Validator.check(new Semantic(":SS::LL::#$&*"));
  }

  @Test
  public void positiveTest7() {
    Validator.check(new Semantic("SSS"));
  }

  @Test
  public void positiveTestWithEscaping1() {
    Validator.check(new Semantic("H#HMM#M:SS#S:LL#E#Ls##"));
  }

  @Test
  public void positiveTestWithEscaping2() {
    Validator.check(new Semantic("HH#H - MM#MSS@#S"));
  }

  @Test
  public void positiveTestWithEscaping3() {
    Validator.check(new Semantic("ssM#MmmSS#Sh#LL#h"));
  }

  @Test
  public void positiveTestWithEscaping4() {
    Validator.check(new Semantic("H####H/#M#M#MM"));
  }

  @Test
  public void positiveTestWithEscaping5() {
    Validator.check(new Semantic("MM#####M#M%^:SS#$&*"));
  }

  @Test
  public void positiveTestWithEscaping6() {
    Validator.check(new Semantic(":SS#S==:##LL::#$&*"));
  }

  @Test
  public void positiveTestWithEscaping7() {
    Validator.check(new Semantic("#S#SS#S#S"));
  }

  @Test(expected = NoNecessarySymbolsException.class)
  public void negativeTestWithNoElements() {
    Validator.check(new Semantic("qwerty lol! ###"));
  }

  @Test(expected = NoNecessarySymbolsException.class)
  public void negativeTestWithAllCommentedElements() {
    Validator.check(new Semantic("#H#Hs#S#L"));
  }

  @Test(expected = IllegalSymbolsPositionException.class)
  public void negativeTestWithIncorrectPositions1() {
    Validator.check(new Semantic("H#HH"));
  }

  @Test(expected = IllegalSymbolsPositionException.class)
  public void negativeTestWithIncorrectPositions2() {
    Validator.check(new Semantic("HH:MM:SSqwertyH"));
  }

  @Test(expected = IllegalSymbolsPositionException.class)
  public void negativeTestWithIncorrectPositions3() {
    Validator.check(new Semantic("HH#HSSS %^&*sS"));
  }

  @Test(expected = IllegalSymbolsPositionException.class)
  public void negativeTestWithIncorrectPositions4() {
    Validator.check(new Semantic("LLasfdLH^&sdHasdL"));
  }

  @Test(expected = IllegalSymbolsPositionException.class)
  public void negativeTestWithIncorrectPositions5() {
    Validator.check(new Semantic("M#M#H#H098/M"));
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination1() {
    Validator.check(new Semantic("HH:MM:L"));
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination2() {
    Validator.check(new Semantic("HH:SS:L"));
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination3() {
    Validator.check(new Semantic("HH:SS"));
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination4() {
    Validator.check(new Semantic("LLLL:H"));
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination5() {
    Validator.check(new Semantic(":M#ME#::LL"));
  }
}