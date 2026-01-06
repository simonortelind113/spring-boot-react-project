import { useState, useEffect } from "react";
import AccountDetails from "./AccountDetails";
import TransactionHistory from "./TransactionHistory";
import api from "../api/api";

// Simple role badge component
function RoleBadge({ role }) {
  const color =
    role === "MANAGER" ? "#2980b9" :
    role === "ADVISOR" ? "#27ae60" :
    "#7f8c8d";
  return (
    <span
      style={{
        color: "white",
        backgroundColor: color,
        padding: "4px 10px",
        borderRadius: "5px",
        fontWeight: "bold",
        marginLeft: "10px",
      }}
    >
      {role}
    </span>
  );
}

function Dashboard({ account, onLogout }) {
  const [currentAccount, setCurrentAccount] = useState(account);
  const [allAccounts, setAllAccounts] = useState([]);
  const [error, setError] = useState("");

  const role = currentAccount?.role;
  const isManager = role === "MANAGER";

  useEffect(() => {
    setCurrentAccount(account);

    if (account?.role === "MANAGER") {
      fetchAllAccounts(account.id);
    }
  }, [account]);

  const fetchAllAccounts = async (adminId) => {
    try {
      const res = await api.get(`/accounts?adminId=${adminId}`);
      setAllAccounts(res.data);
      setError("");
    } catch (err) {
      setError("Access denied or failed to load accounts");
    }
  };

  const handleDeleteAccount = async (targetId) => {
    const confirmDelete = window.confirm(
      targetId === currentAccount.id
        ? "Delete your own account? You will be logged out."
        : "Delete this account?"
    );

    if (!confirmDelete) return;

    try {
      await api.delete(`/accounts/${targetId}?adminId=${currentAccount.id}`);
      setAllAccounts(allAccounts.filter((a) => a.id !== targetId));

      if (targetId === currentAccount.id) onLogout();
    } catch (err) {
      setError(err.response?.data || "Delete failed");
    }
  };

  if (!currentAccount) return null;

  // --- STYLE OBJECTS ---
  const container = {
    maxWidth: "900px",
    margin: "20px auto",
    padding: "20px",
    backgroundColor: "#f5f5f5",
    borderRadius: "10px",
    fontFamily: "sans-serif",
  };

  const header = {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  };

  const logoutBtn = {
    backgroundColor: "#e74c3c",
    color: "white",
    border: "none",
    padding: "10px 20px",
    borderRadius: "5px",
    cursor: "pointer",
    fontWeight: "bold",
  };

  const managerPanel = {
    padding: "20px",
    border: "2px solid #3498db",
    borderRadius: "10px",
    marginBottom: "30px",
    backgroundColor: "#ebf5fb",
  };

  const errorStyle = {
    color: "#c0392b",
    fontWeight: "bold",
    marginBottom: "10px",
  };

  const table = {
    width: "100%",
    borderCollapse: "collapse",
    backgroundColor: "white",
  };

  const deleteBtn = {
    backgroundColor: "#ff4757",
    color: "white",
    border: "none",
    padding: "6px 12px",
    borderRadius: "4px",
    cursor: "pointer",
  };

  return (
    <div style={container}>
      {/* HEADER */}
      <div style={header}>
        <h1>
          Welcome, {currentAccount.ownerName} <RoleBadge role={role} />
        </h1>
        <button onClick={onLogout} style={logoutBtn}>
          Logout
        </button>
      </div>

      <hr style={{ margin: "20px 0" }} />

      {/* MANAGER PANEL */}
      {isManager && (
        <div style={managerPanel}>
          <h2>Manager Control Panel</h2>
          {error && <p style={errorStyle}>{error}</p>}

          <table style={table}>
            <thead>
              <tr style={{ textAlign: "left", backgroundColor: "#3498db", color: "white" }}>
                <th style={{ padding: "10px" }}>ID</th>
                <th style={{ padding: "10px" }}>Owner</th>
                <th style={{ padding: "10px" }}>Role</th>
                <th style={{ padding: "10px" }}>Action</th>
              </tr>
            </thead>
            <tbody>
              {allAccounts.map((acc) => (
                <tr key={acc.id} style={{ borderBottom: "1px solid #ddd" }}>
                  <td style={{ padding: "10px" }}>{acc.id}</td>
                  <td style={{ padding: "10px" }}>
                    {acc.ownerName} {acc.id === currentAccount.id && "(You)"}
                  </td>
                  <td style={{ padding: "10px" }}>{acc.role}</td>
                  <td style={{ padding: "10px" }}>
                    <button onClick={() => handleDeleteAccount(acc.id)} style={deleteBtn}>
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* ACCOUNT DETAILS */}
      <AccountDetails account={currentAccount} setAccount={setCurrentAccount} />

      {/* TRANSACTIONS */}
      <TransactionHistory account={currentAccount} />
    </div>
  );
}

export default Dashboard;
