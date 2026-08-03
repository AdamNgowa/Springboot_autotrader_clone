import { useEffect, useState } from "react";

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
    <form onSubmit={handleSubmit} className="space-y-6">
      {/* Title */}
      <div>
        <label className="block mb-1 font-medium">Title</label>

        <input
          type="text"
          name="title"
          value={formData.title}
          onChange={handleChange}
          className="border rounded-md p-2 w-full"
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
          className="border rounded-md p-2 w-full"
        />
      </div>

      {/* Description */}
      <div>
        <label>Description</label>

        <textarea
          name="description"
          value={formData.description}
          onChange={handleChange}
          rows={4}
          className="border rounded-md p-2 w-full"
        />
      </div>

      {/* When saving is false, render --> <button></button>
      When saving is true , render --> <button disabled></button> */}
      <button
        type="submit"
        disabled={saving}
        className="bg-blue-600 text-white px-5 py-2 rounded-md
        disabled:opacity-50 disabled:cursor-not-allowed"
      >
        {submitText}
      </button>
    </form>
  );
}

export default ListingForm;
