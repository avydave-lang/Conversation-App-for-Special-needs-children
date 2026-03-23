# Conversation App for Special Needs Children

A native Android app that helps children with special needs build real-world conversational skills through guided, voice-based practice. No internet connection required for the core experience — no AI, no cloud, no data collection.

---

## Who Is This For?

This app is designed for children aged 10–16 with special needs (autism spectrum, social communication difficulties, language delays) who are working on practical conversation skills. It is particularly suited for a child who has strong vocabulary but needs structured practice with the back-and-forth of real conversations.

The app was built with a specific child in mind: a 15-year-old with approximately a 10-year-old conversational skill level.

---

## What It Does

The child speaks into the microphone and the app listens, responds, and guides them through realistic conversation scenarios — like ordering food, asking a teacher for help, or comforting a friend. All prompts are spoken aloud by the app using text-to-speech.

- The app asks a question or sets a scene
- The child responds by speaking
- The app recognises key words in the response and moves the conversation forward
- If the child struggles, the app gently offers a second chance, then models the correct response — without ever saying the child was "wrong"
- Completing a scenario earns a star

---

## Key Features

- **Voice only** — no reading or typing required; everything is spoken and listened to
- **50 real-world scenarios** covering school, home, community, friendships, and emotions
- **8 acts per scenario** — each conversation has depth and progresses through multiple stages
- **Non-punitive** — the app never uses the word "wrong"; teaching nodes always say "No problem! You could say..."
- **Star reward system** — one star per completed scenario, tracked across sessions
- **Caregiver settings** — adjust speech speed, font size, mic mode, and contrast
- **No LLM, no internet, no data collection** — all logic is rule-based and runs entirely on device

---

## Scenarios (50 Total)

### Social & School (15)
| # | Scenario | Skill Practised |
|---|----------|----------------|
| 1 | Meeting a New Classmate | Introductions, name exchange |
| 2 | Asking a Teacher for Help | "Excuse me", clear request |
| 3 | Joining a Group Project | Asking to join, offering help |
| 4 | Borrowing a Classmate's Pen | Polite ask, thank you, return |
| 5 | Apologising to a Friend | "I'm sorry", explaining, making up |
| 6 | Saying No Politely | Declining, offering an alternative |
| 7 | Giving a Compliment | Noticing, expressing honestly |
| 8 | Accepting a Compliment | "Thank you", not deflecting |
| 9 | Asking Someone to Repeat | "Sorry, could you say that again?" |
| 10 | Sharing Your Opinion in Class | "I think…", backing up a view |
| 11 | Disagreeing Politely | "I see it differently…" |
| 12 | Accepting Feedback | Listening, not arguing, thanking |
| 13 | Asking to Sit at Lunch | "Is this seat taken?" |
| 14 | Telling a Teacher About Late Work | Owning up, explaining, asking |
| 15 | Asking for More Time | Polite request, giving a reason |

### Emotions & Self-Advocacy (10)
| # | Scenario | Skill Practised |
|---|----------|----------------|
| 16 | Expressing Frustration Calmly | "I feel frustrated because…" |
| 17 | Asking for a Break | Recognising need, asking clearly |
| 18 | Saying You Don't Understand | "I don't get it, can you help?" |
| 19 | Setting a Boundary Kindly | "I'm not comfortable with that" |
| 20 | Asking for Some Space | "I need a moment to myself" |
| 21 | Saying You Feel Overwhelmed | Naming the feeling, asking for help |
| 22 | Talking About Feeling Left Out | Expressing honestly, asking to be included |
| 23 | Asking Someone to Forgive You | Genuine apology, no excuses |
| 24 | Forgiving a Friend | Accepting apology, moving forward |
| 25 | Trying Something New | Self-encouragement, expressing nerves |

### Community & Daily Life (15)
| # | Scenario | Skill Practised |
|---|----------|----------------|
| 26 | Buying Something at a Store | Finding an item, paying, thank you |
| 27 | At the Doctor | Describing symptoms, answering questions |
| 28 | Making a Phone Call | Hello, stating purpose, goodbye |
| 29 | At the Dentist | Check-in, describing discomfort |
| 30 | Asking the Librarian for Help | Finding a book, checking out |
| 31 | Taking the Bus | Asking route, paying, thank you |
| 32 | Picking Up Medicine | Giving name, asking questions |
| 33 | Returning Something to a Store | Explaining reason, politely |
| 34 | Asking for Directions | Clear request, following the response |
| 35 | Getting a Haircut | Describing what you want |
| 36 | Picking Up a Food Order | Giving name, confirming order |
| 37 | Asking a Neighbour for Help | Polite introduction, clear ask |
| 38 | Talking to Your Coach | Asking about practice, receiving feedback |
| 39 | Ordering at a Coffee Shop | Ordering, customising, paying |
| 40 | Introduction at Job Shadowing | Professional greeting, small talk |

### Friendships & Conversations (10)
| # | Scenario | Skill Practised |
|---|----------|----------------|
| 41 | Planning to Hang Out | Suggesting, agreeing on time and place |
| 42 | Talking About a Hobby | Sharing interest, asking about theirs |
| 43 | Clearing Up a Misunderstanding | Explaining calmly, listening |
| 44 | Comforting an Upset Friend | Noticing, asking, listening |
| 45 | Inviting Someone to an Event | Invite, give details, be positive |
| 46 | Responding to Teasing Calmly | Assertive but calm response |
| 47 | Calling a Friend | Hello, chatting, saying bye |
| 48 | Small Talk at a Party | Starting conversation with a stranger |
| 49 | Congratulating Someone | Genuine praise, asking about it |
| 50 | Saying a Meaningful Thank You | Specific, sincere, warm goodbye |

---

## How a Conversation Works

Each scenario is divided into **8 acts** (conversation phases). Each act has:
- A **bridge node** — the app narrates the scene transition (auto-advances)
- **5 prompt steps** — the app asks a question and waits for the child to speak

For each prompt step there are 3 node variants:

| Node | Purpose |
|------|---------|
| `prompt` | Main question — waits for speech |
| `retry` | Gentler re-ask after the first missed attempt |
| `teach` | Models the correct response, then auto-advances |

**Total per scenario: 8 acts × 16 nodes + 1 win node = 129 nodes**
**Total across all 50 scenarios: 6,434 nodes**

---

## Technical Overview

| Area | Implementation |
|------|---------------|
| Platform | Native Android, Kotlin, minSdk 21 (Android 5.0+) |
| Architecture | MVVM — single Activity, Fragment navigation |
| Speech output | Android `TextToSpeech` (built-in, no SDK) |
| Speech input | Android `SpeechRecognizer` (built-in, no SDK) |
| Conversation logic | Rule-based keyword matching (`KeywordMatcher.kt`) |
| Scenario data | `assets/scenarios.json` parsed with Gson |
| Progress storage | `SharedPreferences` |
| Dependencies | AndroidX, Material, Navigation, Gson, Lifecycle |
| AI / LLM | None — fully offline, rule-based |

### Keyword Matching

The app uses substring keyword matching across all STT alternatives returned by the recogniser. For example, the keyword `"play"` matches "playing", "played", or "replay". `minKeywordsRequired` is almost always 1 — the child only needs to include one relevant word to advance.

```kotlin
object KeywordMatcher {
    fun matches(transcript: String, keywords: List<String>, minRequired: Int): Boolean {
        val clean = transcript.replace(Regex("[^a-z0-9 ]"), "")
        val count = keywords.count { clean.contains(it) }
        return count >= minRequired
    }
}
```

---

## Project Structure

```
app/src/main/
  java/com/example/convapp/
    MainActivity.kt
    ui/
      HomeFragment.kt            — Scenario grid, star count
      ConversationFragment.kt    — Active conversation, mic button
      ResultsFragment.kt         — Star award, play again / home
      SettingsFragment.kt        — Caregiver settings
    viewmodel/
      AppViewModel.kt            — Orchestrates TTS, STT, engine
    engine/
      ConversationEngine.kt      — State machine, node traversal
      KeywordMatcher.kt          — Keyword matching logic
    speech/
      SpeechManager.kt           — TTS + STT wrapper
    model/
      Node.kt / Scenario.kt / AppSettings.kt
    data/
      ScenarioRepository.kt      — Loads scenarios.json
      ProgressRepository.kt      — SharedPreferences read/write
  assets/
    scenarios.json               — All 50 scenarios, 6,434 nodes
```

---

## Getting Started

### Requirements
- Android Studio (Hedgehog or later recommended)
- Android device or emulator running API 21+
- Microphone access (for speech recognition)

### Run the App
1. Clone this repository
2. Open in Android Studio
3. Let Gradle sync complete
4. Run on a device or emulator with microphone enabled

> **Note:** Google's speech recogniser requires an internet connection for STT on most devices. The rest of the app works fully offline.

---

## Caregiver Settings

A settings screen (accessible from the home screen) allows caregivers to adjust:

| Setting | Options |
|---------|---------|
| Speech rate | Slider from 0.6× (slow) to 1.2× (normal) |
| Mic mode | Tap to speak / Auto-open after TTS |
| Font size | Normal / Large / Extra Large |
| High contrast | Toggle dark background + white text |
| Reset progress | Clears all stars (double-confirmed) |

---

## Scenario Data Format

All conversation content lives in `app/src/main/assets/scenarios.json`. To add or modify scenarios, edit this file directly — no code changes required.

### Scenario metadata
```json
{
  "id": "greet",
  "title": "Meeting a New Classmate",
  "description": "Practice introducing yourself and starting a conversation.",
  "emoji": "👋",
  "color": "#C8E6C9",
  "startNode": "gr_a1_bridge",
  "totalSteps": 8
}
```

### Node types

| Type | Behaviour |
|------|-----------|
| `bridge` | Narrates a scene transition, auto-advances after a short delay |
| `prompt` | Speaks a question and waits for the child to respond |
| `teach` | Models the correct response aloud, auto-advances — never punitive |
| `celebrate` | Terminal node — awards a star and shows the results screen |

---

## Generating Scenario Content

The `gen_scenarios_b*.py` scripts in the project root were used to generate all 50 scenarios programmatically. Each script takes compact data tuples (question text, keywords, hint, model answer) and expands them into full JSON nodes with all connections wired automatically.

To regenerate or add new batches, run any of the generator scripts with Python 3:

```bash
python gen_scenarios_b5.py
```

This writes a batch JSON file to `app/src/main/assets/`. After generating all batches, merge them into `scenarios.json` using a simple merge script.

---

## Licence

This project was built for personal and educational use. Feel free to adapt it for non-commercial purposes with attribution.

---

## Acknowledgements

Built with care for a child who deserves every opportunity to practise, grow, and feel confident in conversation.
