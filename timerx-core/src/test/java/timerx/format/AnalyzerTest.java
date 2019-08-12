package timerx.format;

import static org.junit.Assert.assertSame;
import static timerx.TestHelper.updateFormatIfNecessary;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.Test;
import timerx.TimeUnits;
import timerx.exceptions.IllegalSymbolsCombinationException;
import timerx.exceptions.IllegalSymbolsPositionException;
import timerx.exceptions.NoNecessarySymbolsException;

@SuppressWarnings("SpellCheckingInspection")
public class AnalyzerTest {

  // Just for cover semantic class with 100% tests
  @Test(expected = IllegalArgumentException.class)
  public void nextPatternOfPositionFailsWithWrongArguments() throws Throwable {
    Method nextPatternOf = Analyzer.class.getDeclaredMethod("nextPatternOf", int.class);
    nextPatternOf.setAccessible(true);
    Constructor<Analyzer> constructor = Analyzer.class
        .getDeclaredConstructor(Semantic.class);
    constructor.setAccessible(true);
    Constructor<Semantic> semanticConstructor = Semantic.class
        .getDeclaredConstructor(String.class);
    semanticConstructor.setAccessible(true);
    Semantic semantic = semanticConstructor.newInstance("format");
    Analyzer analyzer = constructor.newInstance(semantic);
    try {
      nextPatternOf.invoke(analyzer, 500);
    } catch (InvocationTargetException e) {
      throw e.getCause();
    }
  }

  // Just for 100% coverage of analyzer
  @Test(expected = IllegalArgumentException.class)
  public void updateAppropriateFieldFailsWithWrongArguments() throws Throwable {
    Method updateAppropriateField = Analyzer.class
        .getDeclaredMethod("updateAppropriateField", int.class, int.class);
    updateAppropriateField.setAccessible(true);
    Constructor<Analyzer> constructor = Analyzer.class
        .getDeclaredConstructor(Semantic.class);
    constructor.setAccessible(true);
    Constructor<Semantic> semanticConstructor = Semantic.class
        .getDeclaredConstructor(String.class);
    semanticConstructor.setAccessible(true);
    Semantic semantic = semanticConstructor.newInstance("format");
    Analyzer analyzer = constructor.newInstance(semantic);
    try {
      updateAppropriateField.invoke(analyzer, 500, 500);
    } catch (InvocationTargetException e) {
      throw e.getCause();
    }
  }

  @Test
  public void positiveTest1() {
    Semantic semantic = Analyzer.check(updateFormatIfNecessary("HH:MM:SS.LLL"));
    assertSame(TimeUnits.R_MILLISECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTest2() {
    Semantic semantic = Analyzer.check(updateFormatIfNecessary("HhSMM"));
    assertSame(TimeUnits.SECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTest3() {
    Semantic semantic = Analyzer.check(updateFormatIfNecessary("ssMMmSSmLLl"));
    assertSame(TimeUnits.R_MILLISECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTest4() {
    Semantic semantic = Analyzer.check(updateFormatIfNecessary("H/\\*)MMMM"));
    assertSame(TimeUnits.MINUTES, semantic.minimumUnit());
  }

  @Test
  public void positiveTest5() {
    Semantic semantic = Analyzer.check(updateFormatIfNecessary("MMMM%^:SS#$&*"));
    assertSame(TimeUnits.SECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTest6() {
    Semantic semantic = Analyzer.check(updateFormatIfNecessary(":SS::LL::#$&*"));
    assertSame(TimeUnits.R_MILLISECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTest7() {
    Semantic semantic = Analyzer.check(updateFormatIfNecessary("SSS"));
    assertSame(TimeUnits.SECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTestWithEscaping1() {
    Semantic semantic = Analyzer.check(updateFormatIfNecessary("H#HMM#M:SS#S:LL#E#Ls##"));
    assertSame(TimeUnits.R_MILLISECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTestWithEscaping2() {
    Semantic semantic = Analyzer.check(updateFormatIfNecessary("HH#H - MM#MSS@#S"));
    assertSame(TimeUnits.SECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTestWithEscaping3() {
    Semantic semantic = Analyzer.check(updateFormatIfNecessary("ssM#MmmSS#Sh#LL#h"));
    assertSame(TimeUnits.R_MILLISECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTestWithEscaping4() {
    Semantic semantic = Analyzer.check(updateFormatIfNecessary("H####H/#M#M#MM"));
    assertSame(TimeUnits.MINUTES, semantic.minimumUnit());
  }

  @Test
  public void positiveTestWithEscaping5() {
    Semantic semantic = Analyzer.check(updateFormatIfNecessary("MM#####M#M%^:SS#$&*"));
    assertSame(TimeUnits.SECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTestWithEscaping6() {
    Semantic semantic = Analyzer.check(updateFormatIfNecessary(":SS#S==:##LL::#$&*"));
    assertSame(TimeUnits.R_MILLISECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTestWithEscaping7() {
    Semantic semantic = Analyzer.check(updateFormatIfNecessary("#S#SS#S#S"));
    assertSame(TimeUnits.SECONDS, semantic.minimumUnit());
  }

  @Test(expected = NoNecessarySymbolsException.class)
  public void negativeTestWithNoElements() {
    Analyzer.check(updateFormatIfNecessary("qwerty lol! ###"));
  }

  @Test(expected = NoNecessarySymbolsException.class)
  public void negativeTestWithAllCommentedElements() {
    Analyzer.check(updateFormatIfNecessary("#H#Hs#S#L"));
  }

  @Test(expected = IllegalSymbolsPositionException.class)
  public void negativeTestWithIncorrectPositions1() {
    Analyzer.check(updateFormatIfNecessary("H#HH"));
  }

  @Test(expected = IllegalSymbolsPositionException.class)
  public void negativeTestWithIncorrectPositions2() {
    Analyzer.check(updateFormatIfNecessary("HH:MM:SSqwertyH"));
  }

  @Test(expected = IllegalSymbolsPositionException.class)
  public void negativeTestWithIncorrectPositions3() {
    Analyzer.check(updateFormatIfNecessary("HH#HSSS %^&*sS"));
  }

  @Test(expected = IllegalSymbolsPositionException.class)
  public void negativeTestWithIncorrectPositions4() {
    Analyzer.check(updateFormatIfNecessary("LLasfdLH^&sdHasdL"));
  }

  @Test(expected = IllegalSymbolsPositionException.class)
  public void negativeTestWithIncorrectPositions5() {
    Analyzer.check(updateFormatIfNecessary("M#M#H#H098/M"));
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination1() {
    Analyzer.check(updateFormatIfNecessary("HH:MM:L"));
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination2() {
    Analyzer.check(updateFormatIfNecessary("HH:SS:L"));
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination3() {
    Analyzer.check(updateFormatIfNecessary("HH:SS"));
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination4() {
    Analyzer.check(updateFormatIfNecessary("LLLL:H"));
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination5() {
    Analyzer.check(updateFormatIfNecessary(":M#ME#::LL"));
  }
}