# AutoTrader Project Status Report Generator

You are helping maintain continuity for my AutoTrader project.

Your task is to generate a concise but technically accurate `CURRENT_STATUS.md` document describing the **actual state of the project at the end of this conversation**.

The document will be pasted into a future conversation together with:

- `PROJECT_CHARTER.md`
- the latest `FOLDER_STRUCTURE.md`

The purpose of `CURRENT_STATUS.md` is **continuity, not explanation**.

Do not repeat the engineering philosophy or mentoring agreement from `PROJECT_CHARTER.md` unless something has changed.

---

# Source of Truth Rules

The most important requirement is:

> **Describe what actually exists, not what was proposed, discussed, planned, or intended.**

Before generating the report, distinguish carefully between:

### Implemented

Code or functionality that was actually provided, modified, created, tested, or explicitly verified during the conversation.

### Existing

Code or functionality that the user explicitly identified as already existing in the current codebase.

### Planned

Functionality that was discussed as future work but has not been confirmed as implemented.

### Deferred

Functionality deliberately postponed.

### Proposed / Unverified

Code or implementation that was suggested during the conversation but whose presence in the actual codebase was not confirmed.

Do **not** describe proposed or unverified work as completed.

If the conversation contains contradictory information, prefer the **latest concrete evidence**, especially:

1. Actual current source files provided by the user.
2. Actual `FOLDER_STRUCTURE.md` provided by the user.
3. Explicit verification statements from the user.
4. Earlier conversation descriptions.
5. Previous `CURRENT_STATUS.md` documents.

A previous `CURRENT_STATUS.md` is **not proof that code currently exists**.

Never copy completed features from an old status report without checking whether the conversation provides evidence that they remain current.

---

# Folder Structure Requirement

The user wants continuity between conversations without having to repeatedly explain which files exist.

Therefore:

> **Always request the latest `FOLDER_STRUCTURE.md` when generating a status report unless the current conversation already contains it.**

If the user has supplied a current folder structure during this conversation, use it.

The status report should mention important files that were actually created, modified, removed, or relevant during the conversation.

Never invent filenames or assume that a file exists because it existed in an earlier conversation.

---

# Status Accuracy Rules

When describing a feature, ask internally:

1. Was this actually implemented?
2. Was the implementation shown or verified?
3. Is the current implementation different from an earlier proposed implementation?
4. Is the feature partially complete?
5. Is it merely planned?

Use precise language.

For example:

**Correct:**

> Client-side authentication validation exists in `validateAuth.js`, but its integration into the current Login and Register pages still needs verification.

**Incorrect:**

> Authentication validation is complete.

if only a proposed implementation was discussed.

Likewise:

**Correct:**

> The backend supports pagination and the frontend consumes `content` and `totalElements`, but pagination controls have not yet been implemented.

**Incorrect:**

> Frontend pagination is complete.

---

# Phase Tracking

Use the phase structure from the latest `PROJECT_CHARTER.md`.

If the charter's phase description no longer matches the actual state of the project, identify the discrepancy in `CURRENT_STATUS.md`.

If a phase was completed during this conversation:

- explicitly state that it is completed,
- identify the next phase,
- summarize what caused the phase to become complete.

Do not artificially keep a phase open simply because the old status document said it was in progress.

Do not mark a phase complete merely because most of its work exists.

Use the phase completion criteria defined by the current charter.

---

# Required Output Structure

Generate the following document.

---

# CURRENT_STATUS.md

## Current Phase

State the current phase.

If a phase was completed during this conversation:

- explicitly state that,
- state the next phase.

If the current phase contains both completed and unfinished milestones, summarize the exact remaining work.

---

## Project Summary

Briefly summarize the application as it actually exists today.

Keep this to one or two paragraphs.

Do not include planned features as though they already exist.

---

## Completed Phases

List every completed phase.

For each phase:

- provide a short summary,
- mention only functionality actually completed.

Do not rewrite detailed implementation explanations.

---

## Current Architecture

Summarize the architecture that currently exists.

Include only information useful for continuing development.

Possible areas:

- backend modules
- frontend modules
- authentication
- security
- persistence
- storage
- API layer
- routing
- mapping
- validation
- state management
- testing
- deployment

Do not include technologies or patterns merely planned for future use.

---

## Important Architectural Decisions

Summarize significant engineering decisions that future development should respect.

Examples:

- service responsibilities
- ownership authorization
- `AuthContext` as frontend authentication source of truth
- centralized API communication
- manual DTO mapping
- filesystem image storage
- JPA Specifications
- validation architecture
- intentionally postponed abstractions
- intentionally deferred features

Do not repeat the entire engineering philosophy from the charter.

---

## Remaining Roadmap

Summarize the remaining high-level roadmap.

Separate:

- immediate remaining work,
- next phase,
- later phases.

Do not include completed functionality in the remaining roadmap.

Do not turn implementation details into roadmap items unless they represent meaningful remaining work.

---

## Files and Structure Added During This Chat

List important files that were:

- created,
- modified,
- removed,
- or materially changed

during this conversation.

If the current folder structure is available, use it to verify filenames.

Do not invent files.

Do not list every unchanged file.

---

## Concepts Learned During This Chat

Summarize major engineering concepts introduced or reinforced during this conversation.

Focus on ideas rather than code.

Only include concepts actually discussed.

---

## Interview Topics Covered

List questions or concepts the user should be able to explain after this conversation.

Do not answer the questions.

Only include topics actually covered.

---

## Next Recommended Starting Point

State exactly what should happen at the beginning of the next conversation.

Include:

1. Which files should be requested.
2. Whether architecture/theory should happen before implementation.
3. The first implementation milestone.
4. Any important verification that must occur before coding.

If the next step depends on inspecting existing files, say so explicitly.

---

## Notes for Continuation

Include important context that another conversation needs in order to continue naturally.

Examples:

- unfinished discussions
- partially implemented features
- proposed but unverified changes
- intentionally deferred work
- discrepancies between old documentation and current code
- workflow changes
- important implementation constraints

This section is especially important when a previous conversation contained proposed code that may not have been applied.

---

# Continuity Rules

The generated document must allow a new conversation to continue without relying on the previous conversation's memory.

The next conversation should be able to determine:

- what exists,
- what does not exist,
- what is being worked on,
- what was deliberately deferred,
- what should happen next,
- which files must be inspected before modification.

Do not assume the assistant remembers previous conversations.

Do not assume proposed code was applied.

Do not assume filenames.

Do not silently upgrade planned functionality to completed functionality.

---

# Current Folder Structure

If the user has supplied a current `FOLDER_STRUCTURE.md` or equivalent folder structure in this conversation:

- use it as evidence of the current repository structure,
- mention important relevant files,
- preserve the actual filenames and paths.

If it has not been supplied:

> Request the latest `FOLDER_STRUCTURE.md` before generating the final status report.

Do not reconstruct the folder structure from memory.

---

# Status Classification

Use these classifications when useful:

| Classification  | Meaning                                                               |
| --------------- | --------------------------------------------------------------------- |
| **Implemented** | Code exists and implementation is confirmed                           |
| **Verified**    | Functionality was explicitly tested or confirmed working              |
| **Partial**     | Some required functionality exists, but the milestone is unfinished   |
| **Planned**     | Discussed as future work but not implemented                          |
| **Deferred**    | Deliberately postponed                                                |
| **Unverified**  | Proposed or believed to exist but not confirmed against current files |

Use these distinctions whenever they prevent ambiguity.

---

# Final Quality Check

Before producing `CURRENT_STATUS.md`, verify:

- [ ] Current phase matches the latest `PROJECT_CHARTER.md`.
- [ ] Completed phases contain only genuinely completed work.
- [ ] Planned work is not described as implemented.
- [ ] Proposed code is not described as existing without evidence.
- [ ] Current architecture describes only what exists.
- [ ] Remaining roadmap excludes completed work.
- [ ] Important current files are based on the latest folder structure.
- [ ] Contradictions between old documentation and current code are resolved in favor of current concrete evidence.
- [ ] The next conversation has a clear starting point.
- [ ] The report is concise enough to be useful as a handoff document.

The goal is to produce an **accurate engineering handoff**, not a historical transcript.
