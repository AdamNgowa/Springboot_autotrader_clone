import { useEffect, useState } from "react";

import {
  FUEL_TYPES,
  TRANSMISSIONS,
  BODY_TYPES,
} from "../constants/listingEnums";

function ListingForm({ initialValues, onSubmit, submitText, saving }) {
  const [formData, setFormData] = useState(initialValues);

  //Run this effect whenever "initialValues" changes
  useEffect(() => {
    setFormData(initialValues);
  }, [initialValues]);

  //Whenever a user types into an input or text area, react passes and event object
  //It contains details about what just happened. e.g key presses, mouse clicks or maybe, which html element triggered the change
  //event.target - refers to the exact input element the user typed into

  function handleChange(event) {
    //const {name,value } = event.target is the same as const name = event.target.name
    // or const value = event.target.value;
    const { name, value } = event.target;
    //setFormData updates the stored memory(Changes the data inside) and tells react to re-render the screen
    setFormData({
      //Copies already present data into the setformdata so it can be available for the re-render
      //
      ...formData,
      //[name]:value - computed property name
      //Dynamically assigns the key name,in the form if for example name="title",
      //[name] becomes ["title"] so finally [name]:value becomes "title":"Marcopolo g7"
      [name]: value,
      //The result of all this should ,for example,look something like:
      /* 
      {
            title: "Apartment",       // Preserved by ...formData
            description: "Nice view", // Preserved by ...formData
            ["price"]: "100"          // Dynamically sets price: "100"
}
      */
    });
  }

  function handleSubmit(event) {
    event.preventDefault();

    onSubmit(formData);
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
      </div>

      {/* Description */}
      <div>
        <label>Description</label>

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
