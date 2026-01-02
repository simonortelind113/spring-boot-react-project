import { useState } from "react";
import api from "../api/api"; // api.js is in src/api/api.js

function CreateAccount() {
  const [ownerName, setOwnerName] = useState("");
  const [account, setAccount] = useState(null);

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      const response = await api.post(`/accounts?ownerName=${ownerName}`);
      setAccount(response.data);
    } catch (err) {
      console.error(err);
      alert("Failed to create account");
    }
  };

  return (
    <div>
      <h2>Create Account</h2>
      <form onSubmit={handleCreate}>
        <input
          type="text"
          placeholder="Owner Name"
          value={ownerName}
          onChange={(e) => setOwnerName(e.target.value)}
        />
        <button type="submit">Create</button>
      </form>

      {account && (
        <div>
          <h3>Account Created:</h3>
          <p>ID: {account.id}</p>
          <p>Owner: {account.ownerName}</p>
          <p>Balance: {account.balance}</p>
        </div>
      )}
    </div>
  );
}

export default CreateAccount;
