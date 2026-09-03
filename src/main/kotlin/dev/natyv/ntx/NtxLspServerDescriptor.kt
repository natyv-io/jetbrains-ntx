package dev.natyv.ntx

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor

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
}
