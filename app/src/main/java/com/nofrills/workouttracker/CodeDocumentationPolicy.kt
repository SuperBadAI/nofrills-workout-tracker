package com.nofrills.workouttracker

/**
 * ## Why this file exists
 *
 * **No Frills Workout Tracker** is maintained for clarity and fast iteration. A blanket rule of “comment every physical
 * line of code” (including `import` lines, closing braces, and trivial `return` statements) would:
 * - bury the actual algorithm under noise,
 * - create huge diffs and merge conflicts,
 * - duplicate information already carried by names and types,
 * - and make reviews slower without improving correctness.
 *
 * ## What we do instead (and why)
 *
 * - **File / type KDoc** explains the responsibility of a file or class and how it fits the architecture, because that
 *   context is not visible from a single line.
 * - **Public API KDoc** documents parameters, return values, and failure modes, because callers depend on contracts.
 * - **Inline comments** are reserved for non-obvious behavior (Android platform quirks, threading, Room/CSV edge cases,
 *   unit conversions, and intentional trade-offs), because those are the places where future readers can make mistakes.
 *
 * If you need deeper walkthroughs, prefer tests and small, well-named functions over comment-stacking every token.
 */
internal const val CODE_DOCUMENTATION_POLICY_VERSION = 1
