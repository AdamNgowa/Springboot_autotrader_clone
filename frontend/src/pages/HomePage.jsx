import { useState } from "react";
import { login } from "../api/authApi";

function HomePage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  function handleEmailChange(ev) {
    setEmail(ev.target.value);
  }

  function handlePasswordChange(ev) {
    setPassword(ev.target.value);
  }

  function handleSubmit(ev) {
    ev.preventDefault();
    console.log("Form submitted");
    login({ email, password });
  }

  return (
    <main className="m-4">
      <form onSubmit={handleSubmit}>
        Email: <input type="email" onChange={handleEmailChange} value={email} />
        <br />
        Password:{" "}
        <input
          type="password"
          onChange={handlePasswordChange}
          value={password}
        />
        <br />
        <button className="bg-blue-700" type="submit">
          Login
        </button>
      </form>

      <br />
      <div>
        <p>Email: {email}</p>
        <p>Password: {password}</p>
      </div>
    </main>
  );
}
export default HomePage;
