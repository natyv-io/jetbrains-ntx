package dev.natyv.ntx

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import com.intellij.platform.lsp.api.customization.LspSemanticTokensSupport
import dev.natyv.ntx.NtxSyntaxHighlighter.Companion.SEMANTIC_PROPERTY
import dev.natyv.ntx.NtxSyntaxHighlighter.Companion.SEMANTIC_TYPE

// `ntx-lsp` isn't published/on PATH via any package manager yet (see the
// Homebrew tap's own memory -- ntx-lsp deliberately doesn't ship there,
// it belongs in each editor's own marketplace instead), so a plain PATH
// lookup via GeneralCommandLine("ntx-lsp") is the right default for now --
// mirrors zed-ntx's own worktree.which("ntx-lsp") posture exactly, just
// this platform's own idiom for the same thing. No "--stdio" or other
// flag is needed: ntx-lsp always communicates over real stdio
// unconditionally, confirmed directly from its own main.zig.
internal class NtxLspServerDescriptor(project: Project) :
    ProjectWideLspServerDescriptor(project, "ntx-lsp") {

    override fun isSupportedFile(file: VirtualFile): Boolean = file.fileType == NtxFileType

    override fun createCommandLine(): GeneralCommandLine = GeneralCommandLine("ntx-lsp")

    // Real, confirmed-necessary override: a real semanticTokens/full
    // response for "type" tokens (tag names) came back correctly decoded
    // (verified directly against the raw LSP wire data -- Container/Label/
    // Button all present, correct offsets) but rendered identically to a
    // plain identifier -- confirmed via a temporary debug log that the
    // override itself *was* being called correctly with the right
    // tokenType each time. Two candidates were tried and rejected before
    // landing here, both for the same underlying reason: CLASS_NAME, then
    // MARKUP_TAG/MARKUP_ATTRIBUTE, all rendered with no visible distinct
    // color in the active "Dark Theme default" scheme. Root-caused directly
    // in Settings -> Editor -> Color Scheme -> Language Defaults -> Markup:
    // both Tag and Attribute have "Inherit values from: Identifiers ->
    // Default" checked, i.e. they carry no literal color of their own in
    // this scheme (or, it turns out, apparently in most non-XML-oriented
    // dark schemes) -- a real, benign, theme-specific fact, not a bug in
    // this override. Fix: use this plugin's own SEMANTIC_TYPE/
    // SEMANTIC_PROPERTY keys (see NtxSyntaxHighlighter.kt), which carry a
    // literal fallback TextAttributes so they're guaranteed to render with a
    // real, visible, distinct color regardless of the active scheme's own
    // (apparently unreliable) styling for markup-role keys. Matches the
    // exact same legend ntx-lsp's own SemanticTokens.zig declares
    // ("type"/"property"/"string").
    override val lspSemanticTokensSupport = object : LspSemanticTokensSupport() {
        override fun getTextAttributesKey(tokenType: String, modifiers: List<String>): TextAttributesKey =
            when (tokenType) {
                "type" -> SEMANTIC_TYPE
                "property" -> SEMANTIC_PROPERTY
                "string" -> DefaultLanguageHighlighterColors.STRING
                else -> DefaultLanguageHighlighterColors.IDENTIFIER
            }
    }
}
