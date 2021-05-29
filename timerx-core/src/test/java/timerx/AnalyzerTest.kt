package timerx

import org.junit.Assert
import org.junit.Test

class AnalyzerTest {
  
  private fun smallestAvailableUnitOf(format: String): TimeUnitType {
    return analyze(format).smallestAvailableUnit
  }
  
  private fun analyze(format: String): Semantic {
    return Analyzer.analyze(TestHelper.updateFormatIfNeeded(format))
  }
  
  @Test
  fun positiveTest1() {
    Assert.assertSame(TimeUnitType.R_MILLISECONDS, smallestAvailableUnitOf("HH:MM:SS.LLL"))
  }
  
  @Test
  fun positiveTest2() {
    Assert.assertSame(TimeUnitType.SECONDS, smallestAvailableUnitOf("HhSMM"))
  }
  
  @Test
  fun positiveTest3() {
    Assert.assertSame(TimeUnitType.R_MILLISECONDS, smallestAvailableUnitOf("ssMMmSSmLLl"))
  }
  
  @Test
  fun positiveTest4() {
    Assert.assertSame(TimeUnitType.MINUTES, smallestAvailableUnitOf("H/\\*)MMMM"))
  }
  
  @Test
  fun positiveTest5() {
    Assert.assertSame(TimeUnitType.SECONDS, smallestAvailableUnitOf("MMMM%^:SS#$&*"))
  }
  
  @Test
  fun positiveTest6() {
    Assert.assertSame(TimeUnitType.R_MILLISECONDS, smallestAvailableUnitOf(":SS::LL::#$&*"))
  }
  
  @Test
  fun positiveTest7() {
    Assert.assertSame(TimeUnitType.SECONDS, smallestAvailableUnitOf("SSS"))
  }
  
  @Test
  fun positiveTestWithEscaping1() {
    Assert.assertSame(TimeUnitType.R_MILLISECONDS,
      smallestAvailableUnitOf("H#HMM#M:SS#S:LL#E#Ls##"))
  }
  
  @Test
  fun positiveTestWithEscaping2() {
    Assert.assertSame(TimeUnitType.SECONDS, smallestAvailableUnitOf("HH#H - MM#MSS@#S"))
  }
  
  @Test
  fun positiveTestWithEscaping3() {
    Assert.assertSame(TimeUnitType.R_MILLISECONDS, smallestAvailableUnitOf("ssM#MmmSS#Sh#LL#h"))
  }
  
  @Test
  fun positiveTestWithEscaping4() {
    Assert.assertSame(TimeUnitType.MINUTES, smallestAvailableUnitOf("H####H/#M#M#MM"))
  }
  
  @Test
  fun positiveTestWithEscaping5() {
    Assert.assertSame(TimeUnitType.SECONDS, smallestAvailableUnitOf("MM#####M#M%^:SS#$&*"))
  }
  
  @Test
  fun positiveTestWithEscaping6() {
    Assert.assertSame(TimeUnitType.R_MILLISECONDS,
      smallestAvailableUnitOf(":SS#S==:##LL::#$&*"))
  }
  
  @Test
  fun positiveTestWithEscaping7() {
    Assert.assertSame(TimeUnitType.SECONDS, smallestAvailableUnitOf("#S#SS#S#S"))
  }
  
  @Test(expected = NoNecessarySymbolsException::class)
  fun negativeTestWithNoElements() {
    analyze("qwerty lol! ###")
  }
  
  @Test(expected = NoNecessarySymbolsException::class)
  fun negativeTestWithAllCommentedElements() {
    analyze("#H#Hs#S#L")
  }
  
  @Test(expected = NonContiguousFormatSymbolsException::class)
  fun negativeTestWithIncorrectPositions1() {
    analyze("H#HH")
  }
  
  @Test(expected = NonContiguousFormatSymbolsException::class)
  fun negativeTestWithIncorrectPositions2() {
    analyze("HH:MM:SSqwertyH")
  }
  
  @Test(expected = NonContiguousFormatSymbolsException::class)
  fun negativeTestWithIncorrectPositions3() {
    analyze("HH#HSSS %^&*sS")
  }
  
  @Test(expected = NonContiguousFormatSymbolsException::class)
  fun negativeTestWithIncorrectPositions4() {
    analyze("LLasfdLH^&sdHasdL")
  }
  
  @Test(expected = NonContiguousFormatSymbolsException::class)
  fun negativeTestWithIncorrectPositions5() {
    analyze("M#M#H#H098/M")
  }
  
  @Test(expected = IllegalSymbolsCombinationException::class)
  fun negativeTestWithIncorrectCombination1() {
    analyze("HH:MM:L")
  }
  
  @Test(expected = IllegalSymbolsCombinationException::class)
  fun negativeTestWithIncorrectCombination2() {
    analyze("HH:SS:L")
  }
  
  @Test(expected = IllegalSymbolsCombinationException::class)
  fun negativeTestWithIncorrectCombination3() {
    analyze("HH:SS")
  }
  
  @Test(expected = IllegalSymbolsCombinationException::class)
  fun negativeTestWithIncorrectCombination4() {
    analyze("LLLL:H")
  }
  
  @Test(expected = IllegalSymbolsCombinationException::class)
  fun negativeTestWithIncorrectCombination5() {
    analyze(":M#ME#::LL")
  }
}