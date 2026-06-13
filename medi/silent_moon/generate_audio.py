import wave
import struct
import math
import random
import os

# Ensure public directory exists
os.makedirs('public', exist_ok=True)

SAMPLE_RATE = 44100
MAX_AMP = 32767

def save_wav(filename, samples):
    with wave.open(filename, 'w') as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(SAMPLE_RATE)
        for s in samples:
            w.writeframesraw(struct.pack('<h', int(max(-1.0, min(1.0, s)) * MAX_AMP * 0.5)))

# 1. Singing Bowl (432Hz base with harmonics and binaural beating)
print("Generating bowl.wav...")
bowl_samples = []
duration = 60 # 60 seconds
for i in range(SAMPLE_RATE * duration):
    t = i / SAMPLE_RATE
    # Base tone and beating
    s1 = math.sin(2 * math.pi * 216 * t)  # Lower octave
    s2 = math.sin(2 * math.pi * 218 * t)  # Slow beating
    s3 = math.sin(2 * math.pi * 432 * t) * 0.5 # Harmonic
    
    # Very slow, relaxing breathing envelope (10s cycle)
    env = 0.6 + 0.4 * math.sin(2 * math.pi * 0.1 * t)
    bowl_samples.append((s1 + s2 + s3) * 0.3 * env)
save_wav('public/bowl.wav', bowl_samples)

# 2. Relaxing Ocean Waves (Pink-ish noise with slow swell)
print("Generating waves.wav...")
wave_samples = []
prev_val1 = 0
prev_val2 = 0
for i in range(SAMPLE_RATE * duration):
    t = i / SAMPLE_RATE
    noise = random.uniform(-1, 1)
    
    # 2-pole lowpass filter for deep ocean sound
    prev_val1 = prev_val1 + 0.01 * (noise - prev_val1)
    prev_val2 = prev_val2 + 0.01 * (prev_val1 - prev_val2)
    
    # Ocean wave crash envelope (approx 8 seconds per wave)
    env = 0.3 + 0.7 * (math.sin(2 * math.pi * (1/8) * t) ** 4)
    wave_samples.append(prev_val2 * 8.0 * env)
save_wav('public/waves.wav', wave_samples)

# 3. Deep Sleep Drone (108Hz low hum)
print("Generating sleep.wav...")
sleep_samples = []
for i in range(SAMPLE_RATE * duration):
    t = i / SAMPLE_RATE
    s1 = math.sin(2 * math.pi * 108 * t)
    s2 = math.sin(2 * math.pi * 109.5 * t) # Delta wave entrainment (1.5Hz beat)
    env = 0.8 + 0.2 * math.sin(2 * math.pi * 0.05 * t)
    sleep_samples.append((s1 + s2) * 0.4 * env)
save_wav('public/sleep.wav', sleep_samples)

print("Done.")
