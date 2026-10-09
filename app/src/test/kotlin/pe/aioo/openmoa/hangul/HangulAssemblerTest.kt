package pe.aioo.openmoa.hangul

import org.junit.Assert
import org.junit.Test

class HangulAssemblerTest {

    @Test
    fun testAssembleSimpleHangul() {
        val assembler = HangulAssembler()
        Assert.assertNull(assembler.appendJamo("ㄴ"))
        Assert.assertEquals("ㄴ", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㅡ"))
        Assert.assertEquals("느", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㆍ"))
        Assert.assertEquals("누", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㆍ"))
        Assert.assertEquals("뉴", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㅇ"))
        Assert.assertEquals("늉", assembler.getUnresolved())
        Assert.assertEquals("늉", assembler.appendJamo("ㄱ"))
        Assert.assertEquals("ㄱ", assembler.getUnresolved())
    }

    @Test
    fun testAssembleSimpleHangulWithAraea() {
        val assembler = HangulAssembler()
        Assert.assertNull(assembler.appendJamo("ㄴ"))
        Assert.assertEquals("ㄴ", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㆍ"))
        Assert.assertEquals("ㄴㆍ", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㆍ"))
        Assert.assertEquals("ㄴᆢ", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㅣ"))
        Assert.assertEquals("녀", assembler.getUnresolved())
    }

    @Test
    fun testAssembleComplexHangul() {
        val assembler = HangulAssembler()
        Assert.assertNull(assembler.appendJamo("ㅂ"))
        Assert.assertEquals("ㅂ", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㅜ"))
        Assert.assertEquals("부", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㆍ"))
        Assert.assertEquals("뷰", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㅣ"))
        Assert.assertEquals("붜", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㅣ"))
        Assert.assertEquals("붸", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㄹ"))
        Assert.assertEquals("뷀", assembler.getUnresolved())
        Assert.assertNull(assembler.appendJamo("ㄱ"))
        Assert.assertEquals("뷁", assembler.getUnresolved())
        Assert.assertEquals("뷁", assembler.appendJamo("ㄱ"))
        Assert.assertEquals("ㄱ", assembler.getUnresolved())
    }

    @Test
    fun testDisassembleJongSeong() {
        val assembler = HangulAssembler()
        Assert.assertNull(assembler.appendJamo("ㅂ"))
        Assert.assertNull(assembler.appendJamo("ㅜ"))
        Assert.assertNull(assembler.appendJamo("ㆍ"))
        Assert.assertNull(assembler.appendJamo("ㅣ"))
        Assert.assertNull(assembler.appendJamo("ㅣ"))
        Assert.assertNull(assembler.appendJamo("ㄹ"))
        Assert.assertNull(assembler.appendJamo("ㄱ"))
        Assert.assertEquals("뷀", assembler.appendJamo("ㅣ"))
        Assert.assertEquals("기", assembler.getUnresolved())
    }

    @Test
    fun testDisassembleJongSeongWithAraea() {
        val assembler = HangulAssembler()
        Assert.assertNull(assembler.appendJamo("ㅂ"))
        Assert.assertNull(assembler.appendJamo("ㅜ"))
        Assert.assertNull(assembler.appendJamo("ㆍ"))
        Assert.assertNull(assembler.appendJamo("ㅣ"))
        Assert.assertNull(assembler.appendJamo("ㅣ"))
        Assert.assertNull(assembler.appendJamo("ㄹ"))
        Assert.assertNull(assembler.appendJamo("ㄱ"))
        Assert.assertEquals("뷀", assembler.appendJamo("ㆍ"))
        Assert.assertEquals("ㄱㆍ", assembler.getUnresolved())
    }

    @Test
    fun testBackspaceDeletesFreshSyllable() {
        val assembler = HangulAssembler()
        assembler.appendJamo("ㄴ")
        assembler.appendJamo("ㅏ")
        Assert.assertEquals("나", assembler.getUnresolved())
        assembler.backspace()
        Assert.assertNull(assembler.getUnresolved())
        assembler.backspace()
        Assert.assertNull(assembler.getUnresolved())
    }

    @Test
    fun testRemoveLastJamoWithJongseong() {
        val assembler = HangulAssembler()
        assembler.appendJamo("ㄴ")
        assembler.appendJamo("ㅏ")
        assembler.appendJamo("ㄴ")
        Assert.assertEquals("난", assembler.getUnresolved())
        assembler.backspace()
        Assert.assertEquals("나", assembler.getUnresolved())
        assembler.backspace()
        Assert.assertEquals("ㄴ", assembler.getUnresolved())
        assembler.backspace()
        Assert.assertNull(assembler.getUnresolved())
    }

    @Test
    fun testRemoveLastJamoWithCompoundJongseong() {
        // "닭": ㄷ+ㅏ+ㄹ+ㄱ → ㄺ 합자 → 백스페이스 → "달" (ㄹ 유지, ㄱ만 제거)
        val assembler = HangulAssembler()
        assembler.appendJamo("ㄷ")
        assembler.appendJamo("ㅏ")
        assembler.appendJamo("ㄹ")
        assembler.appendJamo("ㄱ")
        Assert.assertEquals("닭", assembler.getUnresolved())
        assembler.backspace()
        Assert.assertEquals("달", assembler.getUnresolved())
        assembler.backspace()
        Assert.assertEquals("다", assembler.getUnresolved())
        assembler.backspace()
        Assert.assertEquals("ㄷ", assembler.getUnresolved())
        assembler.backspace()
        Assert.assertNull(assembler.getUnresolved())
    }

    @Test
    fun testRemoveLastJamoMoeumHistory() {
        // 받침을 지운 뒤에는 ㅜ→ㆍ→ㅠ→ㅣ→ㅝ 조합을 단계별로 복원
        val assembler = HangulAssembler()
        assembler.appendJamo("ㅂ")
        assembler.appendJamo("ㅜ")
        assembler.appendJamo("ㆍ") // ㅜ+ㆍ → ㅠ
        assembler.appendJamo("ㅣ") // ㅠ+ㅣ → ㅝ
        Assert.assertEquals("붜", assembler.getUnresolved())
        assembler.appendJamo("ㄱ")
        assembler.backspace()
        Assert.assertEquals("붜", assembler.getUnresolved())
        assembler.backspace()
        Assert.assertEquals("뷰", assembler.getUnresolved()) // ㅝ → ㅠ
        assembler.backspace()
        Assert.assertEquals("부", assembler.getUnresolved()) // ㅠ → ㅜ
        assembler.backspace()
        Assert.assertEquals("ㅂ", assembler.getUnresolved()) // ㅜ 제거
    }

    @Test
    fun testRemoveLastJamoDirectMoeum() {
        // 받침 없이 직접 입력한 ㅂ+ㅝ는 음절 전체를 한 번에 제거
        val assembler = HangulAssembler()
        assembler.appendJamo("ㅂ")
        assembler.appendJamo("ㅝ")
        Assert.assertEquals("붜", assembler.getUnresolved())
        assembler.backspace()
        Assert.assertNull(assembler.getUnresolved())
    }

    @Test
    fun testMoeumHistoryClearsOnResolution() {
        // 음절 확정 후 모음 이력이 초기화되어야 함
        val assembler = HangulAssembler()
        assembler.appendJamo("ㅂ")
        assembler.appendJamo("ㅜ")
        assembler.appendJamo("ㆍ") // ㅜ+ㆍ → ㅠ (history: ["ㅜ"])
        assembler.appendJamo("ㄱ") // 종성 추가 → "뷰" 유지
        assembler.appendJamo("ㅏ") // 새 모음 → "뷰ㄱ" 음절 확정, history 초기화
        Assert.assertEquals("가", assembler.getUnresolved())
        assembler.backspace()
        Assert.assertNull(assembler.getUnresolved()) // 새 음절은 한 번에 제거
    }

    @Test
    fun testAllFreshSyllablesWithoutJongseongDeleteTogether() {
        for (initial in "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ") {
            for (vowel in "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ") {
                val assembler = HangulAssembler()
                assembler.appendJamo(initial.toString())
                assembler.appendJamo(vowel.toString())
                assembler.backspace()
                Assert.assertNull("$initial + $vowel", assembler.getUnresolved())
            }
        }
    }

    @Test
    fun testFreshSyllableWithVowelHistoryDeletesTogether() {
        val assembler = HangulAssembler()
        listOf("ㅂ", "ㅜ", "ㆍ", "ㅣ").forEach { assembler.appendJamo(it) }
        Assert.assertEquals("붜", assembler.getUnresolved())
        assembler.backspace()
        Assert.assertNull(assembler.getUnresolved())
    }

    @Test
    fun testDirectCompoundVowelAfterDeletingJongseong() {
        val assembler = HangulAssembler()
        listOf("ㄱ", "ㅘ", "ㄴ").forEach { assembler.appendJamo(it) }
        Assert.assertEquals("관", assembler.getUnresolved())
        assembler.backspace()
        Assert.assertEquals("과", assembler.getUnresolved())
        assembler.backspace()
        Assert.assertEquals("ㄱ", assembler.getUnresolved())
        assembler.backspace()
        Assert.assertNull(assembler.getUnresolved())
    }

    @Test
    fun testDeletionStateResetsWhenNextSyllableStarts() {
        val assembler = HangulAssembler()
        listOf("ㄱ", "ㅏ", "ㄴ").forEach { assembler.appendJamo(it) }
        assembler.backspace() // 간 → 가: this syllable now decomposes step by step.
        assembler.appendJamo("ㄴ")
        Assert.assertEquals("가", assembler.appendJamo("ㅏ"))
        Assert.assertEquals("나", assembler.getUnresolved())
        assembler.backspace()
        Assert.assertNull(assembler.getUnresolved())
    }

    @Test
    fun testDeletionStateResetsOnAraeaResolution() {
        val assembler = HangulAssembler()
        listOf("ㄱ", "ㅏ", "ㄴ").forEach { assembler.appendJamo(it) }
        assembler.backspace()
        assembler.appendJamo("ㄴ")
        Assert.assertEquals("가", assembler.appendJamo("ㆍ"))
        assembler.appendJamo("ㅣ")
        Assert.assertEquals("너", assembler.getUnresolved())
        assembler.backspace()
        Assert.assertNull(assembler.getUnresolved())
    }

    @Test
    fun testDeletionStateResetsAfterClearOrFullDeletion() {
        for (finishWithClear in listOf(false, true)) {
            val assembler = HangulAssembler()
            listOf("ㄱ", "ㅏ", "ㄴ").forEach { assembler.appendJamo(it) }
            assembler.backspace()
            if (finishWithClear) {
                assembler.clear()
            } else {
                assembler.backspace()
                assembler.backspace()
            }
            assembler.appendJamo("ㄴ")
            assembler.appendJamo("ㅏ")
            assembler.backspace()
            Assert.assertNull(assembler.getUnresolved())
        }
    }

    @Test
    fun testStandaloneJamoAndIncompleteAraeaKeepOriginalDeletion() {
        val assembler = HangulAssembler()
        assembler.appendJamo("ㄱ")
        assembler.appendJamo("ㆍ")
        assembler.backspace()
        Assert.assertEquals("ㄱ", assembler.getUnresolved())
        assembler.backspace()
        Assert.assertNull(assembler.getUnresolved())
        assembler.appendJamo("ㅜ")
        assembler.appendJamo("ㆍ")
        Assert.assertEquals("ㅠ", assembler.getUnresolved())
        assembler.backspace()
        Assert.assertEquals("ㅜ", assembler.getUnresolved())
        assembler.backspace()
        Assert.assertNull(assembler.getUnresolved())
    }

    @Test
    fun testDeletionStateResetsOnUncombinableConsonant() {
        val assembler = HangulAssembler()
        listOf("ㄱ", "ㅏ", "ㄴ").forEach { assembler.appendJamo(it) }
        assembler.backspace()
        Assert.assertEquals("가", assembler.appendJamo("ㄸ"))
        assembler.appendJamo("ㅏ")
        Assert.assertEquals("따", assembler.getUnresolved())
        assembler.backspace()
        Assert.assertNull(assembler.getUnresolved())
    }
}
