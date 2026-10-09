import {
  BODY_TYPES,
  FUEL_TYPES,
  TRANSMISSIONS,
} from "../constants/listingEnums";

const labelClass = "mb-1.5 block text-xs font-medium text-slate-500";

/*
 * A row of selectable boxes for a small fixed set of options.
 * Clicking the selected option again clears it, so no "All" option is needed.
 */
function ChipGroup({ options, value, onChange }) {
  return (
    <div className="flex flex-wrap gap-1.5">
      {options.map((option) => {
        const selected = value === option;

        return (
          <button
            key={option}
            type="button"
            aria-pressed={selected}
            onClick={() => onChange(selected ? "" : option)}
            className={`border px-3 py-1.5 text-sm transition-colors ${
              selected
                ? "border-blue-600 bg-blue-600 text-white"
                : "border-slate-300 bg-white hover:border-slate-900"
            }`}
          >
            {option}
          </button>
        );
      })}
    </div>
  );
}

/*
 * One component, two presentations:
 * - below `lg`: a bottom sheet that is hidden until `open` is true
 * - from `lg` up: a static sidebar that is always visible
 * Only the wrapper classes differ, so the fields exist once.
 */
function SearchFilters({
  filters,
  setFilters,
  onApply,
  onReset,
  open,
  onClose,
}) {
  function update(name, value) {
    setFilters((current) => ({ ...current, [name]: value }));
  }

  // Wrapping the fields in a form lets the Enter key apply the filters.
  function handleSubmit(event) {
    event.preventDefault();
    onApply();
  }

  return (
    // Backdrop (mobile only). Clicking it closes the sheet.
    <div
      onClick={onClose}
      className={`${
        open
          ? "fixed inset-0 z-[60] flex flex-col justify-end bg-black/50"
          : "hidden"
      } lg:static lg:z-auto lg:block lg:bg-transparent`}
    >
      <form
        onSubmit={handleSubmit}
        // Clicks inside the panel must not reach the backdrop's onClick.
        onClick={(event) => event.stopPropagation()}
        className="max-h-[85vh] space-y-5 overflow-y-auto border-t-2 border-blue-600 bg-white p-4 lg:max-h-none lg:overflow-visible lg:border lg:border-slate-300"
      >
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold">Filters</h2>

          <button
            type="button"
            onClick={onClose}
            aria-label="Close filters"
            className="flex h-9 w-9 items-center justify-center border border-slate-300 hover:bg-slate-100 lg:hidden"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              className="h-5 w-5"
              aria-hidden="true"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M6 6l12 12M18 6 6 18"
              />
            </svg>
          </button>
        </div>

        {/* Make + city */}
        <div className="space-y-3">
          <div>
            <label htmlFor="filter-make" className={labelClass}>
              Make
            </label>
            <input
              id="filter-make"
              type="text"
              placeholder="Toyota"
              value={filters.make}
              onChange={(event) => update("make", event.target.value)}
              className="field"
            />
          </div>

          <div>
            <label htmlFor="filter-city" className={labelClass}>
              City
            </label>
            <input
              id="filter-city"
              type="text"
              placeholder="Nairobi"
              value={filters.city}
              onChange={(event) => update("city", event.target.value)}
              className="field"
            />
          </div>
        </div>

        {/* Price range */}
        <div>
          <p className={labelClass}>Price (KSh)</p>
          <div className="grid grid-cols-2 gap-2">
            <input
              type="number"
              inputMode="numeric"
              placeholder="Min"
              aria-label="Minimum price"
              value={filters.minPrice}
              onChange={(event) => update("minPrice", event.target.value)}
              className="field"
            />
            <input
              type="number"
              inputMode="numeric"
              placeholder="Max"
              aria-label="Maximum price"
              value={filters.maxPrice}
              onChange={(event) => update("maxPrice", event.target.value)}
              className="field"
            />
          </div>
        </div>

        {/* Enum filters */}
        <div>
          <p className={labelClass}>Body type</p>
          <ChipGroup
            options={BODY_TYPES}
            value={filters.bodyType}
            onChange={(value) => update("bodyType", value)}
          />
        </div>

        <div>
          <p className={labelClass}>Fuel</p>
          <ChipGroup
            options={FUEL_TYPES}
            value={filters.fuelType}
            onChange={(value) => update("fuelType", value)}
          />
        </div>

        <div>
          <p className={labelClass}>Transmission</p>
          <ChipGroup
            options={TRANSMISSIONS}
            value={filters.transmission}
            onChange={(value) => update("transmission", value)}
          />
        </div>

        {/* Sticks to the bottom of the sheet on mobile so Apply is always reachable. */}
        <div className="sticky bottom-0 -mx-4 -mb-4 flex gap-2 border-t border-slate-200 bg-white p-4 lg:static lg:m-0 lg:border-0 lg:p-0">
          <button type="submit" className="btn btn-primary flex-1">
            Apply filters
          </button>

          <button type="button" onClick={onReset} className="btn btn-outline">
            Reset
          </button>
        </div>
      </form>
    </div>
  );
}

export default SearchFilters;
