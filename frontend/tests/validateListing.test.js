import { describe, expect, it } from "vitest";

import { validateListing } from "../src/utils/validateListing";

describe("validateListing", () => {
  // A factory (not a shared object) so no test can mutate another test's data.
  const validListing = () => ({
    title: "2019 Toyota Corolla",
    description: "Well maintained",
    price: "1850000",
    year: "2019",
    make: "Toyota",
    model: "Corolla",
    mileage: "65000",
    city: "Nairobi",
    fuelType: "PETROL",
    transmission: "AUTOMATIC",
    bodyType: "SEDAN",
  });

  const requiredFields = [
    ["title", "Title is required"],
    ["description", "Description is required"],
    ["price", "Price is required"],
    ["year", "Year is required"],
    ["make", "Make is required"],
    ["model", "Model is required"],
    ["mileage", "Mileage is required"],
    ["city", "City is required"],
    ["fuelType", "Fuel type is required"],
    ["transmission", "Transmission is required"],
    ["bodyType", "Body type is required"],
  ];

  it("returns no errors for a complete listing", () => {
    expect(validateListing(validListing())).toEqual({});
  });

  it.each(requiredFields)("reports %s when it is empty", (field, message) => {
    const errors = validateListing({ ...validListing(), [field]: "" });

    // toEqual on the whole object also proves no OTHER field was flagged.
    expect(errors).toEqual({ [field]: message });
  });

  it.each(["title", "description", "make", "model", "city"])(
    "treats a whitespace-only %s as empty",
    (field) => {
      const errors = validateListing({ ...validListing(), [field]: "   " });

      expect(errors[field]).toBeDefined();
    },
  );

  it("reports every missing field at once", () => {
    const emptyListing = Object.fromEntries(
      requiredFields.map(([field]) => [field, ""]),
    );

    const errors = validateListing(emptyListing);

    expect(Object.keys(errors)).toHaveLength(requiredFields.length);
  });
});
