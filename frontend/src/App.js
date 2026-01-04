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

        {/* Login */}
        <Route
          path="/login"
          element={<Login onLogin={setAccount} />}
        />

        {/* Create Account */}
        <Route
          path="/create-account"
          element={<CreateAccountPage />}
        />

        {/* Dashboard (protected) */}
        <Route
          path="/dashboard"
          element={
            account
              ? <Dashboard account={account} onLogout={() => setAccount(null)} />
              : <Navigate to="/login" />
          }
        />

        {/* Default redirect */}
        <Route path="*" element={<Navigate to="/login" />} />

      </Routes>
    </Router>
  );
}

export default App;
