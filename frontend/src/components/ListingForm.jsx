import { useEffect, useState } from "react";

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
    setFormData({
      ...formData,
      [name]: value,
    });

    clearValidationError?.(name);
  }

  function handleSubmit(event) {
    event.preventDefault();

    //Allows the parent widget to receive both the listing data and selected image files
    onSubmit(formData, selectedFiles);
  }

  return (
    <form
      onSubmit={handleSubmit}
      className="space-y-6 rounded border border-slate-300 p-4 shadow-md "
    >
      {/* Title */}
      <div>
        <label className="block mb-1 font-medium">Title</label>

        <input
          type="text"
          name="title"
          value={formData.title}
          disabled={saving}
          onChange={handleChange}
          className="border rounded-md p-2 w-full
          disabled:bg-gray-100
          disabled:text-gray-500
          disabled:cursor-not-allowed"
        />
        {validationErrors.title && (
          <p className="mt-1 text-sm text-red-600">
            {" "}
            {validationErrors.title}{" "}
          </p>
        )}
      </div>

      {/* Price */}
      <div>
        <label className="block mb-1 font-medium">Price</label>

        <input
          type="number"
          name="price"
          value={formData.price}
          onChange={handleChange}
          disabled={saving}
          className="border rounded-md p-2 w-full
             disabled:bg-gray-100
             disabled:text-gray-500
             disabled:cursor-not-allowed"
        />
        {validationErrors.price && (
          <p className="mt-1 text-sm text-red-600">{validationErrors.price}</p>
        )}
      </div>

      {/* Description */}
      <div>
        <label className="block mb-1 font-medium">Description</label>

        <textarea
          name="description"
          value={formData.description}
          onChange={handleChange}
          disabled={saving}
          rows={4}
          className="border rounded-md p-2 w-full
             disabled:bg-gray-100
             disabled:text-gray-500
             disabled:cursor-not-allowed"
        />
        {validationErrors.description && (
          <p className="mt-1 text-sm text-red-600">
            {validationErrors.description}
          </p>
        )}
      </div>

      {/* Year */}
      <div>
        <label className="block mb-1 font-medium">Year</label>

        <input
          type="number"
          name="year"
          disabled={saving}
          value={formData.year}
          onChange={handleChange}
          className="border rounded-md p-2 w-full"
        />
        {validationErrors.year && (
          <p className="mt-1 text-sm text-red-600">{validationErrors.year}</p>
        )}
      </div>

      {/* Make */}
      <div>
        <label className="block mb-1 font-medium">Make</label>

        <input
          type="text"
          name="make"
          disabled={saving}
          value={formData.make}
          onChange={handleChange}
          className="border rounded-md p-2 w-full"
        />
        {validationErrors.make && (
          <p className="mt-1 text-sm text-red-600">{validationErrors.make}</p>
        )}
      </div>

      {/* Model */}
      <div>
        <label className="block mb-1 font-medium">Model</label>

        <input
          type="text"
          name="model"
          disabled={saving}
          value={formData.model}
          onChange={handleChange}
          className="border rounded-md p-2 w-full"
        />
        {validationErrors.model && (
          <p className="mt-1 text-sm text-red-600">{validationErrors.model}</p>
        )}
      </div>

      {/* Mileage */}
      <div>
        <label className="block mb-1 font-medium">Mileage</label>

        <input
          type="number"
          name="mileage"
          disabled={saving}
          value={formData.mileage}
          onChange={handleChange}
          className="border rounded-md p-2 w-full"
        />
        {validationErrors.mileage && (
          <p className="mt-1 text-sm text-red-600">
            {validationErrors.mileage}
          </p>
        )}
      </div>
      {/* City */}
      <div>
        <label className="block mb-1 font-medium">City</label>

        <input
          type="text"
          name="city"
          disabled={saving}
          value={formData.city}
          onChange={handleChange}
          className="border rounded-md p-2 w-full"
        />
        {validationErrors.city && (
          <p className="mt-1 text-sm text-red-600">{validationErrors.city}</p>
        )}
      </div>

      {/* Fuel type */}
      <div>
        <label className="block mb-1 font-medium">Fuel Type</label>

        <select
          name="fuelType"
          value={formData.fuelType}
          onChange={handleChange}
          disabled={saving}
          className="border rounded-md p-2 w-full"
        >
          <option value="">Select fuel type</option>
          {/* For every item in this array, create one <option /> */}
          {FUEL_TYPES.map((fuel) => (
            // key={value}  -- React needs a unique identifier for every item it renders in a list.
            <option key={fuel} value={fuel}>
              {fuel}
            </option>
          ))}
        </select>
        {validationErrors.fuelType && (
          <p className="mt-1 text-sm text-red-600">
            {validationErrors.fuelType}
          </p>
        )}
      </div>

      {/* Body type */}
      <div>
        <label className="block mb-1 font-medium">Body Type</label>
        <select
          name="bodyType"
          value={formData.bodyType}
          onChange={handleChange}
          disabled={saving}
          className="border rounded-md p-2 w-full"
        >
          <option value="">Select body type</option>
          {BODY_TYPES.map((bodyType) => (
            <option key={bodyType} value={bodyType}>
              {bodyType}
            </option>
          ))}
        </select>
        {validationErrors.bodyType && (
          <p className="mt-1 text-sm text-red-600">
            {validationErrors.bodyType}
          </p>
        )}
      </div>

      {/* transmission */}
      <div>
        <label className="block mb-1 font-medium">Transmission</label>
        <select
          name="transmission"
          value={formData.transmission}
          onChange={handleChange}
          disabled={saving}
          className="border rounded-md p-2 w-full"
        >
          <option value="">Select transmission</option>
          {TRANSMISSIONS.map((transmission) => (
            <option key={transmission} value={transmission}>
              {transmission}
            </option>
          ))}
        </select>
        {validationErrors.transmission && (
          <p className="mt-1 text-sm text-red-600">
            {validationErrors.transmission}
          </p>
        )}
      </div>
      {/* files */}
      <div>
        <label className="block mb-1 font-medium">Images</label>
        <input
          type="file"
          multiple
          accept="image/png,image/jpeg,image/webp"
          onChange={handleFileChange}
          disabled={saving}
        />

        <p className="mt-1 text-sm text-gray-500">
          You can select one or more JPEG,PNG or WEBP images
        </p>
      </div>

      {/* When saving is false, render --> <button></button>
      When saving is true , render --> <button disabled></button> */}
      <button
        type="submit"
        disabled={saving}
        className="bg-blue-600 text-white px-5 py-2 rounded-md
        disabled:opacity-50 disabled:cursor-not-allowed hover:bg-blue-700 "
      >
        {saving ? "Saving..." : submitText}
      </button>
    </form>
  );
}

export default ListingForm;
