import { useState } from "react";
import api from "../api/api";
import { Link } from "react-router-dom";

<p>
  Don't have an account? <Link to="/create-account">Create one</Link>
</p>



function Login({ onLogin }) {
  const [ownerName, setOwnerName] = useState("");
  const [error, setError] = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await api.post(`/login?ownerName=${ownerName}`);
      onLogin(response.data); // pass account to parent
    } catch (err) {
      console.error(err);
      setError("Account not found. Try creating a new account.");
    }
  };

  const handleCreate = async () => {
    try {
      const response = await api.post(`/accounts?ownerName=${ownerName}`);
      onLogin(response.data); // new account logs in automatically
    } catch (err) {
      console.error(err);
      setError("Failed to create account");
    }
  };

  return (
    <div style={{ maxWidth: "400px", margin: "20px auto", padding: "20px", border: "1px solid #ccc", borderRadius: "10px" }}>
      <h2>Login / Create Account</h2>
      <form onSubmit={handleLogin}>
        <input
          type="text"
          placeholder="Owner Name"
          value={ownerName}
          onChange={(e) => setOwnerName(e.target.value)}
          style={{ padding: "5px", marginRight: "5px" }}
        />
        <button type="submit">Login</button>
      </form>
      <button onClick={handleCreate} style={{ marginTop: "10px" }}>Create New Account</button>
      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
}

export default Login;
