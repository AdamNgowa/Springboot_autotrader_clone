import { describe, expect, it } from "vitest";

import {
  formatElapsed,
  getProgress,
  getStage,
  SHOW_AFTER_SECONDS,
} from "../src/utils/coldStart";

describe("getStage", () => {
  it("returns null while the wait is still short", () => {
    expect(getStage(0)).toBeNull();
    expect(getStage(SHOW_AFTER_SECONDS - 1)).toBeNull();
  });

  it("returns the first stage when the threshold is reached", () => {
    expect(getStage(SHOW_AFTER_SECONDS).text).toBe("Turning the key...");
  });

  it("advances through the stages as time passes", () => {
    expect(getStage(10).text).toMatch(/Engine's cold/);
    expect(getStage(21).text).toMatch(/GPS/);
  });

  it("stays on the last stage for very long waits", () => {
    expect(getStage(500).text).toMatch(/reloading/);
  });
});

describe("getProgress", () => {
  it("starts at zero", () => {
    expect(getProgress(0)).toBe(0);
  });

  it("never reaches 100 so the car cannot finish early", () => {
    expect(getProgress(60)).toBeLessThan(100);
    expect(getProgress(10_000)).toBeLessThan(100);
  });
});

describe("formatElapsed", () => {
  it("formats seconds as m:ss", () => {
    expect(formatElapsed(0)).toBe("0:00");
    expect(formatElapsed(7)).toBe("0:07");
    expect(formatElapsed(75)).toBe("1:15");
  });
});
