// Mirrors intellij-ntss's own already-proven toolchain exactly (IntelliJ
// Platform Gradle Plugin 2.x + Gradle 9.7.1 + a JDK 17 toolchain) -- see
// that repo's own comment for why the legacy `org.jetbrains.intellij` 1.x
// plugin doesn't work against Gradle 9.
plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
    id("org.jetbrains.intellij.platform") version "2.9.0"
}

group = "dev.natyv"
version = "0.1.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Targets IntelliJ IDEA Community for the dev sandbox (runIde) since it's
// free and always resolvable -- same reasoning as intellij-ntss. The
// resulting plugin depends only on com.intellij.modules.platform (real
// language/LSP APIs, not GoLand-specific), so it installs into GoLand or
// any other IntelliJ Platform IDE identically; GoLand is the real
// verification target, this is just what the free dev sandbox runs.
dependencies {
    intellijPlatform {
        create("GO", "2023.3.6")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            // 233 (2023.3) is the earliest platform version with the real
            // LspServerSupportProvider API this plugin needs -- see
            // ~/.claude/plans/lexical-wishing-penguin.md's own research
            // notes on why the newer LspIntegrationProvider generation
            // (2026.1.4+) isn't used instead.
            sinceBuild = "233"
            untilBuild = "252.*"
        }
    }
}

kotlin {
    jvmToolchain(17)
}
