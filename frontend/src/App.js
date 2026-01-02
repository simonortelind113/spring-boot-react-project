import CreateAccount from "./components/CreateAccount";
import AccountDetails from "./components/AccountDetails";
import TransactionHistory from "./components/TransactionHistory";

function App() {
  return (
    <div className="App">
      <h1>Banking App</h1>
      <CreateAccount />
      <AccountDetails />
      <TransactionHistory />
    </div>
  );
}

export default App;