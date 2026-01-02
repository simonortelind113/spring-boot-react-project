import { useState, useEffect } from "react";
import AccountDetails from "./AccountDetails";
import TransactionHistory from "./TransactionHistory";

function Dashboard({ account, onLogout }) {
  const [currentAccount, setCurrentAccount] = useState(account);

  useEffect(() => {
    setCurrentAccount(account);
  }, [account]);

  if (!currentAccount) return null;

  return (
    <div style={{ maxWidth: "800px", margin: "20px auto", padding: "20px", backgroundColor: "#f5f5f5", borderRadius: "10px" }}>
      <h1>Welcome, {currentAccount.ownerName}!</h1>

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

      <div style={{ marginBottom: "30px" }}>
        <AccountDetails account={currentAccount} setAccount={setCurrentAccount} />
      </div>

      <TransactionHistory account={currentAccount} />
    </div>
  );
}

export default Dashboard;

