import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import tailwindcss from "@tailwindcss/vite";

export default defineConfig({
  plugins: [react(), tailwindcss()],

  test: {
    // jsdom provides a simulated browser environment
    environment: "jsdom",
    //Tells vitest before running the tests,execute this setup file
    //We'll use it for global test configration such as jest-dom
    setupFiles: "./tests/setup.js",
    //Allows us to write describe(),it(),expect()...without importing every function into every test file
    globals: true,
  },
});
