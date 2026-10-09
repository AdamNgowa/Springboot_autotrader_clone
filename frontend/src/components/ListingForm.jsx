import { useEffect, useState } from "react";

import FormField from "./FormField";
import {
  FUEL_TYPES,
  TRANSMISSIONS,
  BODY_TYPES,
} from "../constants/listingEnums";

function ListingForm({
  initialValues,
  onSubmit,
  submitText,
  saving,
  validationErrors = {},
  clearValidationError,
  showImageUpload = true,
}) {
  const [formData, setFormData] = useState(initialValues);
  const [selectedFiles, setSelectedFiles] = useState([]);

  //Run this effect whenever "initialValues" changes
  useEffect(() => {
    setFormData(initialValues);
  }, [initialValues]);

  function handleFileChange(event) {
    //event.target.files is a FileList not a true js array
    //Array.from() converts it into a normal array,allowing us to then use familiar methods like:
    // .map(),.foreach() .filter() etc
    setSelectedFiles(Array.from(event.target.files));
  }

  function handleChange(event) {
    const { name, value } = event.target;

    setFormData((current) => ({
      ...current,
      [name]: value,
    }));

    clearValidationError?.(name);
  }

  function handleSubmit(event) {
    event.preventDefault();

    //Allows the parent widget to receive both the listing data and selected image files
    onSubmit(formData, selectedFiles);
  }

  // Props every field shares: its value, change handler, disabled state and error.
  function bind(name) {
    return {
      name,
      value: formData[name],
      onChange: handleChange,
      disabled: saving,
      error: validationErrors[name],
    };
  }

  return (
    <form
      onSubmit={handleSubmit}
      className="border border-slate-300 bg-white p-4 sm:p-6"
    >
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <FormField label="Title" {...bind("title")} className="col-span-full" />

        <FormField label="Make" {...bind("make")} />
        <FormField label="Model" {...bind("model")} />
        <FormField
          label="Year"
          type="number"
          inputMode="numeric"
          {...bind("year")}
        />

        <FormField
          label="Mileage (km)"
          type="number"
          inputMode="numeric"
          {...bind("mileage")}
        />
        <FormField
          label="Price (KSh)"
          type="number"
          inputMode="numeric"
          {...bind("price")}
        />
        <FormField label="City" {...bind("city")} />

        <FormField
          as="select"
          label="Fuel type"
          options={FUEL_TYPES}
          placeholder="Select fuel type"
          {...bind("fuelType")}
        />
        <FormField
          as="select"
          label="Body type"
          options={BODY_TYPES}
          placeholder="Select body type"
          {...bind("bodyType")}
        />
        <FormField
          as="select"
          label="Transmission"
          options={TRANSMISSIONS}
          placeholder="Select transmission"
          {...bind("transmission")}
        />

        <FormField
          as="textarea"
          label="Description"
          rows={5}
          {...bind("description")}
          className="col-span-full"
        />

        {/* files */}
        {showImageUpload && (
          <div className="col-span-full">
            <label
              htmlFor="listing-images"
              className="mb-1.5 block text-sm font-medium text-slate-700"
            >
              Images
            </label>

            <input
              id="listing-images"
              type="file"
              multiple
              accept="image/png,image/jpeg,image/webp"
              onChange={handleFileChange}
              disabled={saving}
              className="block w-full border border-slate-300 text-sm file:mr-3 file:cursor-pointer file:border-0 file:bg-slate-900 file:px-4 file:py-2.5 file:text-sm file:font-medium file:text-white hover:file:bg-blue-600"
            />

            <p className="mt-1 text-xs text-slate-500">
              {selectedFiles.length > 0
                ? `${selectedFiles.length} image${
                    selectedFiles.length === 1 ? "" : "s"
                  } selected`
                : "You can select one or more JPEG, PNG or WEBP images"}
            </p>
          </div>
        )}

        {/* When saving is false, render --> <button></button>
        When saving is true , render --> <button disabled></button> */}
        <div className="col-span-full">
          <button
            type="submit"
            disabled={saving}
            className="btn btn-primary w-full sm:w-auto"
          >
            {saving ? "Saving..." : submitText}
          </button>
        </div>
      </div>
    </form>
  );
}

export default ListingForm;
