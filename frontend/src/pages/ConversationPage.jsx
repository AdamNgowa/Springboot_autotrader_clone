import { useEffect, useRef, useState } from "react";
import { useParams, Navigate } from "react-router-dom";

import { getConversation, getMessages, sendMessage } from "../api/messagingApi";
import { useAuth } from "../hooks/useAuth";

const PAGE_SIZE = 30;

function ConversationPage() {
  const { id } = useParams();
  const { user } = useAuth();

  const [conversation, setConversation] = useState(null);
  const [loadingConversation, setLoadingConversation] = useState(true);
  const [conversationError, setConversationError] = useState(null);

  const [messages, setMessages] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loadingMessages, setLoadingMessages] = useState(true);
  const [loadingOlder, setLoadingOlder] = useState(false);
  const [messagesError, setMessagesError] = useState(null);

  const [newMessage, setNewMessage] = useState("");
  const [sending, setSending] = useState(false);
  const [sendError, setSendError] = useState(null);

  const scrollContainerRef = useRef(null);
  const shouldScrollToBottom = useRef(false);

  // Load the conversation (participants, listing) once on mount.
  useEffect(() => {
    if (!id) {
      return;
    }

    async function loadConversation() {
      setLoadingConversation(true);
      setConversationError(null);

      try {
        const data = await getConversation(id);
        setConversation(data);
      } catch (error) {
        setConversationError(error.message);
      } finally {
        setLoadingConversation(false);
      }
    }

    loadConversation();
  }, [id]);

  // On mount, figure out how many pages of messages exist, then land
  // on the LAST page — the most recent messages — like a real chat.
  useEffect(() => {
    if (!id) {
      return;
    }

    async function loadInitialMessages() {
      setLoadingMessages(true);
      setMessagesError(null);

      try {
        const firstPage = await getMessages(id, 0, PAGE_SIZE);
        const lastPageIndex = Math.max((firstPage.totalPages ?? 1) - 1, 0);

        if (lastPageIndex === 0) {
          setMessages(firstPage.content ?? []);
          setCurrentPage(0);
          setTotalPages(firstPage.totalPages ?? 0);
        } else {
          const lastPage = await getMessages(id, lastPageIndex, PAGE_SIZE);
          setMessages(lastPage.content ?? []);
          setCurrentPage(lastPageIndex);
          setTotalPages(lastPage.totalPages ?? 0);
        }

        shouldScrollToBottom.current = true;
      } catch (error) {
        setMessagesError(error.message);
      } finally {
        setLoadingMessages(false);
      }
    }

    loadInitialMessages();
  }, [id]);

  // Scroll to bottom whenever the message list changes because of an
  // initial load or a sent message — but NOT because of "load earlier"
  // (that path manages scroll position itself, see loadEarlierMessages).
  useEffect(() => {
    if (shouldScrollToBottom.current && scrollContainerRef.current) {
      const container = scrollContainerRef.current;
      container.scrollTop = container.scrollHeight;
      shouldScrollToBottom.current = false;
    }
  }, [messages]);

  // All hooks above run unconditionally on every render, satisfying the
  // Rules of Hooks. Only now — after every hook has been declared — do
  // we branch on whether "id" is actually present.
  if (!id) {
    return <Navigate to="/conversations" replace />;
  }

  async function loadEarlierMessages() {
    if (currentPage === 0 || loadingOlder) {
      return;
    }

    const container = scrollContainerRef.current;
    const previousScrollHeight = container ? container.scrollHeight : 0;

    setLoadingOlder(true);
    setMessagesError(null);

    try {
      const olderPage = await getMessages(id, currentPage - 1, PAGE_SIZE);

      setMessages((existing) => [...(olderPage.content ?? []), ...existing]);
      setCurrentPage(currentPage - 1);
      // Preserve the reader's position: after prepending older messages,
      // the container grows taller above the current viewport, so we
      // push scrollTop forward by exactly how much the content grew.
      requestAnimationFrame(() => {
        if (container) {
          const newScrollHeight = container.scrollHeight;
          container.scrollTop = newScrollHeight - previousScrollHeight;
        }
      });
    } catch (error) {
      setMessagesError(error.message);
    } finally {
      setLoadingOlder(false);
    }
  }

  async function handleSend(event) {
    event.preventDefault();

    const trimmed = newMessage.trim();

    if (!trimmed || sending) {
      return;
    }

    try {
      setSending(true);
      setSendError(null);

      const sent = await sendMessage(id, trimmed);

      setMessages((existing) => [...existing, sent]);
      setNewMessage("");
      shouldScrollToBottom.current = true;
    } catch (error) {
      setSendError(error.message);
    } finally {
      setSending(false);
    }
  }

  if (loadingConversation) {
    return (
      <main className="mx-auto max-w-3xl p-6">
        <p className="text-center text-slate-500">Loading conversation...</p>
      </main>
    );
  }

  if (conversationError) {
    return (
      <main className="mx-auto max-w-3xl p-6">
        <div className="rounded-lg bg-red-100 p-4 text-red-700">
          Unable to load conversation: {conversationError}
        </div>
      </main>
    );
  }

  if (!conversation) {
    return (
      <main className="mx-auto max-w-3xl p-6">
        <div className="rounded-lg bg-yellow-100 p-4 text-yellow-700">
          Conversation not found.
        </div>
      </main>
    );
  }

  const isBuyer = user && user.id === conversation.buyerId;

  const otherParticipantName = isBuyer
    ? `${conversation.sellerFirstName} ${conversation.sellerLastName}`
    : `${conversation.buyerFirstName} ${conversation.buyerLastName}`;

  const hasEarlierMessages = currentPage > 0;

  return (
    <main className="mx-auto flex max-w-3xl flex-col gap-6 p-6">
      <header className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
        <p className="text-sm font-medium uppercase tracking-wide text-blue-600">
          Conversation about
        </p>
        <h1 className="text-2xl font-bold text-slate-900">
          {conversation.listingTitle}
        </h1>
        <p className="mt-1 text-slate-600">With {otherParticipantName}</p>
      </header>

      <section className="flex flex-col overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
        {messagesError && (
          <div className="m-4 rounded-lg bg-red-100 p-4 text-red-700">
            {messagesError}
          </div>
        )}

        <div
          ref={scrollContainerRef}
          className="flex h-[60vh] flex-col gap-3 overflow-y-auto p-5"
        >
          {loadingMessages ? (
            <p className="text-center text-slate-500">Loading messages...</p>
          ) : messages.length === 0 ? (
            <p className="text-center text-slate-500">
              No messages yet. Say hello.
            </p>
          ) : (
            <>
              {hasEarlierMessages && (
                <div className="mb-2 flex justify-center">
                  <button
                    type="button"
                    onClick={loadEarlierMessages}
                    disabled={loadingOlder}
                    className="rounded-full border border-slate-300 px-4 py-1.5 text-sm text-slate-600 transition hover:bg-slate-100 disabled:cursor-not-allowed disabled:opacity-50"
                  >
                    {loadingOlder ? "Loading..." : "Load earlier messages"}
                  </button>
                </div>
              )}

              {messages.map((message) => {
                const isMine = user && message.senderId === user.id;

                return (
                  <div
                    key={message.id}
                    className={`flex flex-col ${
                      isMine ? "items-end" : "items-start"
                    }`}
                  >
                    <div
                      className={`max-w-[75%] rounded-2xl px-4 py-2 ${
                        isMine
                          ? "bg-blue-600 text-white"
                          : "bg-slate-100 text-slate-900"
                      }`}
                    >
                      <p className="whitespace-pre-wrap break-words">
                        {message.content}
                      </p>
                    </div>

                    <span className="mt-1 text-xs text-slate-400">
                      {new Date(message.createdAt).toLocaleTimeString([], {
                        hour: "2-digit",
                        minute: "2-digit",
                      })}
                    </span>
                  </div>
                );
              })}
            </>
          )}
        </div>

        <form
          onSubmit={handleSend}
          className="flex gap-3 border-t border-slate-200 p-4"
        >
          <input
            type="text"
            value={newMessage}
            onChange={(event) => setNewMessage(event.target.value)}
            placeholder="Write a message..."
            disabled={sending}
            autoFocus
            className="flex-1 rounded-full border border-slate-300 px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />

          <button
            type="submit"
            disabled={sending || !newMessage.trim()}
            className="rounded-full bg-blue-600 px-5 py-2 font-medium text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {sending ? "..." : "Send"}
          </button>
        </form>
      </section>

      {sendError && (
        <p className="text-sm text-red-600">
          Failed to send message: {sendError}
        </p>
      )}
    </main>
  );
}

export default ConversationPage;
