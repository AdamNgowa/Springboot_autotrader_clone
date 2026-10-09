// A locally selected image that has not been uploaded yet.
function PendingImageCard({ item, progress, uploading, disabled, onRemove }) {
  return (
    <div className="border border-slate-300 bg-white">
      <div className="aspect-video bg-slate-100">
        <img
          src={item.previewUrl}
          alt={item.file.name}
          className="h-full w-full object-cover"
        />
      </div>

      <div className="space-y-2 p-3">
        <p className="truncate text-sm font-medium">{item.file.name}</p>

        {uploading ? (
          <div>
            <div className="mb-1 flex justify-between text-xs text-slate-500">
              <span>Uploading...</span>
              <span>{progress}%</span>
            </div>

            <div
              role="progressbar"
              aria-valuenow={progress}
              aria-valuemin={0}
              aria-valuemax={100}
              className="h-2 bg-slate-200"
            >
              <div
                className="h-full bg-blue-600 transition-all duration-200"
                style={{ width: `${progress}%` }}
              />
            </div>
          </div>
        ) : (
          <button
            type="button"
            disabled={disabled}
            onClick={onRemove}
            className="btn btn-danger h-9 px-3"
          >
            Remove
          </button>
        )}
      </div>
    </div>
  );
}

export default PendingImageCard;
