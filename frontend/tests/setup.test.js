import { describe, expect, it } from "vitest";

describe("frontend test environment", () => {
  it("provides a browser-like document", () => {
    expect(document).toBeDefined();
  });
});
