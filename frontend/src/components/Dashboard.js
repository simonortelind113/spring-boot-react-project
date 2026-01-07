import { useState, useEffect } from "react";
import api from "../api/api";

/* ---------- ROLE BADGE ---------- */
function RoleBadge({ role }) {
  const color =
    role === "MANAGER" ? "#2980b9" :
    role === "BANK_ADVISOR" ? "#27ae60" :
    "#7f8c8d";

  return (
    <span
      style={{
        backgroundColor: color,
        color: "white",
        padding: "4px 10px",
        borderRadius: "6px",
        fontWeight: "bold",
        marginLeft: "10px",
        fontSize: "0.9rem",
      }}
    >
      {role}
    </span>
  );
}

/* ---------- DASHBOARD ---------- */
function Dashboard({ account, onLogout }) {
  const [currentAccount, setCurrentAccount] = useState(account);
  const [allAccounts, setAllAccounts] = useState([]);
  const [depositAmount, setDepositAmount] = useState("");
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [pendingTransactions, setPendingTransactions] = useState([]);

  const isManager = currentAccount?.role === "MANAGER";
  const isStaff = currentAccount?.role === "MANAGER" || currentAccount?.role === "BANK_ADVISOR";

  useEffect(() => {
    setCurrentAccount(account);

    if (account?.role === "MANAGER") {
      fetchAllAccounts(account.id);
    }

    if (isStaff) {
      fetchPendingTransactions(account.id); 
    }
  }, [account]);

  /* ---------- API CALLS ---------- */
  const fetchAllAccounts = async (adminId) => {
    try {
      const res = await api.get(`/accounts?adminId=${adminId}`);
      setAllAccounts(res.data);
    } catch {
      setError("Failed to load accounts");
    }
  };

  const fetchPendingTransactions = async (staffId) => {
    try {
      const res = await api.get(`/accounts/deposit-requests?staffId=${staffId}`);
      setPendingTransactions(
        res.data.filter(t => t.status === "PENDING")
      );
    } catch {
      setError("Failed to load pending transactions");
    }
  };

  /* ---------- CUSTOMER ACTIONS ---------- */
  const handleDeposit = async () => {
    if (!depositAmount || Number(depositAmount) <= 0) return;

    try {
      await api.post(
        `/accounts/${currentAccount.id}/deposit-request`,
        null,
        {
          params: {
            performerId: currentAccount.id,
            amount: depositAmount,
          },
        }
      );

      setDepositAmount("");
      setError("");
      setMessage("Deposit request submitted. Awaiting approval.");
    } catch (err) {
      setError(err.response?.data || "Deposit request failed");
      setMessage("");
    }
  };

  const handleWithdraw = async () => {
    if (!depositAmount || Number(depositAmount) <= 0) return;

    try {
      await api.post(
        `/accounts/${currentAccount.id}/withdraw`,
        null,
        { params: { amount: depositAmount } }
      );

      // Update balance immediately for UX
      setCurrentAccount({
        ...currentAccount,
        balance: currentAccount.balance - Number(depositAmount),
      });

      setDepositAmount("");
      setError("");
      setMessage("Withdrawal successful");
    } catch (err) {
      setError(err.response?.data || "Withdrawal failed");
      setMessage("");
    }
  };

  /* ---------- STAFF ACTIONS ---------- */
  const handleApproveTransaction = async (requestId) => {
    try {
      await api.post(`/accounts/deposit-requests/${requestId}/approve`, null, {
        params: { staffId: currentAccount.id },
      });
      setPendingTransactions(pendingTransactions.filter(r => r.id !== requestId));
      setMessage("Transaction approved");
    } catch (err) {
      setError(err.response?.data || "Approval failed");
      setMessage("");
    }
  };

  const handleRejectTransaction = async (requestId) => {
    try {
      await api.post(`/accounts/deposit-requests/${requestId}/reject`, null, {
        params: { staffId: currentAccount.id },
      });
      setPendingTransactions(pendingTransactions.filter(r => r.id !== requestId));
      setMessage("Transaction rejected");
    } catch (err) {
      setError(err.response?.data || "Rejection failed");
      setMessage("");
    }
  };

  /* ---------- MANAGER ACTIONS ---------- */
  const handleDeleteAccount = async (targetId) => {
    if (!window.confirm("Delete this account?")) return;

    try {
      await api.delete(`/accounts/${targetId}?adminId=${currentAccount.id}`);
      setAllAccounts(allAccounts.filter((a) => a.id !== targetId));

      if (targetId === currentAccount.id) onLogout();
    } catch (err) {
      const msg =
        typeof err.response?.data === "string"
          ? err.response.data
          : err.response?.data?.error || "Delete failed";
      setError(msg);
      setMessage("");
    }
  };

  if (!currentAccount) return null;

  /* ---------- STYLES ---------- */
  const container = {
    maxWidth: "900px",
    margin: "20px auto",
    padding: "20px",
    backgroundColor: "#f4f6f8",
    borderRadius: "10px",
    fontFamily: "Arial, sans-serif",
  };

  const header = {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  };

  const card = {
    backgroundColor: "white",
    padding: "20px",
    borderRadius: "8px",
    marginBottom: "20px",
    boxShadow: "0 2px 5px rgba(0,0,0,0.1)",
  };

  const button = {
    padding: "10px 18px",
    borderRadius: "6px",
    border: "none",
    fontWeight: "bold",
    cursor: "pointer",
  };

  return (
    <div style={container}>
      {/* ---------- HEADER ---------- */}
      <div style={header}>
        <h1>
          Welcome, {currentAccount.ownerName}
          <RoleBadge role={currentAccount.role} />
        </h1>
        <button
          onClick={onLogout}
          style={{ ...button, backgroundColor: "#e74c3c", color: "white" }}
        >
          Logout
        </button>
      </div>

      {/* ---------- ERROR & MESSAGE ---------- */}
      {error && <p style={{ color: "red", fontWeight: "bold" }}>{error}</p>}
      {message && <p style={{ color: "green", fontWeight: "bold" }}>{message}</p>}

      {/* ---------- ACCOUNT SUMMARY ---------- */}
      <div style={card}>
        <h2>Account Summary</h2>
        <p><strong>Account ID:</strong> {currentAccount.id}</p>
        <p><strong>Balance:</strong> €{currentAccount.balance}</p>
      </div>

      {/* ---------- DEPOSIT / WITHDRAW ---------- */}
      {currentAccount.role === "CUSTOMER" && (
        <div style={card}>
          <h2>Deposit / Withdraw</h2>

          <input
            type="number"
            value={depositAmount}
            placeholder="Enter amount"
            onChange={(e) => setDepositAmount(e.target.value)}
            style={{ padding: "10px", marginRight: "10px", width: "200px" }}
          />

          <button
            onClick={handleDeposit}
            style={{
              ...button,
              backgroundColor: "#2ecc71",
              color: "white",
              marginRight: "8px",
            }}
          >
            Deposit
          </button>

          <button
            onClick={handleWithdraw}
            style={{
              ...button,
              backgroundColor: "#e67e22",
              color: "white",
            }}
          >
            Withdraw
          </button>
        </div>
      )}

      {/* ---------- PENDING TRANSACTIONS (Staff only) ---------- */}
      {isStaff && pendingTransactions.length > 0 && (
        <div style={card}>
          <h2>Pending Transactions</h2>

          <table width="100%">
            <thead>
              <tr>
                <th>ID</th>
                <th>Type</th>
                <th>Customer ID</th>
                <th>Amount</th>
                <th>Action</th>
              </tr>
            </thead>

            <tbody>
              {pendingTransactions.map((r) => (
                <tr key={r.id}>
                  <td>{r.id}</td>
                  <td>
                    <strong
                      style={{
                        color: r.type === "DEPOSIT" ? "#27ae60" : "#e67e22",
                      }}
                    >
                      {r.type}
                    </strong>
                  </td>
                  <td>{r.requestedBy}</td>
                  <td>€{r.amount}</td>
                  <td>
                    <button
                      onClick={() => handleApproveTransaction(r.id)}
                      style={{
                        ...button,
                        backgroundColor: "#2ecc71",
                        color: "white",
                        marginRight: "5px",
                      }}
                    >
                      Approve
                    </button>

                    <button
                      onClick={() => handleRejectTransaction(r.id)}
                      style={{
                        ...button,
                        backgroundColor: "#e74c3c",
                        color: "white",
                      }}
                    >
                      Reject
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* ---------- MANAGER PANEL ---------- */}
      {isManager && (
        <div style={card}>
          <h2>Manager Control Panel</h2>
          <table width="100%">
            <thead>
              <tr>
                <th>ID</th>
                <th>Owner</th>
                <th>Role</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {allAccounts.map((acc) => (
                <tr key={acc.id}>
                  <td>{acc.id}</td>
                  <td>{acc.ownerName}</td>
                  <td>{acc.role}</td>
                  <td>
                    <button
                      onClick={() => handleDeleteAccount(acc.id)}
                      style={{ ...button, backgroundColor: "#ff4757", color: "white" }}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default Dashboard;
