import { useEffect, useState } from "react";

// Counts whole seconds while `active` is true, and resets to 0 when it turns false.
export function useElapsedSeconds(active) {
  const [elapsed, setElapsed] = useState(0);

  useEffect(() => {
    if (!active) {
      return;
    }

    const startedAt = Date.now();

    // Measure from a real timestamp instead of adding 1 per tick, so a
    // throttled background tab doesn't make the counter run slow.
    const intervalId = setInterval(() => {
      setElapsed(Math.floor((Date.now() - startedAt) / 1000));
    }, 1000);

    return () => {
      clearInterval(intervalId);
      setElapsed(0);
    };
  }, [active]);

  return elapsed;
}
