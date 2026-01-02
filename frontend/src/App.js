import { useState } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./components/Login";
import CreateAccountPage from "./components/CreateAccountPage"; // you will create this
import Dashboard from "./components/Dashboard"; // main app after login

function App() {
  const [account, setAccount] = useState(null);

  return (
    <Router>
      <Routes>
        {/* Login page */}
        <Route
          path="/"
          element={
            account ? (
              <Navigate to="/dashboard" />
            ) : (
              <Login onLogin={setAccount} />
            )
          }
        />

        {/* Create Account page */}
        <Route
          path="/create-account"
          element={
            account ? (
              <Navigate to="/dashboard" />
            ) : (
              <CreateAccountPage onLogin={setAccount} />
            )
          }
        />

        {/* Dashboard */}
        <Route
          path="/dashboard"
          element={
            account ? (
              <Dashboard account={account} onLogout={() => setAccount(null)} />
            ) : (
              <Navigate to="/" />
            )
          }
        />
      </Routes>
    </Router>
  );
}

export default App;
