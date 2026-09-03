package dev.natyv.ntx

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServerSupportProvider
import com.intellij.platform.lsp.api.LspServerSupportProvider.LspServerStarter

// Real, minimal LSP wiring -- Stage 1 of
// ~/.claude/plans/lexical-wishing-penguin.md's JetBrains plan. Same scope
// as vscode-ntx/zed-ntx's own Stage 1: registers no feature providers of
// its own, exists only to prove the platform can launch and handshake
// with ntx-lsp over stdio. Mirrors the real Kotlin shape JetBrains' own
// plugin SDK docs show for LspServerSupportProvider.
internal class NtxLspServerSupportProvider : LspServerSupportProvider {
    override fun fileOpened(project: Project, file: VirtualFile, serverStarter: LspServerStarter) {
        if (file.fileType == NtxFileType) {
            serverStarter.ensureServerStarted(NtxLspServerDescriptor(project))
        }
    }
}
