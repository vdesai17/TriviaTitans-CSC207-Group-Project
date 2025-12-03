---

# TriviaTitans — CSC207 Group Project

*A Java Swing Desktop Trivia Game Implementing Clean Architecture*

---

## 📌 Overview

**Trivia Titans** is a full-featured Java desktop trivia game built for the Fall 2025 CSC207: Software Design course.
It uses **Java Swing** for the UI, integrates the **Open Trivia DB API**, and follows **Clean Architecture** with strict adherence to **SOLID principles** and **Dependency Injection**.

The system supports:

* Registering or logging in
* Fetching quizzes from the Open Trivia DB API
* Creating custom quizzes locally
* Selecting category, difficulty, and number of questions
* Completing quizzes and viewing summaries
* Reviewing past quizzes and redoing them
* Generating new quizzes from previously incorrect answers
* Viewing profile statistics and quiz history
* Saving quizzes and attempts locally

---

## 📁 Project Structure (Clean Architecture)

```
trivia/
 ├─ entity/                     # Enterprise business rules
 ├─ use_case/                   # Application-specific business logic
 │   ├─ register_player/
 │   ├─ select_quiz/
 │   ├─ load_quiz/
 │   ├─ complete_quiz/
 │   ├─ review_summary/
 │   ├─ review_quiz/
 │   ├─ create_quiz/
 │   └─ generate_from_wrong/
 │
 ├─ interface_adapter/          # Converts data for use cases & UI
 │   ├─ controller/
 │   ├─ presenter/
 │   ├─ view_model/
 │   ├─ dao/
 │   └─ api/
 │
 └─ framework/
     └― ui/                     # Swing screens, app entry point
```

### Layer Responsibilities

* **Entities:** Core business objects (`Player`, `Question`, `Quiz`, `QuizAttempt`)
* **Use Case Interactors:** Contain all application logic using boundaries
* **Interface Adapters:** Controllers, Presenters, ViewModels, DAOs, API adapters
* **Framework/UI:** Swing UI components and theme utilities

---

# 🎮 Features

### ✔ Player Registration & Login

### ✔ API Quiz Generation

### ✔ Category & Difficulty Selection

### ✔ Custom Quiz Creation

### ✔ Quiz Summary (Score + Accuracy)

### ✔ Review Past Quizzes

### ✔ Redo Any Past Quiz

### ✔ Generate Quiz from Wrong Answers

### ✔ Local Persistence

### ✔ Unified Theme Across Screens

---

# 🌐 API Integration (Open Trivia DB)

The system uses a dedicated adapter (`APIManager`) to fetch trivia questions. It:

* Builds HTTP requests using `HttpURLConnection`
* Parses JSON using Gson
* Converts API responses into `Question` entities
* Randomizes options
* Supports category/difficulty filters

### Fallback Mechanism

If the API request fails, the system gracefully falls back to **locally stored quizzes** using the `QuizDataAccessObject`.

### Architecture Compliance

Use cases depend on the **`SelectQuizAPIDataAccessInterface`**, not the APIManager.
This keeps the interactor decoupled and testable.

---

# 🧱 Detailed Use Cases

Below is an extended description of all use cases implemented in the system.

---

## 1️⃣ Review Quiz Summary

### User Story

> “As a quiz reviewer, I want to see a summary of my score and accuracy at the end of the quiz.”

### Responsibilities

* Receive score and total number of questions
* Compute accuracy
* Return summary as formatted strings

### Example Accuracy Calculation

```java
int accuracy = (int) Math.round((double) score / totalQuestions * 100);
```

### Clean Architecture Flow

1. UI → Controller
2. Controller → Interactor
3. Interactor → Presenter
4. Presenter → View Model
5. View Model → UI update

---

## 2️⃣ Select Quiz Category & Difficulty

### User Story

> “As a quiz taker, I want to select quiz category and difficulty before starting.”

### Key Interactor Validations

* Category must not be empty
* Difficulty must not be empty
* Number of questions must be > 0

If validation fails:

```java
presenter.prepareFailView("Invalid input");
```

### Actions

* Validate input
* Request API questions
* Return a ready-to-play quiz

---

## 3️⃣ Review & Redo Past Quizzes

### User Story

> “As a quiz taker, I want to review past quizzes and redo them.”

### Functionality

* Display all previous QuizAttempts
* Show:

    * Questions
    * User-selected answers
    * Correct answers
    * Correctness indicators
* Allow user to redo any past quiz

### Architecture

* DAO loads all attempts for a player
* Interactor transforms data to output format
* Presenter formats view-ready content
* UI displays quiz attempts and details

---

## 4️⃣ Load Quiz After Selection

### User Story

> “As a quiz taker, I want to load and play the quiz I selected with my chosen options.”

### Flow

1. User selects:

    * Category
    * Difficulty
    * Number of questions
2. Controller forwards selection to interactor
3. Interactor loads:

    * API questions
    * or local quizzes if API fails
4. Presenter creates a ViewModel
5. UI displays `QuizScreen`

### Reliability

The constructor checks:

```java
if (quizDataAccess == null) throw new IllegalArgumentException("QuizDataAccess cannot be null");
```

---

## 5️⃣ Create, Edit & Save Custom Quizzes

### User Story

> “As a quiz creator, I want to create and edit custom quizzes and save them.”

### UI Steps

1. Homepage → Create Custom Quiz
2. Add Question dialog
3. Add multiple questions
4. Save quiz
5. Return to HomeScreen

### Interactor Validations

* Quiz title cannot be empty
* Creator name cannot be empty
* Must contain ≥ 1 question
* Each question must have:

    * Non-empty text
    * Valid options
    * Valid correct option index

### Interactor Duties

* Verify entire quiz
* Construct Question entities
* Generate unique IDs
* Save quiz through DAO
* Return success/failure via presenter

---

## 6️⃣ Generate Quiz From Wrong Answers

### User Story

> “As a quiz taker, I want a new quiz generated from questions I answered incorrectly.”

### Responsibilities

* Extract all incorrect answers from past attempts
* Compile them into a new `Quiz` entity
* Return ready-to-play quiz

### Clean Architecture Strength

* UI depends only on controller
* Controller → Interactor (via InputData)
* Interactor uses `GenerateFromWrongQuizDataAccessInterface`
* Presenter updates ViewModel
* UI observes ViewModel

This isolates business logic from UI and persistence.

---

# 🧩 SOLID Principles

## ✔ Single Responsibility Principle (SRP)

Examples:

* Each interactor manages **exactly one** use case
* API layer handles **only** API communication
* DAO handles **only** data persistence
* UI handles **only** user interaction

Benefits:

* High modularity
* Easier debugging
* Predictable behavior

---

## ✔ Dependency Inversion Principle (DIP)

Interactors:

* Depend on **interfaces**, not concrete classes
* Receive dependencies via **constructor injection**

Benefits:

* Testability
* Pluggable storage / API
* No hard-coded dependencies

---

# 🧪 Dependency Injection (DI)

A centralized **AppFactory** builds:

* Controllers
* Presenters
* Interactors
* DAOs
* ViewModels

### Why?

* UI does *not* construct dependencies
* Centralized configuration
* Easy swapping with mocks
* Reduced coupling
* Preserves Clean Architecture boundaries

---

# 🧹 Code Quality Practices

The team enforced:

* Separate branches per feature
* Peer review for all major Pull Requests
* Clean Architecture compliance checks
* Checkstyle for formatting and style
* Manual testing through UI
* Automated testing for interactors where applicable

---

# ▶️ Running the Project

## Option 1 — IntelliJ IDEA (Recommended)

1. Open the project
2. Locate `Main.java` under `framework/ui`
3. Run the main method

## Option 2 — Command Line

```bash
javac -d out $(find src -name "*.java")
java -cp out trivia.framework.ui.Main
```

---

# 🚀 Future Enhancements

* Online quiz database
* Multi-user authentication
* Global leaderboard
* Export/import quiz sets
* Light/Dark UI themes
* Improved statistical dashboard
* Cloud-synced player profiles

---

# 👥 Contributors

* Vivan Desai
* Kayin
* Hannah
* Vic
* Eason
* Herui Zhang

---






















