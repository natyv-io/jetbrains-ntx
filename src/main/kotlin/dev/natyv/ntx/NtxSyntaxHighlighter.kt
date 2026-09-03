package dev.natyv.ntx

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class NtxSyntaxHighlighter : SyntaxHighlighterBase() {
    companion object {
        val COMMENT: TextAttributesKey = createTextAttributesKey("NTX_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val STRING: TextAttributesKey = createTextAttributesKey("NTX_STRING", DefaultLanguageHighlighterColors.STRING)
        val RUNE: TextAttributesKey = createTextAttributesKey("NTX_RUNE", DefaultLanguageHighlighterColors.STRING)
        val NUMBER: TextAttributesKey = createTextAttributesKey("NTX_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val KEYWORD: TextAttributesKey = createTextAttributesKey("NTX_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val NATYV_KEYWORD: TextAttributesKey = createTextAttributesKey("NTX_NATYV_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val IDENTIFIER: TextAttributesKey = createTextAttributesKey("NTX_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
        val OPERATOR: TextAttributesKey = createTextAttributesKey("NTX_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val BAD_CHARACTER: TextAttributesKey = createTextAttributesKey("NTX_BAD_CHARACTER", com.intellij.openapi.editor.colors.CodeInsightColors.ERRORS_ATTRIBUTES)

        private val EMPTY = emptyArray<TextAttributesKey>()
    }

    override fun getHighlightingLexer(): Lexer = NtxLexer()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        val key = when (tokenType) {
            NtxTokenTypes.COMMENT -> COMMENT
            NtxTokenTypes.STRING -> STRING
            NtxTokenTypes.RUNE -> RUNE
            NtxTokenTypes.NUMBER -> NUMBER
            NtxTokenTypes.KEYWORD -> KEYWORD
            NtxTokenTypes.NATYV_KEYWORD -> NATYV_KEYWORD
            NtxTokenTypes.IDENTIFIER -> IDENTIFIER
            NtxTokenTypes.OPERATOR -> OPERATOR
            NtxTokenTypes.BAD_CHARACTER, TokenType.BAD_CHARACTER -> BAD_CHARACTER
            else -> return EMPTY
        }
        return arrayOf(key)
    }
}
