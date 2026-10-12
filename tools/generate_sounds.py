"""Generate original, compact mono OGG effects for Soundify Reimagined.
These deliberately synthetic placeholder sounds can be replaced with licensed OGGs.
Requires numpy, scipy, and ffmpeg on PATH.
"""
from pathlib import Path
import subprocess
import tempfile
import numpy as np
from scipy.signal import butter, sosfiltfilt
from scipy.io.wavfile import write as write_wav

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "src/main/resources/assets/soundify/sounds"
SR = 22050


def env(n, attack=0.008, decay=0.08, sustain=0.0):
    t = np.arange(n) / SR
    a = np.minimum(1.0, t / max(attack, 1e-4))
    d = np.exp(-t / max(decay, 1e-4))
    return a * (sustain + (1.0 - sustain) * d)


def sweep(duration, f0, f1, amp=1.0, shape="sine", decay=None, seed=1):
    n = max(1, int(SR * duration))
    t = np.arange(n) / SR
    freqs = np.linspace(f0, f1, n)
    phase = 2 * np.pi * np.cumsum(freqs) / SR
    if shape == "square":
        x = np.sign(np.sin(phase))
    elif shape == "triangle":
        x = 2 / np.pi * np.arcsin(np.sin(phase))
    else:
        x = np.sin(phase)
    x *= env(n, min(0.01, duration / 6), decay or max(0.03, duration / 2))
    return amp * x


def noise(duration, amp=1.0, cutoff=5000, high=False, seed=1, decay=0.08):
    n = max(1, int(SR * duration))
    rng = np.random.default_rng(seed)
    x = rng.normal(0, 1, n)
    cutoff = max(50, min(cutoff, SR / 2 - 100))
    kind = "highpass" if high else "lowpass"
    sos = butter(3, cutoff, btype=kind, fs=SR, output="sos")
    x = sosfiltfilt(sos, x)
    x *= env(n, 0.001, decay)
    x /= max(1e-9, np.max(np.abs(x)))
    return amp * x


def mix(*parts):
    n = max(map(len, parts))
    out = np.zeros(n, dtype=np.float64)
    for p in parts:
        out[:len(p)] += p
    peak = max(1e-8, np.max(np.abs(out)))
    if peak > 0.96:
        out *= 0.96 / peak
    return out


def pad(arr, duration):
    n = int(SR * duration)
    if len(arr) >= n:
        return arr[:n]
    return np.pad(arr, (0, n-len(arr)))


def footsteps(seed, low, bright, duration=0.19):
    body = noise(duration, 0.76, low, False, seed, 0.06)
    scrape = noise(duration * 0.72, 0.28, bright, True, seed+90, 0.035)
    thud = sweep(duration * 0.65, 105, 55, 0.45, "sine", 0.06, seed)
    return mix(body, scrape, thud)


def door(seed, closing=False):
    dur = 0.44
    creak = sweep(0.30, 210 if not closing else 165, 430 if not closing else 115, 0.38, "triangle", 0.24, seed)
    rub = noise(0.34, 0.43, 1800, True, seed+1, 0.18)
    end_raw = sweep(0.14, 82, 48, 0.35, "sine", 0.08, seed+2)
    end = np.pad(end_raw, (0, max(0, int(SR*dur) - len(end_raw))))
    return mix(creak, pad(rub, dur), end)


def chest(seed, closing=False):
    dur = 0.31
    thunk = sweep(0.17, 125 if not closing else 95, 70 if not closing else 52, 0.58, "sine", 0.09, seed)
    wood = noise(0.20, 0.42, 900, False, seed+1, 0.08)
    resonance = sweep(0.25, 250, 140, 0.18, "triangle", 0.15, seed+2)
    return mix(thunk, wood, resonance)


SOUNDS = {}
SOUNDS["combat_hit"] = mix(sweep(0.18, 180, 66, 0.70, "sine", 0.08), noise(0.14, 0.52, 3200, True, 10, 0.045))
SOUNDS["combat_crit"] = mix(sweep(0.12, 900, 1450, 0.62, "triangle", 0.07), sweep(0.23, 1450, 600, 0.38, "sine", 0.15, 2), noise(0.08, 0.18, 7000, True, 3, 0.03))
SOUNDS["combat_sweep"] = mix(noise(0.28, 0.72, 4500, True, 4, 0.15), sweep(0.25, 500, 155, 0.28, "sine", 0.15, 5))
SOUNDS["damage"] = mix(sweep(0.23, 130, 48, 0.66, "sine", 0.11), noise(0.15, 0.35, 1700, False, 8, 0.08))
SOUNDS["death_sting"] = mix(sweep(0.55, 310, 125, 0.55, "triangle", 0.45), sweep(0.42, 230, 97, 0.34, "sine", 0.34, 2), noise(0.2, 0.14, 1200, False, 6, 0.2))
heartbeat_first = sweep(0.22, 95, 42, 0.65, "sine", 0.12)
SOUNDS["heartbeat"] = mix(np.pad(heartbeat_first, (0, max(0, int(SR*0.31) - len(heartbeat_first)))), np.pad(sweep(0.18, 82, 39, 0.55, "sine", 0.11, 2), (int(SR*0.28), 0)))
SOUNDS["level_up"] = mix(sweep(0.16, 520, 660, 0.44, "sine", 0.11), np.pad(sweep(0.18, 660, 790, 0.43, "sine", 0.13, 2), (int(SR*0.11), 0)), np.pad(sweep(0.28, 790, 1040, 0.50, "sine", 0.22, 3), (int(SR*0.22), 0)))
SOUNDS["ui_click"] = mix(noise(0.045, 0.62, 6500, True, 11, 0.025), sweep(0.075, 1250, 820, 0.50, "sine", 0.045, 12))
SOUNDS["ui_hover"] = mix(sweep(0.055, 920, 1080, 0.43, "sine", 0.04, 13), noise(0.025, 0.12, 5500, True, 14, 0.02))
SOUNDS["item_pickup"] = mix(sweep(0.12, 640, 980, 0.48, "sine", 0.09, 21), np.pad(sweep(0.12, 980, 1280, 0.35, "sine", 0.09, 22), (int(SR*0.05), 0)))
SOUNDS["xp_pickup"] = mix(sweep(0.12, 1050, 1550, 0.38, "sine", 0.08, 23), np.pad(sweep(0.18, 1550, 1880, 0.30, "sine", 0.14, 24), (int(SR*0.06), 0)))
SOUNDS["armor_equip"] = mix(noise(0.22, 0.42, 3300, True, 31, 0.08), sweep(0.26, 700, 390, 0.38, "sine", 0.19, 32), sweep(0.17, 1120, 650, 0.24, "sine", 0.12, 33))
SOUNDS["item_use"] = mix(noise(0.10, 0.28, 4200, True, 41, 0.06), sweep(0.11, 430, 870, 0.4, "sine", 0.08, 42))
SOUNDS["eat"] = mix(noise(0.20, 0.76, 2700, False, 51, 0.11), noise(0.08, 0.32, 6000, True, 52, 0.045), sweep(0.13, 180, 90, 0.20, "sine", 0.09, 53))
SOUNDS["drink"] = mix(noise(0.26, 0.44, 1250, False, 61, 0.15), sweep(0.24, 230, 340, 0.26, "sine", 0.18, 62), np.pad(sweep(0.09, 450, 720, 0.18, "sine", 0.07, 63), (int(SR*0.17),0)))
SOUNDS["footstep_grass"] = footsteps(70, 650, 3000)
SOUNDS["footstep_stone"] = mix(footsteps(71, 1250, 5000), sweep(0.13, 230, 135, 0.27, "sine", 0.075, 72))
SOUNDS["footstep_wood"] = mix(footsteps(73, 900, 2600), sweep(0.13, 170, 90, 0.32, "triangle", 0.08, 74))
SOUNDS["footstep_sand"] = noise(0.22, 0.83, 1600, False, 75, 0.10)
SOUNDS["footstep_gravel"] = mix(noise(0.21, 0.63, 3700, True, 76, 0.12), noise(0.12, 0.27, 1500, False, 77, 0.08))
SOUNDS["footstep_snow"] = mix(noise(0.22, 0.75, 1150, False, 78, 0.12), sweep(0.15, 120, 72, 0.22, "sine", 0.08, 79))
SOUNDS["footstep_metal"] = mix(sweep(0.23, 500, 180, 0.44, "sine", 0.13, 80), sweep(0.19, 940, 540, 0.32, "sine", 0.11, 81), noise(0.11, 0.34, 3600, True, 82, 0.05))
SOUNDS["chest_open"] = chest(91, False)
SOUNDS["chest_close"] = chest(92, True)
SOUNDS["door_open"] = door(101, False)
SOUNDS["door_close"] = door(102, True)
SOUNDS["block_break"] = mix(noise(0.24, 0.74, 4200, True, 111, 0.12), sweep(0.18, 190, 55, 0.4, "sine", 0.09, 112), noise(0.11, 0.30, 1800, False, 113, 0.07))
SOUNDS["metal_clank"] = mix(noise(0.20, 0.43, 5000, True, 121, 0.11), sweep(0.35, 730, 290, 0.5, "sine", 0.27, 122), sweep(0.29, 1170, 430, 0.25, "sine", 0.23, 123))
SOUNDS["cave_ambience"] = mix(sweep(1.7, 68, 54, 0.18, "sine", 1.6, 131), sweep(1.2, 130, 98, 0.12, "sine", 1.1, 132), noise(1.55, 0.12, 380, False, 133, 1.3))
SOUNDS["water_splash"] = mix(noise(0.34, 0.73, 3200, False, 141, 0.15), noise(0.22, 0.34, 7000, True, 142, 0.09), sweep(0.26, 430, 110, 0.24, "sine", 0.17, 143))

OUT.mkdir(parents=True, exist_ok=True)
for name, audio in SOUNDS.items():
    audio = np.asarray(audio, dtype=np.float64)
    # Add a short fade-in/out to avoid clicks at the boundaries.
    fade_n = min(int(0.008 * SR), len(audio) // 3)
    if fade_n:
        audio[:fade_n] *= np.linspace(0.0, 1.0, fade_n)
        audio[-fade_n:] *= np.linspace(1.0, 0.0, fade_n)
    peak = max(1e-9, np.max(np.abs(audio)))
    audio = np.clip(audio / peak * 0.78, -1.0, 1.0)
    pcm = (audio * 32767).astype(np.int16)
    with tempfile.NamedTemporaryFile(suffix=".wav", delete=False) as tmp:
        wav_path = Path(tmp.name)
    write_wav(wav_path, SR, pcm)
    out_path = OUT / f"{name}.ogg"
    subprocess.run([
        "ffmpeg", "-hide_banner", "-loglevel", "error", "-y", "-i", str(wav_path),
        "-ac", "1", "-ar", str(SR), "-c:a", "libvorbis", "-q:a", "3", str(out_path)
    ], check=True)
    wav_path.unlink(missing_ok=True)
    print(f"wrote {out_path.name}")
