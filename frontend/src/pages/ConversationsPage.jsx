import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { getMyConversations } from "../api/messagingApi";
import { useAuth } from "../hooks/useAuth";

const PAGE_SIZE = 10;

function ConversationsPage() {
  const { user } = useAuth();

  const [conversations, setConversations] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalConversations, setTotalConversations] = useState(0);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function loadConversations() {
      setLoading(true);
      setError(null);

      try {
        const data = await getMyConversations(currentPage, PAGE_SIZE);

        setConversations(data.content ?? []);
        setTotalPages(data.totalPages ?? 0);
        setTotalConversations(data.totalElements ?? 0);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    }

    loadConversations();
  }, [currentPage]);

  function goToPreviousPage() {
    if (currentPage > 0) {
      setCurrentPage((page) => page - 1);
    }
  }

  function goToNextPage() {
    if (currentPage < totalPages - 1) {
      setCurrentPage((page) => page + 1);
    }
  }

  return (
    <main className="mx-auto max-w-4xl p-6">
      <h1 className="mb-2 text-3xl font-bold text-slate-900">Messages</h1>

      <p className="mb-6 text-slate-500">
        {totalConversations} conversation
        {totalConversations !== 1 ? "s" : ""}
      </p>

      {error && (
        <div className="mb-6 rounded-lg bg-red-100 p-4 text-red-700">
          Unable to load conversations: {error}
        </div>
      )}

      {loading ? (
        <p className="text-center text-slate-500">Loading conversations...</p>
      ) : conversations.length === 0 ? (
        <div className="rounded-xl border border-slate-200 bg-slate-50 p-8 text-center">
          <p className="text-slate-600">
            You have no conversations yet. Message a seller from a listing page
            to start one.
          </p>
        </div>
      ) : (
        <>
          <div className="flex flex-col gap-3">
            {conversations.map((conversation) => {
              const isBuyer = user && user.id === conversation.buyerId;

              const otherParticipantName = isBuyer
                ? `${conversation.sellerFirstName} ${conversation.sellerLastName}`
                : `${conversation.buyerFirstName} ${conversation.buyerLastName}`;

              return (
                <Link
                  key={conversation.id}
                  to={`/conversations/${conversation.id}`}
                  className="flex flex-col gap-1 rounded-xl border border-slate-200 bg-white p-5 shadow-sm transition hover:border-blue-300 hover:shadow-md"
                >
                  <div className="flex items-center justify-between">
                    <span className="font-semibold text-slate-900">
                      {otherParticipantName}
                    </span>

                    <span className="text-xs text-slate-400">
                      {new Date(conversation.createdAt).toLocaleDateString()}
                    </span>
                  </div>

                  <span className="text-sm text-slate-600">
                    {conversation.listingTitle}
                  </span>
                </Link>
              );
            })}
          </div>

          {totalPages > 1 && (
            <nav
              className="mt-8 flex flex-wrap items-center justify-center gap-2"
              aria-label="Conversation pagination"
            >
              <button
                type="button"
                onClick={goToPreviousPage}
                disabled={currentPage === 0}
                className="rounded-lg border px-4 py-2 disabled:cursor-not-allowed disabled:opacity-50"
              >
                Previous
              </button>

              {Array.from({ length: totalPages }, (_, index) => index).map(
                (pageNumber) => (
                  <button
                    key={pageNumber}
                    type="button"
                    onClick={() => setCurrentPage(pageNumber)}
                    aria-current={
                      currentPage === pageNumber ? "page" : undefined
                    }
                    className={`rounded-lg border px-4 py-2 ${
                      currentPage === pageNumber
                        ? "bg-blue-600 font-medium text-white"
                        : "hover:bg-gray-100"
                    }`}
                  >
                    {pageNumber + 1}
                  </button>
                ),
              )}

              <button
                type="button"
                onClick={goToNextPage}
                disabled={currentPage === totalPages - 1}
                className="rounded-lg border px-4 py-2 disabled:cursor-not-allowed disabled:opacity-50"
              >
                Next
              </button>
            </nav>
          )}
        </>
      )}
    </main>
  );
}

export default ConversationsPage;
