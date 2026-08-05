import { apiClient } from "./apiClient";

//Upload a single image for an existing file
export function uploadImage(listingId, file) {
  //Instantiates a new FormData object. FormData is a built-in browser API used to construct key-value pairs ,
  // representing form fields and their values.
  // It is specifically designed to send binary data (like files or images) over HTTP using multipart/form-data encoding.
  const formData = new FormData();
  //It adds a new key-value pair to the FormData object.
  // "file" is the key (the parameter name your backend server expects to read, e.g., req.file or @RequestParam("file")).
  // file is the value (the actual binary File or Blob object).
  // This packages the raw binary data of the image alongside file metadata (like file name and MIME type) so the browser can structure it properly during transmission.
  formData.append("file", file);
  return apiClient(`/listings/${listingId}/images`, {
    method: "POST",
    requiresAuth: true,
    body: formData,
  });
}
