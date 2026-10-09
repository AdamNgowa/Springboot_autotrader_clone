const STYLES = {
  error: "border-red-300 bg-red-50 text-red-700",
  warning: "border-yellow-300 bg-yellow-50 text-yellow-800",
  success: "border-green-300 bg-green-50 text-green-800",
  info: "border-slate-300 bg-slate-50 text-slate-600",
};

// One boxy message strip for every page. variant: error | warning | success | info
function Notice({ variant = "info", children, className = "" }) {
  return (
    <div
      role={variant === "error" ? "alert" : undefined}
      className={`border p-3 text-sm ${STYLES[variant]} ${className}`}
    >
      {children}
    </div>
  );
}

export default Notice;
