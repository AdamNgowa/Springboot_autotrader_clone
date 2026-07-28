import { useState } from "react";
import { useAuth } from "../hooks/useAuth";

function HomePage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loginError, setLoginError] = useState("");

  const { login, isAuthenticated } = useAuth();

  function handleEmailChange(ev) {
    setEmail(ev.target.value);
  }

  function handlePasswordChange(ev) {
    setPassword(ev.target.value);
  }

  async function handleSubmit(ev) {
    ev.preventDefault();

    try {
      await login({ email, password });

      setLoginError("");
    } catch (error) {
      setLoginError(error.message);
      console.error(error);
    }
  }

  return (
    <main className="m-4">
      <form onSubmit={handleSubmit}>
        Email: <input type="email" value={email} onChange={handleEmailChange} />
        <br />
        Password:{" "}
        <input
          type="password"
          value={password}
          onChange={handlePasswordChange}
        />
        <br />
        <button className="bg-blue-700" type="submit">
          Login
        </button>
        {loginError && <p className="mt-2 text-red-600">{loginError}</p>}
      </form>

      <br />

      <div>
        <p>Email: {email}</p>
        <p>Password: {password}</p>

        <hr />

        <p>Authenticated: {isAuthenticated ? "Yes" : "No"}</p>
      </div>
    </main>
  );
}

export default HomePage;
