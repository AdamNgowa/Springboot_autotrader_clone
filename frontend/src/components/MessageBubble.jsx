function MessageBubble({ message, isMine }) {
  return (
    <div className={`flex flex-col ${isMine ? "items-end" : "items-start"}`}>
      <div
        className={`max-w-[80%] border px-3 py-2 ${
          isMine
            ? "border-blue-600 bg-blue-600 text-white"
            : "border-slate-300 bg-white text-slate-900"
        }`}
      >
        <p className="whitespace-pre-wrap break-words">{message.content}</p>
      </div>

      <span className="mt-1 text-xs text-slate-400">
        {new Date(message.createdAt).toLocaleTimeString([], {
          hour: "2-digit",
          minute: "2-digit",
        })}
      </span>
    </div>
  );
}

export default MessageBubble;
