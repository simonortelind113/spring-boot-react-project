import { useState } from "react";
import api from "../api/api";

function TransactionHistory() {
  const [accountId, setAccountId] = useState("");
  const [transactions, setTransactions] = useState([]);

  const fetchTransactions = async () => {
    try {
      const res = await api.get(`/accounts/${accountId}/transactions`);
      setTransactions(res.data);
    } catch (err) {
      console.error(err);
      alert("Failed to fetch transactions");
    }
  };

  return (
    <div>
      <h2>Transaction History</h2>
      <input
        type="number"
        placeholder="Account ID"
        value={accountId}
        onChange={(e) => setAccountId(e.target.value)}
      />
      <button onClick={fetchTransactions}>Get Transactions</button>

      <ul>
        {transactions.map((t) => (
          <li key={t.id}>
            {t.type} - {t.amount} - {new Date(t.createdAt).toLocaleString()}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default TransactionHistory;
