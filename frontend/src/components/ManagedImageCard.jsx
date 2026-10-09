// One already-uploaded image inside ImageManager, with its action buttons.
function ManagedImageCard({
  image,
  imageUrl,
  isPrimary,
  isFirst,
  isLast,
  busy,
  deleting,
  onMakePrimary,
  onMove,
  onDelete,
  onError,
}) {
  return (
    <div className="border border-slate-300 bg-white">
      <div className="relative aspect-video bg-slate-100">
        <img
          src={imageUrl}
          alt={image.originalFilename || "Vehicle"}
          className="h-full w-full object-cover"
          onError={onError}
        />

        {isPrimary && (
          <span className="absolute left-2 top-2 bg-blue-600 px-2 py-1 text-xs font-medium text-white">
            Primary
          </span>
        )}
      </div>

      <div className="space-y-2 p-3">
        <p className="truncate text-sm text-slate-600">
          {image.originalFilename || "Vehicle image"}
        </p>

        <div className="flex flex-wrap gap-2">
          {!isPrimary && (
            <button
              type="button"
              disabled={busy}
              onClick={onMakePrimary}
              className="btn btn-primary h-9 px-3"
            >
              Make primary
            </button>
          )}

          <button
            type="button"
            aria-label="Move image left"
            disabled={busy || isFirst}
            onClick={() => onMove("left")}
            className="btn btn-outline h-9 w-9 px-0"
          >
            ←
          </button>

          <button
            type="button"
            aria-label="Move image right"
            disabled={busy || isLast}
            onClick={() => onMove("right")}
            className="btn btn-outline h-9 w-9 px-0"
          >
            →
          </button>

          <button
            type="button"
            disabled={busy}
            onClick={onDelete}
            className="btn btn-danger ml-auto h-9 px-3"
          >
            {deleting ? "Deleting..." : "Delete"}
          </button>
        </div>
      </div>
    </div>
  );
}

export default ManagedImageCard;
