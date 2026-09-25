# Repository Agent Instructions

The detailed Android and Kotlin Multiplatform rules for this repository are maintained in
`copilot_files/AGENTS.md`.

Read and follow that file before making code changes anywhere in this repository.

The core migration constraint is incremental isolation:

- Preserve the existing Android implementation unless migration is explicitly requested.
- Implement new KMP work separately in `kmpFeatures` and reusable KMP UI in `viewModule`.
- Keep shared Android/KMP model types in `kmpModels`, using the ownership and package rules in the
  detailed instructions.
- Prefer `commonMain`, with platform-specific code limited to the appropriate source set.
- Do not trigger broad Android-to-KMP refactors as a side effect of feature work.
