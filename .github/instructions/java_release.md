---
applyTo: "**"
---

# Java Release Workflow

## When This Applies

Any time the user asks to release, publish, build, or deploy SignalShow-Java. Common phrasings:

- "build the java and publish a new release"
- "release a new version"
- "push and release"
- "deploy the java app"

## Overview

SignalShow-Java uses GitHub Actions to automatically build native Mac (.dmg) and Windows (.exe/.msi) installers whenever a version tag is pushed. The user always expects both platform installers to be available on GitHub as downloadable release assets.

## Release Steps (follow every time)

### 1. Bump the version in pom.xml

The version lives in `pom.xml` under `<version>`. Bump it appropriately:

- Bug fixes: patch bump (e.g., 1.2.3 → 1.2.4)
- New features: minor bump (e.g., 1.2.3 → 1.3.0)

The build scripts (`build-installer-mac.sh`, `build-installer-windows.sh`) extract the version from pom.xml automatically — no need to update them.

### 2. Compile and verify locally

```bash
mvn compile -q
```

If this fails, fix the errors before proceeding.

### 3. Commit and push to main

```bash
git add -A
git commit -m "v<VERSION>: <description>"
git push origin main
```

### 4. Create and push an annotated tag

The tag MUST match the pattern `v*` (e.g., `v1.2.4`) to trigger the CI workflow.

```bash
git tag -a v<VERSION> -m "v<VERSION>: <description>"
git push origin v<VERSION>
```

### 5. Direct the user to check the build

Always provide these exact instructions and links:

> The build is now running. Check the status here:
>
> **https://github.com/Nuthatch-Inc/SignalShow-Java/actions**
>
> Look for the "Build Native Installers" workflow run triggered by the `v<VERSION>` tag. It has three jobs:
>
> 1. **Build macOS Installer** — builds the .dmg file
> 2. **Build Windows Installer** — builds the .exe/.msi file
> 3. **Create GitHub Release** — creates a draft release with both installers attached
>
> Wait for all three jobs to show green checkmarks (this usually takes several minutes).

### 6. Direct the user to publish the release

Once the build succeeds, always provide these exact instructions:

> The build succeeded! Now publish the release:
>
> 1. Go to **https://github.com/Nuthatch-Inc/SignalShow-Java/releases**
> 2. You should see a **draft** release for `v<VERSION>`
> 3. Click the pencil icon (Edit) on the draft release
> 4. Review the release notes — edit if you want
> 5. Click **Publish release**
>
> Once published, the Mac .dmg and Windows installer will be available for download on that release page.

## If the Build Fails

- Check the failed job's logs at **https://github.com/Nuthatch-Inc/SignalShow-Java/actions**
- Click on the failed workflow run, then click the failed job to see the error
- Common issues:
  - **JAR file not found**: Check that pom.xml version matches what the build scripts expect (should be automatic now)
  - **Java version mismatch**: The CI uses Java 25 (Temurin). If pom.xml specifies a different version, update it
  - **Maven build failure**: Compilation errors — fix locally with `mvn compile`, commit, then re-tag

### Re-triggering after a fix

If you need to fix something and re-trigger the build:

```bash
# Delete the old tag locally and remotely
git tag -d v<VERSION>
git push origin :refs/tags/v<VERSION>

# Delete any draft release on GitHub for this version (user must do this manually on the website)

# Commit your fix, push, then create a new tag
git add -A
git commit -m "Fix: <description>"
git push origin main
git tag -a v<VERSION> -m "v<VERSION>: <description>"
git push origin v<VERSION>
```

Then direct the user to check the build status again (step 5).

## Important Notes

- **Always provide clickable links** — do not assume the user knows where to find things on GitHub
- **Always prompt the user to check the build** — never assume it will succeed
- **Always prompt the user to publish** — the workflow creates a DRAFT release, not a published one
- **Never skip the version bump** — the pom.xml version flows into the JAR filename, installer version, and release tag
- The workflow is defined in `.github/workflows/build-installers.yml`
- Build scripts are `build-installer-mac.sh` and `build-installer-windows.sh`
