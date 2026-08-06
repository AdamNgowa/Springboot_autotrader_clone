function ImageGallery({ images, selectedImage, setSelectedImage, title }) {
  return (
    <>
      <div className="aspect-[16/9] w-full overflow-hidden rounded-xl bg-slate-200 shadow-sm">
        {selectedImage ? (
          <img
            src={`http://localhost:8080${selectedImage.imageUrl}`}
            alt={title}
            className="h-full w-full object-cover transition-opacity duration-200"
          />
        ) : (
          <div className="flex h-full items-center justify-center text-slate-500">
            No image available
          </div>
        )}
      </div>

      {images.length > 1 && (
        <div className="flex gap-3 overflow-x-auto pb-2">
          {images.map((image) => (
            <button
              key={image.id}
              type="button"
              onClick={() => setSelectedImage(image)}
              className={`overflow-hidden rounded-lg border-2 transition-all duration-200
                hover:scale-105 hover:opacity-80
                ${
                  selectedImage?.id === image.id
                    ? "border-blue-600"
                    : "border-transparent"
                }`}
            >
              <img
                src={`http://localhost:8080${image.imageUrl}`}
                alt={title}
                className="h-20 w-28 object-cover"
              />
            </button>
          ))}
        </div>
      )}
    </>
  );
}

export default ImageGallery;
