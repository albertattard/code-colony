#!/usr/bin/env bash

set -euo pipefail

API_KEY_FILE="${HOME}/.chatgpt/api.key"
MODEL="gpt-4o-mini-tts"
VOICE="alloy"

# --- Validate input ---
if [ "$#" -lt 1 ] || [ "$#" -gt 2 ]; then
  echo "Usage: $0 <markdown-file> [output-file]"
  exit 1
fi

INPUT_FILE="$1"
OUTPUT_FILE="${2:-speech.mp3}"

if [ ! -f "${INPUT_FILE}" ]; then
  echo "File not found: ${INPUT_FILE}"
  exit 1
fi

TEXT="$(
  awk '
    /^##[[:space:]]+Briefing[[:space:]]*$/ { in_briefing=1; next }
    /^##[[:space:]]+/ && in_briefing { exit }
    in_briefing { print }
  ' "${INPUT_FILE}" \
  | sed -e 's/`//g' \
        -e 's/\*//g' \
  | tr '\n' ' ' \
  | sed -e 's/[[:space:]]\+/ /g' -e 's/^ //' -e 's/ $//'
)"

if [ -z "${TEXT}" ]; then
  echo "No ## Briefing section found in ${INPUT_FILE}"
  exit 1
fi

mkdir -p "$(dirname "${OUTPUT_FILE}")"

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
