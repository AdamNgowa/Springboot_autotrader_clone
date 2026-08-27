import { apiClient } from "./apiClient";

// Create a conversation with the seller of a listing, or return the
// existing one if a conversation for this buyer/seller/listing trio
// already exists (backend enforces this via a unique constraint).
export function createOrGetConversation(listingId) {
  return apiClient("/conversations", {
    method: "POST",
    requiresAuth: true,
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ listingId }),
  });
}

// Fetch the authenticated user's conversations (as buyer or seller), paginated.
export function getMyConversations(page = 0, size = 10) {
  return apiClient(`/conversations?page=${page}&size=${size}`, {
    method: "GET",
    requiresAuth: true,
  });
}

// Fetch a single conversation. Backend rejects this with an authorization
// error if the authenticated user is not a participant.
export function getConversation(conversationId) {
  return apiClient(`/conversations/${conversationId}`, {
    method: "GET",
    requiresAuth: true,
  });
}

// Fetch messages belonging to a conversation, paginated, oldest first.
export function getMessages(conversationId, page = 0, size = 20) {
  return apiClient(
    `/conversations/${conversationId}/messages?page=${page}&size=${size}`,
    {
      method: "GET",
      requiresAuth: true,
    },
  );
}

// Send a message to a conversation. The sender is derived from the JWT
// on the backend, never supplied by the client.
export function sendMessage(conversationId, content) {
  return apiClient(`/conversations/${conversationId}/messages`, {
    method: "POST",
    requiresAuth: true,
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ content }),
  });
}
