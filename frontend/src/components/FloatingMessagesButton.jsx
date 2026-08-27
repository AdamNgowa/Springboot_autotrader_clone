import { Link } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

function FloatingMessagesButton() {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return null;
  }

  return (
    <Link
      to="/conversations"
      aria-label="Messages"
      title="Messages"
      className="fixed bottom-6 right-6 z-50 flex h-14 w-14 items-center justify-center rounded-full bg-blue-600 text-white shadow-lg transition hover:scale-105 hover:bg-blue-700"
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        className="h-6 w-6"
        aria-hidden="true"
      >
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          d="M21 12c0 4.142-4.03 7.5-9 7.5a9.77 9.77 0 0 1-2.555-.337A5.972 5.972 0 0 1 5.41 20.97a5.969 5.969 0 0 1-.474-.065 4.48 4.48 0 0 0 .978-2.025c.09-.457-.133-.901-.5-1.185C3.964 16.478 3 14.376 3 12c0-4.142 4.03-7.5 9-7.5s9 3.358 9 7.5Z"
        />
      </svg>
    </Link>
  );
}

export default FloatingMessagesButton;
