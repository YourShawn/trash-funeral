import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import Layout from "./components/Layout";
import RequireAuth from "./components/RequireAuth";
import CardPage from "./pages/CardPage";
import Cemetery from "./pages/Cemetery";
import Ceremony from "./pages/Ceremony";
import Landing from "./pages/Landing";
import Login from "./pages/Login";
import PublicCardPage from "./pages/PublicCardPage";
import Register from "./pages/Register";
import Upload from "./pages/Upload";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<Landing />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/c/:token" element={<PublicCardPage />} />
          <Route
            path="/new"
            element={
              <RequireAuth>
                <Upload />
              </RequireAuth>
            }
          />
          <Route
            path="/ceremony/:id"
            element={
              <RequireAuth>
                <Ceremony />
              </RequireAuth>
            }
          />
          <Route
            path="/card/:id"
            element={
              <RequireAuth>
                <CardPage />
              </RequireAuth>
            }
          />
          <Route
            path="/cemetery"
            element={
              <RequireAuth>
                <Cemetery />
              </RequireAuth>
            }
          />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
