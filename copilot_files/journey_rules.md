# Branching and deployment

- `main` is the only permanent branch and must contain production-ready code.
- Develop each feature on a short-lived `feature/*` branch created from the latest `main`.
- Merge completed features into `main` through a pull request, then delete the merged feature
  branch.
- Do not create `develop`, `production`, `staging`, or other permanent branches.
- After implementing and verifying a change, deploy any affected Supabase components and update the
  local application preview without waiting for a separate deployment request.
- Deploy the production application only when the user explicitly requests it. Do not push to or
  merge into `main` as part of the routine Supabase and local deployments.
- Every push to `main` automatically runs `.github/workflows/deploy-kmp-features-pages.yml`.
- GitHub Actions builds the web application with
  `./gradlew :kmpFeatures:wasmJsBrowserDistribution --no-daemon`.
- GitHub Pages deploys only `kmpFeatures/build/dist/wasmJs/productionExecutable`.
- The `github-pages` environment permits deployments only from `main`.