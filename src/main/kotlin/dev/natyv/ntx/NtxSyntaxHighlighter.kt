package dev.natyv.ntx

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import java.awt.Color

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

        // Real, confirmed-necessary: DefaultLanguageHighlighterColors.MARKUP_TAG/
        // MARKUP_ATTRIBUTE are semantically the right choice for LSP "type"/
        // "property" semantic tokens, but in the active "Dark Theme default"
        // scheme both are configured to *inherit* from Identifiers -> Default
        // (confirmed directly in Settings -> Editor -> Color Scheme -> Language
        // Defaults -> Markup -> Attribute/Tag: "Inherit values from" is checked),
        // i.e. they carry no literal color of their own in this scheme and render
        // identical to plain identifiers. Rather than depend on a base key whose
        // real configured value we don't control (and which apparently isn't
        // themed at all outside actual XML/HTML editing), these two keys carry
        // their own literal fallback TextAttributes so LSP semantic tokens are
        // guaranteed to render with a real, visible, distinct color regardless of
        // the active scheme -- the same reasoning real language plugins use for
        // custom semantic roles the base scheme was never designed to cover.
        val SEMANTIC_TYPE: TextAttributesKey = createTextAttributesKey(
            "NTX_SEMANTIC_TYPE",
            TextAttributes(Color(0xFF, 0xC6, 0x6D), null, null, null, java.awt.Font.PLAIN),
        )
        val SEMANTIC_PROPERTY: TextAttributesKey = createTextAttributesKey(
            "NTX_SEMANTIC_PROPERTY",
            TextAttributes(Color(0x9C, 0xDC, 0xFE), null, null, null, java.awt.Font.PLAIN),
        )

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
