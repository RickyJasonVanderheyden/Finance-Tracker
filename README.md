# Finance Tracker

Finance Tracker is a modern, intuitive Android application for tracking personal income, expenses, and budgets. It empowers users to take control of their financial habits by providing clear insights into their transactions, category breakdowns, and monthly budget status.

---

## Features

- **Add, Edit, and Delete Transactions**
  - Record income or expense transactions with title, amount, category, and date.
  - Edit or remove existing transactions easily.
- **Categorized Transactions**
  - Assign transactions to categories for both income and expenses.
  - View breakdowns by category to analyze spending and earning habits.
- **Monthly Budget Management**
  - Set and update a monthly budget.
  - Visual budget consumption tracker with warnings as you approach or exceed your budget.
- **Summary Dashboard**
  - View total income, total expenses, and remaining budget for the month.
  - Quick access to recent transactions.
  - At-a-glance budget status and progress bar.
- **Notifications**
  - Budget consumption notifications (when you approach or exceed set thresholds).
- **Persistent Storage**
  - All data is saved locally using Room Database.
- **Intuitive UI**
  - Bottom navigation for Home, Activity (all transactions), Budget, and Settings.
  - Modern Android best practices (ViewModel, LiveData, RecyclerViews).

---

## Technical Requirements

- **Android Studio**: Hedgehog or newer recommended
- **Android SDK**: Min SDK 21 (Android 5.0 Lollipop) or higher
- **Kotlin**: Main development language
- **Libraries/Frameworks**:
  - AndroidX
  - Room Database
  - LiveData & ViewModel (MVVM architecture)
  - Material Components
- **Permissions**:
  - Notification permissions (required for budget alerts)

---

## Installation

1. **Clone the Repository**

   ```bash
   git clone https://github.com/RickyJasonVanderheyden/Finance-Tracker.git
   cd Finance-Tracker
   ```

2. **Open in Android Studio**

   - Launch Android Studio.
   - Select `Open an existing project` and choose the `Finance-Tracker` directory.

3. **Sync Gradle & Build**

   - Let Android Studio sync Gradle files.
   - If prompted, install any missing SDK versions or tools.

4. **Run the App**

   - Connect an Android device or start an emulator.
   - Click "Run" (green arrow) in Android Studio.

---

## Build & Run Notes

- **First Run**: The app creates a local database for transactions and preferences.
- **Permissions**: The app will request notification permissions on first launch (Android 13+).
- **Debugging**: Logs and errors are visible in Logcat.
- **Testing**: You can add, edit, and delete transactions, set budgets, and observe UI updates in real-time.

---

## Project Structure

- `app/src/main/java/`: Contains Kotlin source code for app logic, including activities, fragments, and utility classes.
- `app/src/main/res/layout/`: XML layout files for the user interface.
- `app/src/main/res/values/`: Resource files for strings, colors, and themes (including dark theme support).
- `app/build.gradle`: Gradle configuration for dependencies and build settings.

---

## Contribution

This project was developed as part of a coursework assignment. Contributions are welcome for bug fixes or additional features. To contribute:

1. **Fork** the repository.
2. **Create a new branch**  
   ```bash
   git checkout -b feature-branch
   ```
3. **Make your changes and commit**  
   ```bash
   git commit -m "Add feature"
   ```
4. **Push to your branch**  
   ```bash
   git push origin feature-branch
   ```
5. **Create a pull request** on GitHub.

---

## Acknowledgments

- Faculty of Computing, SLIIT, for providing the project guidelines and evaluation criteria.
- Android Developer Documentation for Kotlin and XML best practices.
- Open-source libraries used in the project (listed in `app/build.gradle`).

---

## Contact

For inquiries or feedback, please contact:  
**rickyjason83@gmail.com**

---
