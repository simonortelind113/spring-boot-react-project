// src/components/CreateAccountPage.js
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api"; // make sure this points to your api.js

function CreateAccountPage({ onLogin }) {
  const [ownerName, setOwnerName] = useState("");
  const [password, setPassword] = useState(""); // optional for now
  const [error, setError] = useState("");
  const navigate = useNavigate(); // allows navigation after creation

  const handleCreate = async (e) => {
    e.preventDefault();
  
    if (!ownerName) {
      setError("Owner Name is required");
      return;
    }
  
    try {
        await api.post("/accounts", { ownerName });
        navigate("/login"); // gowhat do i nee to login page
    } catch (err) {
      console.error(err);
      setError("Failed to create account.");
    }
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
      <h2>Create New Account</h2>
      <form onSubmit={handleCreate} style={{ display: "flex", flexDirection: "column" }}>
        <input
          type="text"
          placeholder="Owner Name"
          value={ownerName}
          onChange={(e) => setOwnerName(e.target.value)}
          style={{ padding: "8px", marginBottom: "10px" }}
        />
        <input
          type="password"
          placeholder="Password (optional)"
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
          Create Account
        </button>
      </form>
      {error && <p style={{ color: "red", marginTop: "10px" }}>{error}</p>}
    </div>
  );
}

export default CreateAccountPage;
