import { useState, useEffect } from "react";
import AccountDetails from "./AccountDetails";
import TransactionHistory from "./TransactionHistory";
import api from "../api/api";

function Dashboard({ account, onLogout }) {
  const [currentAccount, setCurrentAccount] = useState(account);
  const [allAccounts, setAllAccounts] = useState([]); 
  const [managerError, setManagerError] = useState("");

  const isManager = currentAccount?.manager || currentAccount?.isManager;

  useEffect(() => {
    setCurrentAccount(account);
    
    if (account && (account.manager || account.isManager)) {
      fetchAllAccounts(account.id);
    }
  }, [account]);

  const fetchAllAccounts = async (adminId) => {
    try {
      const response = await api.get(`/accounts?adminId=${adminId}`); 
      setAllAccounts(response.data);
      setManagerError("");
    } catch (err) {
      console.error("Failed to fetch accounts", err);
      setManagerError("Could not load account list. Access denied.");
    }
  };

  const handleDeleteAccount = async (targetId) => {
    const confirmDelete = window.confirm(
      targetId === currentAccount.id 
        ? "Are you sure you want to delete YOUR OWN account? You will be logged out."
        : "Are you sure you want to delete this account?"
    );

    if (!confirmDelete) return;

    try {
      // Matches your @DeleteMapping("/{id}") with @RequestParam Long adminId
      await api.delete(`/accounts/${targetId}?adminId=${currentAccount.id}`);
      
      // Remove the deleted account from the UI list
      setAllAccounts(allAccounts.filter(acc => acc.id !== targetId));
      
      // If the manager deletes themselves, force logout
      if (targetId === currentAccount.id) {
        onLogout();
      }
    } catch (err) {
      const msg = err.response?.data || "Failed to delete account";
      setManagerError(msg);
    }
  };

  if (!currentAccount) return null;

  return (
    <div style={{ maxWidth: "800px", margin: "20px auto", padding: "20px", backgroundColor: "#f5f5f5", borderRadius: "10px", fontFamily: "sans-serif" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <h1>Welcome, {currentAccount.ownerName}!</h1>
        <button
          onClick={onLogout}
          style={{
            backgroundColor: "#e74c3c",
            color: "white",
            border: "none",
            padding: "10px 20px",
            borderRadius: "5px",
            cursor: "pointer",
            fontWeight: "bold"
          }}
        >
          Logout
        </button>
      </div>

      {isManager && <p style={{ color: "#2980b9", fontWeight: "bold", marginTop: "-15px" }}>★ Manager Account</p>}

      <hr style={{ margin: "20px 0", border: "0", borderTop: "1px solid #ccc" }} />

      {/* --- MANAGER CONTROL PANEL --- */}
      {isManager && (
        <div style={{ 
          padding: "20px", 
          border: "2px solid #3498db", 
          borderRadius: "10px", 
          marginBottom: "30px", 
          backgroundColor: "#ebf5fb" 
        }}>
          <h2 style={{ marginTop: 0, color: "#2c3e50" }}>Manager Control Panel</h2>
          {managerError && <p style={{ color: "#c0392b", fontWeight: "bold" }}>{managerError}</p>}
          
          <div style={{ overflowX: "auto" }}>
            <table style={{ width: "100%", borderCollapse: "collapse", backgroundColor: "white" }}>
              <thead>
                <tr style={{ textAlign: "left", backgroundColor: "#3498db", color: "white" }}>
                  <th style={{ padding: "12px" }}>ID</th>
                  <th style={{ padding: "12px" }}>Owner Name</th>
                  <th style={{ padding: "12px" }}>Action</th>
                </tr>
              </thead>
              <tbody>
                {allAccounts.length > 0 ? (
                  allAccounts.map((acc) => (
                    <tr key={acc.id} style={{ borderBottom: "1px solid #ddd" }}>
                      <td style={{ padding: "12px" }}>{acc.id}</td>
                      <td style={{ padding: "12px" }}>
                        {acc.ownerName} {acc.id === currentAccount.id && <i style={{ color: "#7f8c8d" }}>(You)</i>}
                      </td>
                      <td style={{ padding: "12px" }}>
                        <button 
                          onClick={() => handleDeleteAccount(acc.id)}
                          style={{ 
                            backgroundColor: "#ff4757", 
                            color: "white", 
                            border: "none", 
                            padding: "6px 12px", 
                            borderRadius: "4px", 
                            cursor: "pointer" 
                          }}
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan="3" style={{ padding: "12px", textAlign: "center" }}>No accounts found.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* --- REGULAR USER DETAILS --- */}
      <div style={{ marginBottom: "30px" }}>
        <AccountDetails account={currentAccount} setAccount={setCurrentAccount} />
      </div>

      <TransactionHistory account={currentAccount} />
    </div>
  );
}

export default Dashboard;