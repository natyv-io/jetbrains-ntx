package dev.natyv.ntx

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

// A Kotlin `object` compiles to a singleton with an auto-generated static
// `INSTANCE` field -- exactly what plugin.xml's <fileType fieldName="INSTANCE">
// binds to, no hand-written field needed. Mirrors intellij-ntss's own
// NtssFileType.kt exactly.
object NtxFileType : LanguageFileType(NtxLanguage) {
    override fun getName(): String = "ntx"
    override fun getDescription(): String = "natyv Markup"
    override fun getDefaultExtension(): String = "ntx"
    override fun getIcon(): Icon? = null
}
