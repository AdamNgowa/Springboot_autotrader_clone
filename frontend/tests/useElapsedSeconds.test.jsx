import { act, renderHook } from "@testing-library/react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useElapsedSeconds } from "../src/hooks/useElapsedSeconds";

describe("useElapsedSeconds", () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it("stays at zero while inactive", () => {
    const { result } = renderHook(() => useElapsedSeconds(false));

    act(() => vi.advanceTimersByTime(5000));

    expect(result.current).toBe(0);
  });

  it("counts whole seconds while active", () => {
    const { result } = renderHook(() => useElapsedSeconds(true));

    act(() => vi.advanceTimersByTime(3000));

    expect(result.current).toBe(3);
  });

  it("resets to zero when it becomes inactive", () => {
    const { result, rerender } = renderHook(
      ({ active }) => useElapsedSeconds(active),
      { initialProps: { active: true } },
    );

    act(() => vi.advanceTimersByTime(4000));
    expect(result.current).toBe(4);

    rerender({ active: false });

    expect(result.current).toBe(0);
  });
});
