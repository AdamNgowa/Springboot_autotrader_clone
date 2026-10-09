function ListingManagement({ onEdit, onDelete, deleting }) {
  return (
    <div className="border border-blue-600 bg-blue-50 p-4">
      <p className="font-semibold">Manage listing</p>
      <p className="mt-1 text-sm text-slate-600">
        You are the owner of this listing.
      </p>

      <div className="mt-3 flex gap-2">
        <button
          type="button"
          onClick={onEdit}
          disabled={deleting}
          className="btn btn-primary flex-1"
        >
          Edit
        </button>

        <button
          type="button"
          onClick={onDelete}
          disabled={deleting}
          className="btn btn-danger flex-1"
        >
          {deleting ? "Deleting..." : "Delete"}
        </button>
      </div>
    </div>
  );
}

export default ListingManagement;
