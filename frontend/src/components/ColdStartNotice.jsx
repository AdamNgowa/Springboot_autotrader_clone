import { useElapsedSeconds } from "../hooks/useElapsedSeconds";
import { formatElapsed, getProgress, getStage } from "../utils/coldStart";

// A side-on car. Wheels spin (animate-spin) around their own centres.
function Car() {
  const wheelStyle = { transformBox: "fill-box", transformOrigin: "center" };

  return (
    <svg
      viewBox="0 0 56 24"
      className="h-7 w-auto animate-bob motion-reduce:animate-none"
      aria-hidden="true"
    >
      {/* Body and cabin */}
      <rect x="2" y="10" width="52" height="8" fill="#2563eb" />
      <path d="M14 10 L20 3 H38 L46 10 Z" fill="#2563eb" />

      {/* Windows */}
      <path d="M18 10 L22 5 H28 V10 Z" fill="#bfdbfe" />
      <path d="M30 10 V5 H37 L42 10 Z" fill="#bfdbfe" />

      {/* Headlight and tail light */}
      <rect x="50" y="12" width="4" height="3" fill="#fde047" />
      <rect x="2" y="12" width="3" height="3" fill="#ef4444" />

      {/* Wheels: the light dot is what makes the spin visible */}
      {[15, 41].map((cx) => (
        <g
          key={cx}
          className="animate-spin motion-reduce:animate-none"
          style={wheelStyle}
        >
          <circle cx={cx} cy="19" r="4.5" fill="#0f172a" stroke="#94a3b8" />
          <circle cx={cx + 2} cy="19" r="1" fill="#e2e8f0" />
        </g>
      ))}
    </svg>
  );
}

function ColdStartNotice({ active }) {
  const elapsed = useElapsedSeconds(active);
  const stage = getStage(elapsed);

  // Quick responses never see this.
  if (!active || !stage) {
    return null;
  }

  const progress = getProgress(elapsed);

  return (
    <div
      role="status"
      aria-live="polite"
      className="mb-6 border border-slate-300 bg-white p-4"
    >
      <div className="flex items-baseline justify-between gap-3">
        <p className="font-semibold">Waking up the server</p>
        <p className="text-sm tabular-nums text-slate-500">
          {formatElapsed(elapsed)}
        </p>
      </div>

      <p className="mt-1 text-sm text-slate-600">{stage.text}</p>

      {/* The road. Decorative, so hidden from screen readers. */}
      <div
        className="relative mt-4 h-14 overflow-hidden border border-slate-300 bg-slate-100"
        aria-hidden="true"
      >
        {/* Checkered finish flag */}
        <div
          className="absolute bottom-3 right-3 h-8 w-3 border border-slate-400"
          style={{
            backgroundImage:
              "conic-gradient(#0f172a 25%, #f8fafc 0 50%, #0f172a 0 75%, #f8fafc 0)",
            backgroundSize: "6px 6px",
          }}
        />

        {/* The car drives toward the flag as time passes */}
        <div
          className="absolute bottom-3 transition-[left] duration-1000 ease-linear"
          style={{ left: `${4 + progress * 0.7}%` }}
        >
          <Car />
        </div>

        {/* Dashed centre line, scrolling left so the car appears to move right */}
        <div className="road absolute inset-x-0 bottom-1.5 h-0.5 animate-road text-slate-400 motion-reduce:animate-none" />
      </div>
    </div>
  );
}

export default ColdStartNotice;
