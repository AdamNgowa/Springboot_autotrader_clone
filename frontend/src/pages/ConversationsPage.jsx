import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { getMyConversations } from "../api/messagingApi";
import { useAuth } from "../hooks/useAuth";
import Notice from "../components/Notice";
import Pagination from "../components/Pagination";

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

  return (
    <main className="mx-auto max-w-4xl px-4 py-6">
      <h1 className="text-2xl font-bold">Messages</h1>

      <p className="mb-6 mt-1 text-sm text-slate-500">
        {totalConversations} conversation{totalConversations !== 1 ? "s" : ""}
      </p>

      {error && (
        <Notice variant="error" className="mb-4">
          Unable to load conversations: {error}
        </Notice>
      )}

      {loading ? (
        <div className="flex flex-col gap-2">
          {Array.from({ length: 4 }, (_, index) => (
            <div
              key={index}
              className="h-[74px] animate-pulse border border-slate-200 bg-slate-100"
            />
          ))}
        </div>
      ) : conversations.length === 0 ? (
        <div className="border border-slate-300 p-8 text-center">
          <p className="font-medium">No conversations yet</p>
          <p className="mt-1 text-sm text-slate-500">
            Message a seller from a listing page to start one.
          </p>
          <Link to="/" className="btn btn-primary mt-4">
            Browse vehicles
          </Link>
        </div>
      ) : (
        <>
          <div className="flex flex-col gap-2">
            {conversations.map((conversation) => {
              const isBuyer = user && user.id === conversation.buyerId;

              const otherParticipantName = isBuyer
                ? `${conversation.sellerFirstName} ${conversation.sellerLastName}`
                : `${conversation.buyerFirstName} ${conversation.buyerLastName}`;

              const initial =
                otherParticipantName.trim()[0]?.toUpperCase() ?? "?";

              return (
                <Link
                  key={conversation.id}
                  to={`/conversations/${conversation.id}`}
                  className="flex items-center gap-3 border border-slate-300 bg-white p-4 transition-colors hover:border-blue-600 hover:bg-blue-50"
                >
                  <span className="flex h-10 w-10 shrink-0 items-center justify-center bg-blue-600 font-medium text-white">
                    {initial}
                  </span>

                  <div className="min-w-0 flex-1">
                    <div className="flex items-baseline justify-between gap-2">
                      <span className="truncate font-semibold">
                        {otherParticipantName}
                      </span>

                      <span className="shrink-0 text-xs text-slate-400">
                        {new Date(conversation.createdAt).toLocaleDateString()}
                      </span>
                    </div>

                    <p className="truncate text-sm text-slate-600">
                      {conversation.listingTitle}
                    </p>
                  </div>
                </Link>
              );
            })}
          </div>

          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={setCurrentPage}
            label="Conversation pagination"
          />
        </>
      )}
    </main>
  );
}

export default ConversationsPage;
