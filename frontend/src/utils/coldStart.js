// How long a request must take before we explain why.
export const SHOW_AFTER_SECONDS = 4;

// Roughly how long a free Render instance needs to wake up.
export const EXPECTED_SECONDS = 60;

// Each stage starts at `from` seconds and stays until the next one begins.
const STAGES = [
  { from: 4, text: "Turning the key..." },
  {
    from: 8,
    text: "Engine's cold. Our free server naps when nobody's visiting.",
  },
  { from: 14, text: "Checking the oil. Kicking the tires." },
  { from: 20, text: "Arguing with the GPS about the shortest route." },
  {
    from: 28,
    text: "Pumping the pedal. It's a free server, not a Ferrari.",
  },
  {
    from: 38,
    text: "Warming up. The first start is slow, every drive after is quick.",
  },
  { from: 50, text: "Final stretch. The server is rolling out of the garage." },
  { from: 65, text: "Taking longer than usual. Try reloading the page." },
];

// Returns the stage to show, or null while the wait is still short.
export function getStage(elapsedSeconds) {
  if (elapsedSeconds < SHOW_AFTER_SECONDS) {
    return null;
  }

  let current = STAGES[0];

  for (const stage of STAGES) {
    if (elapsedSeconds >= stage.from) {
      current = stage;
    }
  }

  return current;
}

// 0-95. Capped below 100 so the car never "finishes" before the data arrives.
export function getProgress(elapsedSeconds) {
  return Math.min(elapsedSeconds / EXPECTED_SECONDS, 0.95) * 100;
}

// 75 -> "1:15"
export function formatElapsed(totalSeconds) {
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = String(totalSeconds % 60).padStart(2, "0");

  return `${minutes}:${seconds}`;
}
