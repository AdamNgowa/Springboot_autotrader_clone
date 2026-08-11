import {
  BODY_TYPES,
  FUEL_TYPES,
  TRANSMISSIONS,
} from "../constants/listingEnums";

function SearchFilters({ filters, setFilters, onReset }) {
  return (
    <div className="mb-6 grid grid-cols-1 gap-4 md:grid-cols-2">
      {/* Sort */}
      <select
        value={filters.sort}
        onChange={(event) =>
          setFilters((current) => ({
            ...current,
            sort: event.target.value,
          }))
        }
        className="rounded-lg border p-3"
      >
        <option value="createdAt,desc">Newest First</option>
        <option value="createdAt,asc">Oldest First</option>
        <option value="price,asc">Price: Low → High</option>
        <option value="price,desc">Price: High → Low</option>
        <option value="year,desc">Year: Newest</option>
        <option value="year,asc">Year: Oldest</option>
      </select>

      {/* Make */}
      <input
        type="text"
        placeholder="Search by make..."
        value={filters.make}
        onChange={(event) =>
          setFilters((current) => ({
            ...current,
            make: event.target.value,
          }))
        }
        className="w-full rounded-lg border p-3"
      />

      {/* City */}
      <input
        type="text"
        placeholder="City..."
        value={filters.city}
        onChange={(event) =>
          setFilters((current) => ({
            ...current,
            city: event.target.value,
          }))
        }
        className="w-full rounded-lg border p-3"
      />

      {/* Price range */}
      <div className="grid grid-cols-2 gap-4">
        <input
          type="number"
          placeholder="Minimum Price"
          value={filters.minPrice}
          onChange={(event) =>
            setFilters((current) => ({
              ...current,
              minPrice: event.target.value,
            }))
          }
          className="rounded-lg border p-3"
        />

        <input
          type="number"
          placeholder="Maximum Price"
          value={filters.maxPrice}
          onChange={(event) =>
            setFilters((current) => ({
              ...current,
              maxPrice: event.target.value,
            }))
          }
          className="rounded-lg border p-3"
        />
      </div>

      <div className="grid grid-cols-3 gap-4">
        {/* Body types */}
        <select
          value={filters.bodyType}
          onChange={(event) =>
            setFilters((current) => ({
              ...current,
              bodyType: event.target.value,
            }))
          }
          className="rounded-lg border p-3"
        >
          <option value="">All Body Types</option>

          {BODY_TYPES.map((type) => (
            <option key={type} value={type}>
              {type}
            </option>
          ))}
        </select>

        {/* Fuel types */}
        <select
          value={filters.fuelType}
          onChange={(event) =>
            setFilters((current) => ({
              ...current,
              fuelType: event.target.value,
            }))
          }
          className="rounded-lg border p-3"
        >
          <option value="">All Fuel Types</option>

          {FUEL_TYPES.map((type) => (
            <option key={type} value={type}>
              {type}
            </option>
          ))}
        </select>

        {/* Transmission */}
        <select
          value={filters.transmission}
          onChange={(event) =>
            setFilters((current) => ({
              ...current,
              transmission: event.target.value,
            }))
          }
          className="rounded-lg border p-3"
        >
          <option value="">All Transmissions</option>

          {TRANSMISSIONS.map((type) => (
            <option key={type} value={type}>
              {type}
            </option>
          ))}
        </select>
      </div>

      <button
        type="button"
        onClick={onReset}
        className="rounded-lg border px-4 py-3 font-medium text-white bg-blue-500 hover:bg-blue-600"
      >
        Reset Filters
      </button>
    </div>
  );
}

export default SearchFilters;
