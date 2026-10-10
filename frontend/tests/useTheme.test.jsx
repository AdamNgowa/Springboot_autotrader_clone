import { act, renderHook } from "@testing-library/react";
import { beforeEach, describe, expect, it } from "vitest";

import { useTheme } from "../src/hooks/useTheme";

describe("useTheme", () => {
  beforeEach(() => {
    localStorage.clear();
    document.documentElement.classList.remove("dark");
  });

  it("starts light when the page has no dark class", () => {
    const { result } = renderHook(() => useTheme());

    expect(result.current.theme).toBe("light");
  });

  it("starts dark when index.html already added the dark class", () => {
    document.documentElement.classList.add("dark");

    const { result } = renderHook(() => useTheme());

    expect(result.current.theme).toBe("dark");
  });

  it("toggles the html class and remembers the choice", () => {
    const { result } = renderHook(() => useTheme());

    act(() => result.current.toggleTheme());

    expect(result.current.theme).toBe("dark");
    expect(document.documentElement.classList.contains("dark")).toBe(true);
    expect(localStorage.getItem("theme")).toBe("dark");

    act(() => result.current.toggleTheme());

    expect(result.current.theme).toBe("light");
    expect(document.documentElement.classList.contains("dark")).toBe(false);
    expect(localStorage.getItem("theme")).toBe("light");
  });

  it("does not write to storage until the user actually toggles", () => {
    renderHook(() => useTheme());

    expect(localStorage.getItem("theme")).toBeNull();
  });
});
