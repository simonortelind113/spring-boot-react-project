// src/components/Login.js
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";

function Login({ onLogin }) {
  const [ownerName, setOwnerName] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
  
    if (!ownerName || !password) {
      setError("Owner Name and Password are required");
      return;
    }
  
    try {
      const response = await api.post("/accounts/login", { ownerName, password });
      const account = response.data;
      onLogin(account); // sets account in App state
      navigate("/dashboard");
    } catch (err) {
        console.log("--- DEBUG START ---");
        console.log("Status Code:", err.response?.status); // e.g. 400, 404, 500
        console.log("Server Message:", err.response?.data); // The message from your backend
        console.log("Full Error Object:", err);
        console.log("--- DEBUG END ---");
        setError("Login failed. Check the console for details.");
    }
  };
  
  const goToCreateAccount = () => {
    navigate("/create-account"); // navigate to account creation page
  };

  return (
    <div
      style={{
        maxWidth: "400px",
        margin: "50px auto",
        padding: "20px",
        border: "1px solid #ccc",
        borderRadius: "10px",
        backgroundColor: "#f9f9f9",
      }}
    >
      <h2>Login</h2>
      <form onSubmit={handleLogin} style={{ display: "flex", flexDirection: "column" }}>
        <input
          type="text"
          placeholder="Owner Name"
          value={ownerName}
          onChange={(e) => setOwnerName(e.target.value)}
          style={{ padding: "8px", marginBottom: "10px" }}
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          style={{ padding: "8px", marginBottom: "10px" }}
        />
        <button
          type="submit"
          style={{
            padding: "10px",
            backgroundColor: "#3498db",
            color: "white",
            border: "none",
            borderRadius: "5px",
            cursor: "pointer",
          }}
        >
          Login
        </button>
      </form>

      <button
        onClick={goToCreateAccount}
        style={{
          marginTop: "10px",
          padding: "10px",
          backgroundColor: "#2ecc71",
          color: "white",
          border: "none",
          borderRadius: "5px",
          cursor: "pointer",
        }}
      >
        Create New Account
      </button>

      {error && <p style={{ color: "red", marginTop: "10px" }}>{error}</p>}
    </div>
  );
}

export default Login;
