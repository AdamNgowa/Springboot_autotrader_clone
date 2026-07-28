//Small wrapper around react's useContext

import { useContext } from "react";
import { AuthContext } from "../context/AuthContext";

//Components use this instead of importing AuthContext directly
export function useAuth() {
  return useContext(AuthContext);
}
