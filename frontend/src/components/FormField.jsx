/*
 * One labelled form control: label + control + hint + error message.
 *
 * - as:          "input" (default) | "select" | "textarea"
 * - options:     array of strings (select only)
 * - placeholder: for a select it becomes the empty first option
 * - className:   applied to the wrapper (use it for grid column spans)
 * - everything else (value, onChange, type, disabled...) goes to the control
 */
function FormField({
  label,
  name,
  error,
  hint,
  as = "input",
  options = [],
  placeholder,
  className = "",
  ...controlProps
}) {
  const id = controlProps.id ?? name;
  const errorId = `${id}-error`;

  const sharedProps = {
    id,
    name,
    "aria-invalid": Boolean(error),
    "aria-describedby": error ? errorId : undefined,
    ...controlProps,
  };

  // .field comes from index.css; a textarea needs its height released.
  const controlClass = `field ${as === "textarea" ? "h-auto py-2" : ""} ${
    error ? "border-red-500" : ""
  }`;

  return (
    <div className={className}>
      <label
        htmlFor={id}
        className="mb-1.5 block text-sm font-medium text-slate-700"
      >
        {label}
      </label>

      {as === "select" ? (
        <select {...sharedProps} className={controlClass}>
          {placeholder && <option value="">{placeholder}</option>}

          {options.map((option) => (
            <option key={option} value={option}>
              {option}
            </option>
          ))}
        </select>
      ) : as === "textarea" ? (
        <textarea
          {...sharedProps}
          placeholder={placeholder}
          className={controlClass}
        />
      ) : (
        <input
          {...sharedProps}
          placeholder={placeholder}
          className={controlClass}
        />
      )}

      {hint && !error && <p className="mt-1 text-xs text-slate-500">{hint}</p>}

      {error && (
        <p id={errorId} className="mt-1 text-sm text-red-600">
          {error}
        </p>
      )}
    </div>
  );
}

export default FormField;
