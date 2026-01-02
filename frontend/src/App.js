// src/App.js
import { useState } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./components/Login";
import CreateAccountPage from "./components/CreateAccountPage";
import Dashboard from "./components/Dashboard";

function App() {
  const [account, setAccount] = useState(null);

  return (
    <Router>
      <Routes>
        {/* Login Page */}
        <Route
          path="/"
          element={account ? <Navigate to="/dashboard" /> : <Login onLogin={setAccount} />}
        />

        {/* Create Account Page */}
        <Route
          path="/create-account"
          element={account ? <Navigate to="/dashboard" /> : <CreateAccountPage onLogin={setAccount} />}
        />

        {/* Dashboard Page */}
        <Route
          path="/"
          element={account ? <Dashboard account={account} onLogout={() => setAccount(null)} /> : <Navigate to="/" />}
        />
      </Routes>
    </Router>
  );
}

export default App;
