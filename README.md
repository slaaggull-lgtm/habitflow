# HabitFlow 🌱 🍵




<div align="center">

<img src="https://img.shields.io/badge/version-1.0.0-7BA05B?style=for-the-badge&labelColor=EDE9E1"/>
<img src="https://img.shields.io/badge/Java-17+-5D8A6B?style=for-the-badge&logo=openjdk&logoColor=white&labelColor=EDE9E1"/>
<img src="https://img.shields.io/badge/Spring_Boot-3.2-A8C5A0?style=for-the-badge&logo=springboot&logoColor=white&labelColor=EDE9E1"/>
<img src="https://img.shields.io/badge/SQLite-3-7BA05B?style=for-the-badge&logo=sqlite&logoColor=white&labelColor=EDE9E1"/>
<img src="https://img.shields.io/badge/Render-Deploy-C4D9BC?style=for-the-badge&labelColor=EDE9E1"/>

<br/><br/>

```
  🌱  H a b i t F l o w
  ─────────────────────
  grow every day, one habit at a time
```

**A modern, matcha-inspired habit tracker — Java Spring Boot web app.**  
Track daily habits · earn badges · visualize your growth · deployed on Render.com.

</div>

---

## ✨ Features

- **Daily Check-ins** — Mark habits done with one click
- **Streak Tracking** — Current & longest streak counters
- **10 Achievement Badges** — From First Step 🌱 to Century Club 🏆
- **Statistics** — 30-day activity chart & habit breakdown
- **Smart Categories** — Health, Fitness, Learning, Mindfulness, Productivity & more
- **Customisable** — Name, description, target days, color per habit
- **Matcha Design** — Cream backgrounds, sage greens, fully responsive
- **Java Backend** — Spring Boot + Thymeleaf + JPA + SQLite

---

## 🚀 Deploy to Render (No Installation Required)

### Step 1 — Push to GitHub

1. Go to [github.com](https://github.com) → **New repository** → name it `HabitFlow` → **Create**
2. Download [GitHub Desktop](https://desktop.github.com/) (no terminal needed)
3. Open GitHub Desktop → **Add Existing Repository** → select the unzipped folder
4. Click **Publish Repository** → choose your GitHub account → Publish

### Step 2 — Deploy on Render

1. Go to [render.com](https://render.com) → Sign up free (use GitHub login)
2. Click **New +** → **Web Service**
3. Connect your GitHub account → select the `HabitFlow` repository
4. Render auto-detects the settings from `render.yaml`
5. Click **Create Web Service** → wait ~3 minutes
6. Your app is live at `https://habitflow-xxxx.onrender.com` 🎉

> **Note:** On the free plan, the app sleeps after 15 min of inactivity and takes ~30s to wake up. Upgrade to a paid plan ($7/mo) for always-on.

---

## 🛠 Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Language |
| Spring Boot | 3.2 | Web framework |
| Thymeleaf | 3.x | HTML templates |
| Spring Data JPA | 3.x | Database ORM |
| SQLite | 3.45 | Database |
| Maven | 3.8+ | Build tool |
| Render.com | — | Hosting |

---

## 📁 Project Structure

```
habitflow/
├── src/main/java/com/habitflow/
│   ├── HabitFlowApplication.java        # Entry point
│   ├── model/
│   │   ├── Habit.java                   # Habit entity + Category enum
│   │   ├── HabitLog.java                # Daily completion log
│   │   └── Badge.java                   # Achievement badge
│   ├── repository/
│   │   ├── HabitRepository.java
│   │   ├── HabitLogRepository.java
│   │   └── BadgeRepository.java
│   ├── service/
│   │   ├── HabitService.java            # Business logic & badge awards
│   │   └── DataSeeder.java              # Seeds default habits on startup
│   └── controller/
│       └── HabitController.java         # All web routes
├── src/main/resources/
│   ├── templates/
│   │   ├── fragments/
│   │   │   ├── layout.html              # Sidebar shell
│   │   │   └── habit-card.html          # Reusable card component
│   │   └── pages/
│   │       ├── dashboard.html
│   │       ├── habits.html
│   │       ├── habit-form.html
│   │       ├── statistics.html
│   │       └── badges.html
│   ├── static/css/styles.css            # Matcha theme
│   └── application.properties
├── render.yaml                          # Render deploy config
└── pom.xml
```

---

## 🏅 Badges

| Badge | Requirement |
|-------|-------------|
| 🌱 First Step | Complete first habit |
| 🌿 Week Warrior | 7-day streak |
| 🍃 Fortnight Flow | 14-day streak |
| 🌳 Monthly Master | 30-day streak |
| 🏆 Century Club | 100 completions |
| 🌸 Multi-Habit | 5 active habits |
| ⭐ Perfect Week | All habits complete for a week |
| 🍵 Matcha Zen | 30 days mindfulness |
| 🌅 Early Bird | 7 consistent days |
| 🔥 Comeback Kid | Resume after a break |

---

## 📝 License

MIT License — see [LICENSE](LICENSE)

---

<div align="center">



*Small habits. Big changes. Every single day. 🌱🍵 *
