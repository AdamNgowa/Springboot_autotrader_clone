export function validateListing(listing) {
  const errors = {};

  if (!listing.title.trim()) {
    errors.title = "Title is required";
  }

  if (!listing.description.trim()) {
    errors.description = "Description is required";
  }

  if (!listing.price) {
    errors.price = "Price is required";
  }

  if (!listing.year) {
    errors.year = "Year is required";
  }

  if (!listing.make.trim()) {
    errors.make = "Make is required";
  }

  if (!listing.model.trim()) {
    errors.model = "Model is required";
  }

  if (!listing.mileage) {
    errors.mileage = "Mileage is required";
  }

  if (!listing.city.trim()) {
    errors.city = "City is required";
  }

  if (!listing.fuelType) {
    errors.fuelType = "Fuel type is required";
  }

  if (!listing.transmission) {
    errors.transmission = "Transmission is required";
  }

  if (!listing.bodyType) {
    errors.bodyType = "Body type is required";
  }

  return errors;
}
