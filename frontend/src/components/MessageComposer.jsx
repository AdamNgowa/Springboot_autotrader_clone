function MessageComposer({ value, onChange, onSubmit, sending }) {
  return (
    <form
      onSubmit={onSubmit}
      className="flex gap-2 border-t border-slate-300 bg-white p-3"
    >
      <input
        type="text"
        value={value}
        onChange={(event) => onChange(event.target.value)}
        placeholder="Write a message..."
        aria-label="Message"
        disabled={sending}
        className="field flex-1"
      />

      <button
        type="submit"
        disabled={sending || !value.trim()}
        className="btn btn-primary"
      >
        {sending ? "..." : "Send"}
      </button>
    </form>
  );
}

export default MessageComposer;
