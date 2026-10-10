import { useEffect, useState } from "react";

const STORAGE_KEY = "theme";

// index.html already applied the right class before React started: read it back.
function getInitialTheme() {
  return document.documentElement.classList.contains("dark") ? "dark" : "light";
}

export function useTheme() {
  const [theme, setTheme] = useState(getInitialTheme);

  // Keep the <html> class in sync with state.
  useEffect(() => {
    document.documentElement.classList.toggle("dark", theme === "dark");
  }, [theme]);

  function toggleTheme() {
    const next = theme === "dark" ? "light" : "dark";

    setTheme(next);

    // Only an explicit click is remembered. Storage can be blocked (private mode).
    try {
      localStorage.setItem(STORAGE_KEY, next);
    } catch {
      // Ignore: the toggle still works for this visit.
    }
  }

  return { theme, toggleTheme };
}
