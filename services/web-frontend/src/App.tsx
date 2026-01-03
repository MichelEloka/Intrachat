import { useKeycloak } from "@react-keycloak/web";
import Login from "./pages/Login";
import Home from "./pages/Home";

export default function App() {
  const keycloakContext = useKeycloak();

  if (!keycloakContext.initialized) {
    return <div style={{ color: "green" }}>Eloka mich</div>;
  }

  return keycloakContext.keycloak.authenticated ? <Home /> : <Login />;
}
