#!/usr/bin/env bash

set -euo pipefail

API_KEY_FILE="${HOME}/.chatgpt/api.key"
OUTPUT_FILE="speech.mp3"
MODEL="gpt-4o-mini-tts"
VOICE="alloy"

# --- Validate input ---
if [ "$#" -eq 0 ]; then
  echo "Usage: $0 \"Text to speak\""
  exit 1
fi

TEXT="$*"

# --- Read API key safely (strip newline) ---
API_KEY=$(tr -d '\n' < "${API_KEY_FILE}")

# --- Call OpenAI TTS ---
curl https://api.openai.com/v1/audio/speech \
  -sS \
  -H "Authorization: Bearer ${API_KEY}" \
  -H "Content-Type: application/json" \
  --data "$(jq -n \
    --arg model "${MODEL}" \
    --arg voice "${VOICE}" \
    --arg input "${TEXT}" \
    '{model: $model, voice: $voice, input: $input}')" \
  --output "${OUTPUT_FILE}"

echo "Saved to ${OUTPUT_FILE}"