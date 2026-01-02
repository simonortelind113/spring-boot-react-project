// src/components/Dashboard.js
import { useState } from "react";
import AccountDetails from "./AccountDetails";
import TransactionHistory from "./TransactionHistory";


function Dashboard({ account, onLogout }) {
  const [currentAccount, setCurrentAccount] = useState(account);

  return (
    <div style={{ maxWidth: "800px", margin: "20px auto", padding: "20px", backgroundColor: "#f5f5f5", borderRadius: "10px" }}>
      <h1>Welcome, {currentAccount.ownerName}!</h1>

      {/* Logout button */}
      <button
        onClick={onLogout}
        style={{
          backgroundColor: "#e74c3c",
          color: "white",
          border: "none",
          padding: "8px 16px",
          borderRadius: "5px",
          cursor: "pointer",
          marginBottom: "20px",
        }}
      >
        Logout
      </button>

      {/* Account Details */}
      <div style={{ marginBottom: "30px" }}>
        <AccountDetails account={currentAccount} setAccount={setCurrentAccount} />
      </div>

      {/* Transaction History */}
      <div>
        <TransactionHistory account={currentAccount} />
      </div>
    </div>
  );
}

export default Dashboard;
