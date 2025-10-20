import { BrowserRouter, Routes, Route, NavLink } from "react-router-dom";
import MarinesPage from "./pages/MarinesPage";
import SpecialOpsPage from "./pages/SpecialOpsPage";

function App() {
  return (
    <BrowserRouter>
      <nav className="bg-gray-800 text-white px-4 py-2 flex gap-4">
        <NavLink to="/" className="hover:underline">Marines</NavLink>
        <NavLink to="/special" className="hover:underline">Special Ops</NavLink>
      </nav>

      <Routes>
        <Route path="/" element={<MarinesPage />} />
        <Route path="/special" element={<SpecialOpsPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
