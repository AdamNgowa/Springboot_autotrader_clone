import { useEffect, useRef, useState } from "react";
import { useParams, Navigate, Link } from "react-router-dom";

import { getConversation, getMessages, sendMessage } from "../api/messagingApi";
import { useAuth } from "../hooks/useAuth";
import MessageBubble from "../components/MessageBubble";
import MessageComposer from "../components/MessageComposer";
import Notice from "../components/Notice";

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
      <main className="mx-auto max-w-3xl px-4 py-6">
        <div className="h-24 animate-pulse border border-slate-200 bg-slate-100" />
      </main>
    );
  }

  if (conversationError) {
    return (
      <main className="mx-auto max-w-3xl px-4 py-6">
        <Notice variant="error">
          Unable to load conversation: {conversationError}
        </Notice>
      </main>
    );
  }

  if (!conversation) {
    return (
      <main className="mx-auto max-w-3xl px-4 py-6">
        <Notice variant="warning">Conversation not found.</Notice>
      </main>
    );
  }

  const isBuyer = user && user.id === conversation.buyerId;

  const otherParticipantName = isBuyer
    ? `${conversation.sellerFirstName} ${conversation.sellerLastName}`
    : `${conversation.buyerFirstName} ${conversation.buyerLastName}`;

  const hasEarlierMessages = currentPage > 0;

  return (
    // 3.5rem is the navbar height, so the chat fills exactly the rest of the screen.
    <main className="mx-auto flex h-[calc(100dvh-3.5rem)] max-w-3xl flex-col bg-white md:border-x md:border-slate-300">
      <header className="flex items-center gap-3 border-b border-slate-300 p-3">
        <Link
          to="/conversations"
          aria-label="Back to messages"
          className="flex h-9 w-9 shrink-0 items-center justify-center border border-slate-300 hover:bg-slate-100"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            className="h-5 w-5"
            aria-hidden="true"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="m15.75 19.5-7.5-7.5 7.5-7.5"
            />
          </svg>
        </Link>

        <div className="min-w-0">
          <h1 className="truncate font-semibold text-slate-900">
            {conversation.listingTitle}
          </h1>
          <p className="truncate text-sm text-slate-500">
            With {otherParticipantName}
          </p>
        </div>
      </header>

      {messagesError && (
        <Notice variant="error" className="m-3">
          {messagesError}
        </Notice>
      )}

      {/* min-h-0 lets this flex child shrink and scroll instead of growing the page. */}
      <div
        ref={scrollContainerRef}
        className="flex min-h-0 flex-1 flex-col gap-3 overflow-y-auto bg-slate-50 p-4"
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
                  className="btn btn-outline h-9 text-xs"
                >
                  {loadingOlder ? "Loading..." : "Load earlier messages"}
                </button>
              </div>
            )}

            {messages.map((message) => (
              <MessageBubble
                key={message.id}
                message={message}
                isMine={Boolean(user && message.senderId === user.id)}
              />
            ))}
          </>
        )}
      </div>

      {sendError && (
        <p className="border-t border-slate-200 bg-white px-4 py-2 text-sm text-red-600">
          Failed to send message: {sendError}
        </p>
      )}

      <MessageComposer
        value={newMessage}
        onChange={setNewMessage}
        onSubmit={handleSend}
        sending={sending}
      />
    </main>
  );
}

export default ConversationPage;
