const BASE_URL = import.meta.env.VITE_API_URL || "";

export function getImageUrl(imageUrl) {
  if (!imageUrl) {
    return "";
  }

  if (
    imageUrl.startsWith("http://") ||
    imageUrl.startsWith("https://") ||
    imageUrl.startsWith("blob:")
  ) {
    return imageUrl;
  }

  return `${BASE_URL}${imageUrl}`;
}
