# Soundify Reimagined — Fabric 1.21.11

An original, client-side sound overhaul prototype. The mod does not copy Soundify's code or audio. It ships original synthesized placeholder effects and replaces selected vanilla sound events at the client sound manager level.

## Features in this build

- **Combat:** new hit, critical-hit, sweep, damage, and death effects.
- **Low health:** a heartbeat alert when health falls to three hearts or below, with a cooldown to prevent spam.
- **Level-up:** a custom sound when the player's XP level increases.
- **Items:** alternate pickup, XP pickup, armor-equip, use, eating, and drinking effects.
- **Environment:** different synthesized footsteps for grass, stone, wood, sand, gravel, snow, and metal; chest and door open/close sounds; a stone-breaking effect, metallic anvil sound, cave ambience, and splashes.
- **Subtitles:** custom subtitle labels for the new sound events.
- **Client-only:** it is designed not to require installing the mod on a multiplayer server.
- **Config:** on first launch it creates `config/soundify.properties`. Set `enabled=false` to turn off all replacements, or disable categories such as `combat`, `items`, `footsteps`, `blocks`, or `ambience` individually. `low_health_alert` and `level_up_alert` control the extra alerts.

## Compatibility

- Minecraft Java Edition **1.21.11**
- Fabric Loader **0.18.5 or newer**
- Fabric API **0.141.1+1.21.11**
- Java **21**

## Build the JAR

This delivery is the **source project**; a compiled JAR has not been verified in this environment because Gradle/Minecraft dependencies could not be downloaded here.

With JDK 21 and Gradle 9.2.1 installed, run from this directory:

```sh
gradle build
```

The resulting mod should be in `build/libs/soundify-reimagined-1.0.0.jar`.

A GitHub Actions workflow is included at `.github/workflows/build.yml`. After placing the project in a GitHub repository, run the **Build Fabric mod** workflow and download the `soundify-reimagined-1.21.11` artifact from the workflow run.

## Install

Copy the compiled `soundify-reimagined-1.0.0.jar` into the `mods` folder of a Fabric 1.21.11 Java Edition instance with Fabric API installed. Test in a separate instance first.

## Audio note

The included 30 short OGG files are original, algorithmically synthesized placeholders, not field-recorded realistic foley. They make the prototype self-contained but are an obvious area to polish. To regenerate them, install Python with `numpy` and `scipy`, plus `ffmpeg`, then run:

```sh
python tools/generate_sounds.py
```

Audio files are mono OGG Vorbis at 22.05 kHz.

## Known limitations

- This is a focused prototype, not a one-to-one recreation of any proprietary mod or pack.
- It changes a curated set of sound event IDs rather than replacing every one of Minecraft's sounds.
- Sound overrides and Mixin compatibility have not been tested in a running Minecraft client in this environment.
