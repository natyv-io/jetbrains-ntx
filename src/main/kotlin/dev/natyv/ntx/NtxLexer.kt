package dev.natyv.ntx

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

/**
 * Hand-written lexer giving `.ntx` files real base coloring for the raw
 * Go code they're made of -- comments, string/rune literals, keywords,
 * numbers -- mirroring `NtssLexer.kt`'s own proven `LexerBase` shape.
 *
 * Deliberately stateless and flat across the whole file: `<`/`>`/`/`/`=`
 * are shared by natyv's own tag syntax and Go's real operators, so this
 * lexer never tries to tell them apart (same posture vscode-ntx/zed-ntx
 * already established -- tag/attribute-specific coloring is left entirely
 * to the LSP's own semantic tokens, Stage 3). No brace/tag-depth tracking
 * is needed for that reason; only real Go string/comment boundaries need
 * to be scanned correctly so their contents don't get mis-tokenized.
 */
class NtxLexer : LexerBase() {
    private var buffer: CharSequence = ""
    private var bufferEnd: Int = 0
    private var pos: Int = 0
    private var tokenStart: Int = 0
    private var tokenEnd: Int = 0
    private var tokenType: IElementType? = null

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.bufferEnd = endOffset
        this.pos = startOffset
        scanNextToken()
    }

    override fun getState(): Int = 0
    override fun getTokenType(): IElementType? = tokenType
    override fun getTokenStart(): Int = tokenStart
    override fun getTokenEnd(): Int = tokenEnd
    override fun advance() {
        scanNextToken()
    }

    override fun getBufferSequence(): CharSequence = buffer
    override fun getBufferEnd(): Int = bufferEnd

    private fun isIdentStart(c: Char): Boolean = c.isLetter() || c == '_'
    private fun isIdentCont(c: Char): Boolean = c.isLetterOrDigit() || c == '_'

    private fun scanNextToken() {
        tokenStart = pos
        if (pos >= bufferEnd) {
            tokenType = null
            tokenEnd = pos
            return
        }
        val c = buffer[pos]

        if (c.isWhitespace()) {
            while (pos < bufferEnd && buffer[pos].isWhitespace()) pos++
            tokenType = TokenType.WHITE_SPACE
            tokenEnd = pos
            return
        }

        if (c == '/' && pos + 1 < bufferEnd && buffer[pos + 1] == '/') {
            while (pos < bufferEnd && buffer[pos] != '\n') pos++
            tokenType = NtxTokenTypes.COMMENT
            tokenEnd = pos
            return
        }

        if (c == '/' && pos + 1 < bufferEnd && buffer[pos + 1] == '*') {
            pos += 2
            while (pos + 1 < bufferEnd && !(buffer[pos] == '*' && buffer[pos + 1] == '/')) pos++
            pos = if (pos + 1 < bufferEnd) pos + 2 else bufferEnd
            tokenType = NtxTokenTypes.COMMENT
            tokenEnd = pos
            return
        }

        if (c == '"') {
            pos++
            while (pos < bufferEnd && buffer[pos] != '"' && buffer[pos] != '\n') {
                if (buffer[pos] == '\\' && pos + 1 < bufferEnd) pos++
                pos++
            }
            if (pos < bufferEnd && buffer[pos] == '"') pos++
            tokenType = NtxTokenTypes.STRING
            tokenEnd = pos
            return
        }

        // Raw string -- no escapes at all, real Go semantics: runs
        // verbatim until the next backtick, including real newlines.
        if (c == '`') {
            pos++
            while (pos < bufferEnd && buffer[pos] != '`') pos++
            if (pos < bufferEnd) pos++
            tokenType = NtxTokenTypes.STRING
            tokenEnd = pos
            return
        }

        if (c == '\'') {
            pos++
            while (pos < bufferEnd && buffer[pos] != '\'' && buffer[pos] != '\n') {
                if (buffer[pos] == '\\' && pos + 1 < bufferEnd) pos++
                pos++
            }
            if (pos < bufferEnd && buffer[pos] == '\'') pos++
            tokenType = NtxTokenTypes.RUNE
            tokenEnd = pos
            return
        }

        if (isIdentStart(c)) {
            while (pos < bufferEnd && isIdentCont(buffer[pos])) pos++
            tokenEnd = pos
            val word = buffer.subSequence(tokenStart, tokenEnd).toString()
            tokenType = when {
                word in NATYV_KEYWORDS -> NtxTokenTypes.NATYV_KEYWORD
                word in GO_KEYWORDS -> NtxTokenTypes.KEYWORD
                else -> NtxTokenTypes.IDENTIFIER
            }
            return
        }

        // Numbers: decimal/hex(0x)/octal(0o)/binary(0b), a real fractional
        // part, and a real exponent -- deliberately lexical-only (no
        // validation that the result is a well-formed literal, matching
        // this file's own "classify for coloring, don't parse" scope).
        if (c.isDigit()) {
            pos++
            if (c == '0' && pos < bufferEnd && (buffer[pos] == 'x' || buffer[pos] == 'X' ||
                    buffer[pos] == 'o' || buffer[pos] == 'O' || buffer[pos] == 'b' || buffer[pos] == 'B')
            ) {
                pos++
            }
            while (pos < bufferEnd && (buffer[pos].isLetterOrDigit() || buffer[pos] == '_')) pos++
            if (pos < bufferEnd && buffer[pos] == '.') {
                pos++
                while (pos < bufferEnd && (buffer[pos].isDigit() || buffer[pos] == '_')) pos++
            }
            if (pos < bufferEnd && (buffer[pos] == 'e' || buffer[pos] == 'E')) {
                pos++
                if (pos < bufferEnd && (buffer[pos] == '+' || buffer[pos] == '-')) pos++
                while (pos < bufferEnd && buffer[pos].isDigit()) pos++
            }
            tokenType = NtxTokenTypes.NUMBER
            tokenEnd = pos
            return
        }

        // Everything else -- operators/punctuation, including the exact
        // characters natyv's own tag syntax also uses (`<`, `>`, `/`,
        // `=`) -- see this file's own doc comment for why no tag-vs-
        // operator disambiguation happens here.
        pos++
        tokenType = NtxTokenTypes.OPERATOR
        tokenEnd = pos
    }

    companion object {
        // Go's real, complete, stable reserved-word list (25 words, fixed
        // since Go 1.0) -- not guessed, matches the language spec exactly.
        private val GO_KEYWORDS = setOf(
            "break", "case", "chan", "const", "continue", "default", "defer",
            "else", "fallthrough", "for", "func", "go", "goto", "if",
            "import", "interface", "map", "package", "range", "return",
            "select", "struct", "switch", "type", "var",
        )
        private val NATYV_KEYWORDS = setOf("expose", "uses")
    }
}
