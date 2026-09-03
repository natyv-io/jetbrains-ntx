package dev.natyv.ntx

import com.intellij.psi.tree.IElementType

class NtxTokenType(debugName: String) : IElementType(debugName, NtxLanguage)

// Deliberately minimal, mirroring vscode-ntx/zed-ntx's own established
// scope: real Go lexical categories (comments/strings/keywords/numbers)
// plus expose/uses -- nothing tag/attribute-specific here at all. Tag
// names and attribute names get their own real coloring from LSP semantic
// tokens (Stage 3), the same "grammar handles the host language, the LSP
// handles natyv's own markup semantics" split this whole editor-support
// arc already uses in VS Code/Zed. `<`/`>`/`/`/`=` are shared by markup
// syntax and Go's own operators, so they're just OPERATOR here -- no tag
// vs. comparison disambiguation happens at this layer, same as the other
// two editors' own grammars.
object NtxTokenTypes {
    val COMMENT = NtxTokenType("COMMENT")
    val STRING = NtxTokenType("STRING")
    val RUNE = NtxTokenType("RUNE")
    val NUMBER = NtxTokenType("NUMBER")
    val KEYWORD = NtxTokenType("KEYWORD")
    val NATYV_KEYWORD = NtxTokenType("NATYV_KEYWORD")
    val IDENTIFIER = NtxTokenType("IDENTIFIER")
    val OPERATOR = NtxTokenType("OPERATOR")
    val BAD_CHARACTER = NtxTokenType("BAD_CHARACTER")
}
