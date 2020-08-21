package timerx.format;

import static org.junit.Assert.assertSame;
import static timerx.TestHelper.updateFormatIfNecessary;
import static timerx.format.Analyzer.create;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.Test;
import timerx.TimeUnit;
import timerx.exceptions.IllegalSymbolsCombinationException;
import timerx.exceptions.NoNecessarySymbolsException;
import timerx.exceptions.NonContiguousFormatSymbolsException;

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
    Semantic semantic = create(updateFormatIfNecessary("HH:MM:SS.LLL"));
    assertSame(TimeUnit.R_MILLISECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTest2() {
    Semantic semantic = create(updateFormatIfNecessary("HhSMM"));
    assertSame(TimeUnit.SECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTest3() {
    Semantic semantic = create(updateFormatIfNecessary("ssMMmSSmLLl"));
    assertSame(TimeUnit.R_MILLISECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTest4() {
    Semantic semantic = create(updateFormatIfNecessary("H/\\*)MMMM"));
    assertSame(TimeUnit.MINUTES, semantic.minimumUnit());
  }

  @Test
  public void positiveTest5() {
    Semantic semantic = create(updateFormatIfNecessary("MMMM%^:SS#$&*"));
    assertSame(TimeUnit.SECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTest6() {
    Semantic semantic = create(updateFormatIfNecessary(":SS::LL::#$&*"));
    assertSame(TimeUnit.R_MILLISECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTest7() {
    Semantic semantic = create(updateFormatIfNecessary("SSS"));
    assertSame(TimeUnit.SECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTestWithEscaping1() {
    Semantic semantic = create(updateFormatIfNecessary("H#HMM#M:SS#S:LL#E#Ls##"));
    assertSame(TimeUnit.R_MILLISECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTestWithEscaping2() {
    Semantic semantic = create(updateFormatIfNecessary("HH#H - MM#MSS@#S"));
    assertSame(TimeUnit.SECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTestWithEscaping3() {
    Semantic semantic = create(updateFormatIfNecessary("ssM#MmmSS#Sh#LL#h"));
    assertSame(TimeUnit.R_MILLISECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTestWithEscaping4() {
    Semantic semantic = create(updateFormatIfNecessary("H####H/#M#M#MM"));
    assertSame(TimeUnit.MINUTES, semantic.minimumUnit());
  }

  @Test
  public void positiveTestWithEscaping5() {
    Semantic semantic = create(updateFormatIfNecessary("MM#####M#M%^:SS#$&*"));
    assertSame(TimeUnit.SECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTestWithEscaping6() {
    Semantic semantic = create(updateFormatIfNecessary(":SS#S==:##LL::#$&*"));
    assertSame(TimeUnit.R_MILLISECONDS, semantic.minimumUnit());
  }

  @Test
  public void positiveTestWithEscaping7() {
    Semantic semantic = create(updateFormatIfNecessary("#S#SS#S#S"));
    assertSame(TimeUnit.SECONDS, semantic.minimumUnit());
  }

  @Test(expected = NoNecessarySymbolsException.class)
  public void negativeTestWithNoElements() {
    create(updateFormatIfNecessary("qwerty lol! ###"));
  }

  @Test(expected = NoNecessarySymbolsException.class)
  public void negativeTestWithAllCommentedElements() {
    create(updateFormatIfNecessary("#H#Hs#S#L"));
  }

  @Test(expected = NonContiguousFormatSymbolsException.class)
  public void negativeTestWithIncorrectPositions1() {
    create(updateFormatIfNecessary("H#HH"));
  }

  @Test(expected = NonContiguousFormatSymbolsException.class)
  public void negativeTestWithIncorrectPositions2() {
    create(updateFormatIfNecessary("HH:MM:SSqwertyH"));
  }

  @Test(expected = NonContiguousFormatSymbolsException.class)
  public void negativeTestWithIncorrectPositions3() {
    create(updateFormatIfNecessary("HH#HSSS %^&*sS"));
  }

  @Test(expected = NonContiguousFormatSymbolsException.class)
  public void negativeTestWithIncorrectPositions4() {
    create(updateFormatIfNecessary("LLasfdLH^&sdHasdL"));
  }

  @Test(expected = NonContiguousFormatSymbolsException.class)
  public void negativeTestWithIncorrectPositions5() {
    create(updateFormatIfNecessary("M#M#H#H098/M"));
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination1() {
    create(updateFormatIfNecessary("HH:MM:L"));
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination2() {
    create(updateFormatIfNecessary("HH:SS:L"));
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination3() {
    create(updateFormatIfNecessary("HH:SS"));
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination4() {
    create(updateFormatIfNecessary("LLLL:H"));
  }

  @Test(expected = IllegalSymbolsCombinationException.class)
  public void negativeTestWithIncorrectCombination5() {
    create(updateFormatIfNecessary(":M#ME#::LL"));
  }
}