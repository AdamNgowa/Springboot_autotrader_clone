import { useParams } from "react-router-dom";
function EditListingPage() {
  const { id } = useParams();
  return (
    <main className="max-w-4xl mx-auto p-6">
      <h1 className="text-3xl font-bold">Edit listing</h1>
      <p>
        Edit listing with id: <strong>{id}</strong>{" "}
      </p>
    </main>
  );
}
export default EditListingPage;
