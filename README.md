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
<img width="1344" height="2992" alt="Screenshot_20250422_140210" src="https://github.com/user-attachments/assets/02fecc0f-8a26-46f0-9173-2f2edee19e6d" />
<img width="1344" height="2992" alt="Screenshot_20250422_140108" src="https://github.com/user-attachments/assets/e7b87414-b93e-4e29-be41-d19f19da2007" />
<img width="1344" height="2992" alt="Screenshot_20250422_140101" src="https://github.com/user-attachments/assets/25863480-361e-4df1-87ca-e859dadde29a" />
<img width="1344" height="2992" alt="Screenshot_20250422_140049" src="https://github.com/user-attachments/assets/94d31ed5-979e-4f54-8a38-7a5db0100d00" />
<img width="1344" height="2992" alt="Screenshot_20250422_140034" src="https://github.com/user-attachments/assets/b99b8695-b499-4106-861d-dd8b9da67aa0" />
<img width="1344" height="2992" alt="Screenshot_20250422_140027" src="https://github.com/user-attachments/assets/f20b3832-ed7a-429f-af69-38a16d407a22" />
<img width="1344" height="2992" alt="Screenshot_20250422_140020" src="https://github.com/user-attachments/assets/3a9a665e-f4cc-4117-9bbb-8204cd802ee8" />
<img width="1344" height="2992" alt="Screenshot_20250422_140012" src="https://github.com/user-attachments/assets/6c907647-1e9a-4300-9ee5-6e549c77e554" />
<img width="1344" height="2992" alt="Screenshot_20250422_140002" src="https://github.com/user-attachments/assets/b8dc00b4-ec39-48ff-a28a-0e575a316db1" />
<img width="1344" height="2992" alt="Screenshot_20250422_135942" src="https://github.com/user-attachments/assets/38d5b5af-7f89-47e0-b1cd-3d6ac4c16f98" />
<img width="1344" height="2992" alt="Screenshot_20250422_140228" src="https://github.com/user-attachments/assets/e1626752-f7ff-4ae7-bc76-189b588c7273" />
