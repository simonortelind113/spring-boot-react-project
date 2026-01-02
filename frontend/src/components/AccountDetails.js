import { useState } from "react";
import api from "../api/api";

function AccountDetails() {
  const [accountId, setAccountId] = useState("");
  const [account, setAccount] = useState(null);
  const [amount, setAmount] = useState("");

  const fetchAccount = async () => {
    try {
      const res = await api.get(`/accounts/${accountId}`);
      setAccount(res.data);
    } catch (err) {
      console.error(err);
      alert("Account not found");
    }
  };

  const deposit = async () => {
    try {
      const res = await api.post(`/accounts/${accountId}/deposit?amount=${amount}`);
      setAccount(res.data);
    } catch (err) {
      console.error(err);
      alert("Deposit failed");
    }
  };

  return (
    <div>
      <h2>Account Details</h2>
      <input
        type="number"
        placeholder="Account ID"
        value={accountId}
        onChange={(e) => setAccountId(e.target.value)}
      />
      <button onClick={fetchAccount}>Get Account</button>

      {account && (
        <div>
          <p>ID: {account.id}</p>
          <p>Owner: {account.ownerName}</p>
          <p>Balance: {account.balance}</p>

          <input
            type="number"
            placeholder="Deposit Amount"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
          />
          <button onClick={deposit}>Deposit</button>
        </div>
      )}
    </div>
  );
}

export default AccountDetails;
