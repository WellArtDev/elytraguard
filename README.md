# ElytraGuard

Never lose an elytra mid-flight again. ElytraGuard watches your
chest-slot elytra and shows its remaining durability percentage right
under the crosshair. The moment 42 durability or fewer remain
(roughly your last safe seconds of gliding), the line turns red with
an "Elytra critical: N left!" warning and a deep alert thud plays
once.

## Features

- **Elytra durability HUD** — white "Elytra: NN%" text under the
  crosshair whenever you wear an elytra
- **Critical warning** — red "Elytra critical: N left!" line at 42
  durability or less, the last safe window before it snaps
- **Alert sound** — a deep bass thud fires once, so you hear it
  without being spammed
- **One-shot warning** — re-arms only when you swap the elytra out or
  repair it, never nags
- **Zero config** — drop it in and fly

## Install

1. Install [Fabric Loader](https://fabricmc.net/use/) 0.19.2+ for
   Minecraft 26.2
2. Install [Fabric API](https://modrinth.com/mod/fabric-api) for
   Minecraft 26.2
3. Drop `elytraguard-1.0.0.jar` into your `mods` folder
4. Launch and fly — no config needed

## Notes

- Client-side only. Works on any server (including vanilla).
- No mixins, no networking, nothing saved to disk.
- Pairs well with ArmorGuard (armor) and DurabilityPing (tools) —
  ElytraGuard covers your wings.

## License

MIT — see [LICENSE](LICENSE).
