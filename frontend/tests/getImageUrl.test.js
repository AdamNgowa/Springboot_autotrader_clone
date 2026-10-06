import { afterEach, describe, expect, it, vi } from "vitest";

import { getImageUrl } from "../src/utils/getImageUrl";

/*
 * getImageUrl turns the path the API returns ("/uploads/abc.jpg") into a URL
 * the browser can load. Phase 12.11 (persistent image storage) will make the
 * API return ABSOLUTE storage URLs, so the "absolute URLs pass through
 * untouched" tests below are the safety net for that change.
 *
 * BASE_URL is read once at import time from VITE_API_URL, so the tests read
 * the same variable instead of hard-coding a value.
 */
const BASE_URL = import.meta.env.VITE_API_URL || "";

describe("getImageUrl", () => {
  afterEach(() => {
    vi.unstubAllEnvs();
    vi.resetModules();
  });

  it.each([null, undefined, ""])("returns an empty string for %p", (value) => {
    expect(getImageUrl(value)).toBe("");
  });

  it("prefixes a relative upload path with the API base URL", () => {
    expect(getImageUrl("/uploads/car.jpg")).toBe(`${BASE_URL}/uploads/car.jpg`);
  });

  it.each([
    "http://localhost:8080/uploads/car.jpg",
    "https://storage.example.com/bucket/car.jpg",
    "blob:http://localhost:5173/3f2a9c7e-1b2d-4c8e-9a10-000000000000",
  ])("returns absolute URL %s unchanged", (url) => {
    expect(getImageUrl(url)).toBe(url);
  });

  it("returns the path unchanged when VITE_API_URL is empty", async () => {
    // BASE_URL is captured when the module loads, so load a fresh copy.
    vi.stubEnv("VITE_API_URL", "");
    vi.resetModules();

    const { getImageUrl: freshGetImageUrl } =
      await import("../src/utils/getImageUrl");

    expect(freshGetImageUrl("/uploads/car.jpg")).toBe("/uploads/car.jpg");
  });

  it("uses a different base URL when VITE_API_URL changes", async () => {
    vi.stubEnv("VITE_API_URL", "https://api.example.com");
    vi.resetModules();

    const { getImageUrl: freshGetImageUrl } =
      await import("../src/utils/getImageUrl");

    expect(freshGetImageUrl("/uploads/car.jpg")).toBe(
      "https://api.example.com/uploads/car.jpg",
    );
  });
});
