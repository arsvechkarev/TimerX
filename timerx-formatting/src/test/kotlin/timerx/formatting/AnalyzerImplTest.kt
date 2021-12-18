package timerx.formatting

import org.junit.Assert.assertSame
import org.junit.Test
import timerx.formatting.TimeUnitType.HOURS
import timerx.formatting.TimeUnitType.MINUTES
import timerx.formatting.TimeUnitType.R_MILLISECONDS
import timerx.formatting.TimeUnitType.SECONDS

class AnalyzerImplTest {
  
  @Test
  fun smallestAvailableUnitTest1() {
    assertSame(R_MILLISECONDS, smallestAvailableUnitOf("HH:MM:SS.LLL"))
  }
  
  @Test
  fun smallestAvailableUnitTest2() {
    assertSame(SECONDS, smallestAvailableUnitOf("HhSMM"))
  }
  
  @Test
  fun smallestAvailableUnitTest3() {
    assertSame(R_MILLISECONDS, smallestAvailableUnitOf("ssMMmSSmLLl"))
  }
  
  @Test
  fun smallestAvailableUnitTest4() {
    assertSame(MINUTES, smallestAvailableUnitOf("H/\\*)MMMM"))
  }
  
  @Test
  fun smallestAvailableUnitTest5() {
    assertSame(SECONDS, smallestAvailableUnitOf("MMMM%^:SS#$&*"))
  }
  
  @Test
  fun smallestAvailableUnitTest6() {
    assertSame(R_MILLISECONDS, smallestAvailableUnitOf(":SS::LL::#$&*"))
  }
  
  @Test
  fun smallestAvailableUnitTest7() {
    assertSame(SECONDS, smallestAvailableUnitOf("SSS"))
  }
  
  @Test
  fun smallestAvailableUnitTestWithEscaping1() {
    assertSame(R_MILLISECONDS,
      smallestAvailableUnitOf("H#HMM#M:SS#S:LL#E#Ls##"))
  }
  
  @Test
  fun smallestAvailableUnitTestWithEscaping2() {
    assertSame(SECONDS, smallestAvailableUnitOf("HH#H - MM#MSS@#S"))
  }
  
  @Test
  fun smallestAvailableUnitTestWithEscaping3() {
    assertSame(R_MILLISECONDS, smallestAvailableUnitOf("ssM#MmmSS#Sh#LL#h"))
  }
  
  @Test
  fun smallestAvailableUnitTestWithEscaping4() {
    assertSame(MINUTES, smallestAvailableUnitOf("H####H/#M#M#MM"))
  }
  
  @Test
  fun smallestAvailableUnitTestWithEscaping5() {
    assertSame(SECONDS, smallestAvailableUnitOf("MM#####M#M%^:SS#$&*"))
  }
  
  @Test
  fun smallestAvailableUnitTestWithEscaping6() {
    assertSame(R_MILLISECONDS, smallestAvailableUnitOf(":SS#S==:##LL::#$&*"))
  }
  
  @Test
  fun smallestAvailableUnitTestWithEscaping7() {
    assertSame(SECONDS, smallestAvailableUnitOf("#S#SS#S#S"))
  }
  
  @Test
  fun largestAvailableUnitTest1() {
    assertSame(MINUTES, largestAvailableUnitOf("MM:SS:LL"))
  }
  
  @Test
  fun largestAvailableUnitTest2() {
    assertSame(HOURS, largestAvailableUnitOf("HH:MM:SS:LL"))
  }
  
  @Test
  fun largestAvailableUnitTest3() {
    assertSame(SECONDS, largestAvailableUnitOf("__SS:LL"))
  }
  
  @Test
  fun largestAvailableUnitTestWithEscaping1() {
    assertSame(MINUTES, largestAvailableUnitOf("#H#H:MM:SS"))
  }
  
  @Test
  fun largestAvailableUnitTestWithEscaping2() {
    assertSame(MINUTES, largestAvailableUnitOf("#MM:SS:LL"))
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
  
  private fun smallestAvailableUnitOf(format: String): TimeUnitType {
    return analyze(format).smallestAvailableUnit
  }
  
  private fun largestAvailableUnitOf(format: String): TimeUnitType {
    return analyze(format).largestAvailableUnit
  }
  
  private fun analyze(format: String): SemanticImpl {
    return AnalyzerImpl.analyze(TestHelper.updateFormatIfNeeded(format))
  }
}
