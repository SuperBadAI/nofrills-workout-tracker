# No Frills Workout Tracker

A simple Android workout logging app for people who want to replace pen and paper with a fast, distraction-free digital tracker.

---

## Product Requirements Document

### 1. Overview

**No Frills Workout Tracker** is a lightweight workout logging app built for users who want to record exercises quickly during a live workout without the clutter of full-service fitness platforms.

The product is intentionally minimal. Its purpose is to help users log exercises, sets, and weights with as little friction and screen time as possible.

### 2. Problem

Many fitness apps try to be all-in-one platforms. They add social features, nutrition tracking, coaching, wearables, progress gamification, and content layers that increase cognitive load.

For users who simply want to track a workout in the gym, this creates unnecessary friction. The user often needs to fight the app just to record what they did.

Pen and paper still wins for many gym-goers because it is fast, flexible, and distraction-free. This product aims to preserve that simplicity while improving portability, structure, and exportability.

### 3. Product Vision

Create the simplest possible digital replacement for pen-and-paper workout logging.

### 4. Target User

**Primary user:**  
Anyone who wants a simple workout tracker with no distractions.

**User characteristics:**
- Logs workouts in the gym, in the moment
- Does not want social or community features
- Values speed and simplicity over deep analytics
- Wants an easy way to keep and export workout records

### 5. Product Principles

- Fast over feature-rich
- Minimal taps over complex workflows
- Low screen time over high engagement
- Clear logging over motivational gimmicks
- Utility over entertainment

### 6. Goals

The MVP should:

- Allow users to log exercises quickly during a workout
- Reduce friction compared with broader fitness apps
- Minimize screen time required for each action in the logging flow
- Provide a reliable digital record of workouts
- Allow users to download and share workout data for personal logs or third-party trackers

### 7. Non-Goals

This product intentionally does **not** aim to include:

- Nutrition tracking
- Social sharing or community features
- Coaching plans
- Wearable integrations
- Apple Health / Google Fit integrations
- Content, classes, or programming plans
- Any feature outside logging and tracking exercises

### 8. User Problem Statement

“As a user, I want to log my workout quickly and with minimal screen interaction so I can stay focused on training instead of managing an app.”

### 9. Core User Stories

- As a user, I want to enter an exercise by title so I can log what I am doing without navigating unnecessary menus.
- As a user, I want to record sets and weight so I have a clear workout record.
- As a user, I want to add extra sets during a workout so the app reflects how training actually happens.
- As a user, I want to add drop sets so I can log more realistic lifting sessions.
- As a user, I want to download and share my workout data so I can keep personal records or use third-party tools.
- As a user, I want the app to feel faster and less distracting than a typical fitness app.

### 10. Functional Requirements

#### Must-have
- Create or enter exercises by title
- Log multiple sets for an exercise
- Record weight for each set
- Add extra sets dynamically during a workout
- Add drop sets
- Save workout data locally
- Download workout data
- Share workout data externally

#### Nice-to-have later
- Workout history improvements
- Exercise search improvements
- Personal records
- Basic trends over time

### 11. Experience Requirements

The app should:

- Require minimal taps to log a set
- Keep the logging flow obvious and fast
- Reduce unnecessary screen time during use
- Avoid distracting UI patterns
- Feel closer to a notebook than a content platform

### 12. Success Metrics

This product is successful if it improves speed and reduces interaction cost in the workout flow.

**Primary metrics**
- Time to log a set
- Total screen time required through the core logging flow

**Supporting metrics**
- Number of interactions required to log an exercise
- Number of workouts successfully logged per user per week

### 13. Scope Definition

#### In scope
- Exercise logging
- Set tracking
- Weight tracking
- Extra set and drop set support
- Exporting and sharing workout data

#### Out of scope
- Everything not directly related to logging and tracking exercises

### 14. Why this project exists

I built **No Frills Workout Tracker** because I wanted a cleaner alternative to pen and paper.

Most workout apps optimize for feature expansion and engagement. This project optimizes for speed, clarity, and low distraction. It is designed around a simple product belief: for a workout logger, less is often better.

### 15. Open-source intent

This project is also a practical product management artifact. It shows how a focused product can be defined through:
- a clear user problem
- strong scope control
- measurable UX goals
- explicit non-goals
- requirements tied to real user behavior

### 16. Tech

- Android
- Kotlin
- Android Studio
- Local device storage

### 17. Roadmap

- [x] Exercise title input
- [x] Set logging
- [x] Weight tracking
- [x] Extra sets
- [x] Drop sets
- [x] Download workout data
- [x] Share workout data
- [ ] Faster history and retrieval flows
- [ ] Lightweight progress insights
- [ ] Continued UX simplification

### 18. Contribution notes

Contributions are welcome if they support the core philosophy of the product: simple workout tracking with minimal friction.

Before proposing major features, please consider whether they improve the core logging experience or move the app away from its no-frills purpose.
