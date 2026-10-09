function SpecificationCard({ label, value }) {
  return (
    <div className="border border-slate-300 bg-white p-3">
      <p className="text-xs text-slate-500">{label}</p>

      <p className="mt-0.5 font-medium text-slate-900">{value}</p>
    </div>
  );
}

export default SpecificationCard;
