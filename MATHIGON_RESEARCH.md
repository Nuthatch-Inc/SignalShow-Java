# Mathigon Research: The Interactive Mathematics Platform Redefining Engagement

## What is Mathigon and Why Does It Matter for SignalShow?

### Overview

**Mathigon** is an **award-winning interactive mathematics platform** that combines textbooks, virtual manipulatives, and AI-powered tutoring to create "the textbook of the future." Founded by **Philipp Legner** (former Google engineer, Cambridge University) in 2016, Mathigon has grown from a spare-time project to serving **600,000+ monthly users** before being acquired by **Amplify** (major US curriculum publisher) in October 2021.

**Key Products**:

- **Polypad** - Virtual manipulatives platform ("mathematical playground")
- **Interactive Courses** - Middle/high school math textbooks with simulations, animations, games
- **Virtual Tutor** - AI-powered conversational hints and encouragement

**Website**: https://mathigon.org/

### Why Mathigon is Critically Relevant to SignalShow

Mathigon matters to SignalShow for several transformative reasons:

1. **Active Learning Philosophy**: Mathigon doesn't tell students solutions—they **explore and discover**. Content is split into small sections; students must actively participate (solve problems, explore simulations, find patterns) before the next section unlocks. SignalShow should adopt this "discovery-first" approach.

2. **Storytelling + Real Applications**: Mathigon uses **captivating narratives, historical context, real-life applications** to make abstract topics engaging. Every course has a story. SignalShow should similarly contextualize DSP (e.g., "How Spotify compresses music," "How noise-canceling headphones work").

3. **Personalization via Knowledge Graph**: Mathigon builds an **internal model** of what students know, predicts where they'll struggle, adapts content to learning style. SignalShow could track which DSP concepts users have mastered (FFT, filters, modulation) and personalize difficulty.

4. **Virtual Tutor**: Conversational AI gives **tailored hints** ("Try thinking about the frequency domain"), not just answers. SignalShow could add similar contextual guidance for DSP exploration.

5. **Polypad as Inspiration**: Polypad is **best-in-class virtual manipulatives**—drag fractions, rotate polygons, build 3D shapes, play with functions. SignalShow should create "DSP playground" with draggable signals, interactive filters, real-time audio.

6. **Awards + Recognition**: Mathigon won **BETT Awards, GESS Awards, Reimagine Education Gold, Webby Honoree, Lovie Gold** and got endorsements from **Grant Sanderson (3Blue1Brown)**: "One of the greatest math resources out there on the internet, no question." This validates Mathigon's approach—SignalShow should study what makes it award-winning.

7. **Amplify Acquisition**: Acquired by major publisher (Amplify serves 10M+ students in US) validates commercial viability of interactive educational platforms. SignalShow could similarly partner with/be acquired by educational publishers.

8. **Fully Open-Source**: GitHub repo (371 stars, TypeScript/Node.js) shows technology stack. SignalShow can learn from Mathigon's architecture (custom Markdown, TypeScript interactives, SCSS styling).

### The Research Question

This document investigates: **What can SignalShow learn from Mathigon's active learning pedagogy, storytelling approach, personalization engine, virtual tutor, Polypad manipulatives, and successful Amplify acquisition?**

---

## Executive Summary

**Platform**: Mathigon - Interactive mathematics textbooks and virtual manipulatives

**Key Findings**:

- **Founder**: Philipp Legner (ex-Google, Cambridge University), started 2016 as side project
- **Acquisition**: Acquired by Amplify (Oct 2021) after raising $500K seed round (Dec 2019)
- **Adoption**: 600K+ monthly users (6M+ total lifetime), 20+ languages, used globally
- **Technology**: TypeScript, Node.js, custom Markdown parser, modular architecture (open-source on GitHub)
- **Pedagogy**: Active learning (explore/discover, not memorize), storytelling, personalization, virtual AI tutor
- **Polypad**: Best-in-class virtual manipulatives (fraction bars, 3D solids, algebra tiles, graphing, probability, music!)
- **Awards**: BETT Winner, GESS Winner, Reimagine Education Gold, Webby Honoree, Lovie Gold, Common Sense Top Pick
- **Strengths**: Unparalleled interactivity, beautiful design, compelling narratives, free forever (post-Amplify commitment)
- **Weaknesses**: Limited to geometry/algebra/probability (no calculus, statistics, DSP), still building out full curriculum
- **SignalShow Opportunity**: Apply Mathigon's active learning + storytelling to DSP education, create "DSP Polypad" with draggable signals/filters

**Strategic Position**: Mathigon is the **gold standard for interactive math education** (like PhET for science). SignalShow should emulate Mathigon's pedagogy (active learning, storytelling) and technology (TypeScript, custom interactives), but focus on DSP domain.

---

## 1. Platform Overview

### 1.1 History and Development

**Timeline**:

- **2016**: Philipp Legner starts Mathigon as **spare-time project** while volunteering with math outreach at Cambridge University
- **2017-2019**: Rapid growth, wins prestigious awards (GESS, Reimagine Education Gold, Webby Honoree, Lovie Gold)
- **December 2019**: Raises **$500K seed round** to build "Textbook of the Future"
- **2020-2021**: Develops Polypad (virtual manipulatives), expands courses (geometry, algebra, fractals, graph theory, cryptography)
- **October 2021**: **Acquired by Amplify** (major US curriculum publisher serving 10M+ students)
- **2021-2025**: Continues free access, integrates into Amplify's math programs, develops Polypad further

**Founder**:

- **Philipp Legner**: Former Google engineer, studied mathematics at Cambridge University
- **Motivation**: "I loved mathematics throughout my education, and built Mathigon so that all students can have a similar experience"
- **Current Role**: VP, Mathigon Studio at Amplify (post-acquisition)

**Amplify Acquisition** (October 2021):

- **Acquirer**: Amplify (K-12 curriculum/assessment publisher, founded 2000, serves 10M+ students)
- **Deal**: Mathigon remains free forever (Amplify committed), tools integrated into Amplify's math programs
- **Larry Berger (Amplify CEO)**: "I have almost never seen a product that inspires such pure intellectual delight in children and adults. Delight may be the hardest outcome to achieve in education. Mathigon achieves it over and over again."
- **Strategic Rationale**: Amplify gains cutting-edge interactive tools, Mathigon gains distribution (10M+ students)

### 1.2 Product Ecosystem

**1. Polypad** (Virtual Manipulatives Platform):

- **Description**: "Mathematical playground" with world's best virtual manipulatives
- **URL**: https://polypad.amplify.com/
- **Features**:
  - **Geometry**: Polygons, polyominoes, tangrams, aperiodic tiles, pentagon tilings, 3D solids, linkages
  - **Numbers**: Fraction bars, number lines, base-10 blocks, number cubes
  - **Algebra**: Algebra tiles, function machines, coordinate grids, equations
  - **Probability**: Dice, coins, spinners, Venn diagrams, tree diagrams
  - **Data Science**: Bar charts, histograms, scatter plots, box plots
  - **Games**: Balance scale, logic gates, patterns, art tools
  - **Music**: Polygons make sounds, fractions create rhythms, equations compose music!
- **Technology**: Drag-and-drop interface, infinite canvas, customizable tiles, save/share templates
- **Target**: K-12 educators and students (all grade levels)
- **Adoption**: "Game changer" per teachers (see testimonials from @3blue1brown, @TimBrzezinski, @geoffkrall)

**2. Interactive Courses** (Textbooks):

- **URL**: https://mathigon.org/courses
- **Topics**:
  - **Middle School**: Euclidean Geometry, Transformations & Symmetry, Triangles & Trigonometry, Polygons & Polyhedra
  - **High School**: Circles & Pi, Non-Euclidean Geometry (spherical, hyperbolic, topology), Fractals
  - **Under Construction**: More algebra, number theory, probability courses
- **Pedagogy**:
  - **Active learning**: Students explore simulations, solve problems, draw conclusions before next section unlocks
  - **Storytelling**: Real applications, historical context, fictional stories (makes content memorable)
  - **Virtual tutor**: AI gives hints in conversational interface, students can ask questions
  - **Personalization**: Knowledge graph tracks mastery, adapts content to learning style
- **Interactives**: Draw paths across bridges (Königsberg), run probability simulations, explore tessellations, manipulate fractals

**3. Activities and Lessons**:

- **Activities**: Standalone puzzles/games (https://mathigon.org/activities)
- **Lessons**: Teacher-created Polypad templates (https://polypad.amplify.com/lessons)
- **Teacher Resources**: Lesson plans, webinars, video tutorials (all free)

### 1.3 Current Status (2025)

**Active Development**: Yes - Now under Amplify, continued expansion

**Usage Statistics**:

- **Monthly Users**: 600,000+ (as of Oct 2021 acquisition announcement)
- **Lifetime Users**: 6M+ students and teachers globally
- **Languages**: 20+ (translations via GitLocalize)
- **Geographic Reach**: Global (K-12 schools worldwide)

**Market Position**:

- **Polypad**: Dominant in virtual manipulatives ("best virtual manipulatives I've ever come across" per educators)
- **Interactive Courses**: Growing (still building curriculum, but already award-winning)
- **Niche**: K-12 math (geometry, algebra, probability)—not yet calculus, statistics, or advanced topics

**Competitive Landscape**:

- **Polypad** vs. **GeoGebra**: GeoGebra more powerful (CAS, 3D graphing), Polypad more intuitive/beautiful (better UX)
- **Courses** vs. **Khan Academy**: Khan Academy has videos + exercises, Mathigon has interactive simulations + storytelling
- **Courses** vs. **Desmos**: Desmos focuses on graphing calculator, Mathigon broader (geometry, fractals, cryptography)

---

## 2. Technology Architecture

### 2.1 Open-Source Stack (GitHub)

**Mathigon GitHub** (https://github.com/mathigon):

- **Main Repo**: `textbooks` (371 stars, 156 forks, TypeScript 84.2%, SCSS 14.8%)
- **Active Development**: 1,892 commits, 39 contributors
- **License**: Custom (free to use, CLA required for contributions)

**Repository Structure**:

```
mathigon/textbooks/
  content/           # Course content (Markdown)
    shared/          # Biographies, glossary, web components
    euclidean-geometry/
      content.md     # Custom Markdown with interactives
      functions.ts   # TypeScript for interactive elements
      styles.scss    # Course-specific styles
      hints.yaml     # Virtual tutor messages
  frontend/assets/   # Images, fonts, icons
  translations/      # Localization files (20+ languages)
  config.yaml        # Site configuration
```

**Key Libraries** (Custom-built by Mathigon, published on NPM):

1. **@mathigon/core** - JavaScript utilities (arrays, strings, promises, events)
2. **@mathigon/fermat** - Math library (number theory, random, linear algebra)
3. **@mathigon/hilbert** - Expression parsing, simplification, MathML rendering
4. **@mathigon/euclid** - Geometry classes, SVG/Canvas drawing
5. **@mathigon/boost** - Browser utilities (DOM, web components, gestures, animations, routing)
6. **@mathigon/studio** - NodeJS server, TypeScript components, Markdown parser for interactive courses

### 2.2 Content Architecture

**Custom Markdown** (`.content.md` files):

- **Extension of Markdown** with custom syntax for interactives
- **Example**:

  ```markdown
  # Section Title

  Regular text with **blanks** for student input.

  ::: .theorem
  **Pythagoras' Theorem**: a² + b² = c²
  :::

  {.reveal(when="blank-0")} Revealed text after student fills blank.

  x-geopad(width=600 height=400) # Interactive geometry canvas
  svg
  circle(x="point(100,100)" name="a")
  path(x="segment(a,b)")
  ```

**TypeScript Interactives** (`functions.ts`):

- **Custom code** for each course's interactive elements
- **Example**:
  ```typescript
  export function euclideanGeometry($step: Step) {
    const $geopad = $step.$("x-geopad") as Geopad;
    $geopad.on("add:point", (point) => {
      // Custom logic when student adds point
      $step.score("point-added");
    });
  }
  ```

**Virtual Tutor** (`hints.yaml`):

- **Conversational hints** triggered by student actions
- **Example**:
  ```yaml
  blank-0:
    - "Think about the relationship between the sides."
    - "Remember: the square of the hypotenuse..."
    - "Try using the Pythagorean theorem."
  ```

### 2.3 Polypad Architecture

**Technology** (Inferred from usage, not publicly documented):

- **Frontend**: TypeScript, Canvas API (for rendering manipulatives)
- **Drag-and-Drop**: Custom gesture recognition (likely using @mathigon/boost)
- **Infinite Canvas**: Pan/zoom, unlimited workspace
- **Music Feature**: Web Audio API (polygons/fractions trigger sounds)
- **Collaboration**: Save/share templates, classroom mode (teacher sees student work)

**Manipulative Types**:

- **Visual**: Polygons, tiles, 3D solids (rendered as SVG/Canvas)
- **Numeric**: Fraction bars, number lines (snap to grid)
- **Algebraic**: Tiles (x², x, 1), function machines (input/output)
- **Stochastic**: Dice/coins (animated rolling/flipping)
- **Auditory**: Sounds for each manipulative (accessibility + engagement)

**Comparison to SignalShow**:

- **Mathigon Polypad**: Visual/tactile math manipulatives
- **SignalShow "DSP Polypad"**: Audio/signal manipulatives (drag waveforms, adjust filter knobs, see/hear results)

---

## 3. Pedagogical Philosophy

### 3.1 Active Learning (Discovery-First)

**Mathigon's Principle**: "Rather than telling students how to solve problems, we want them to explore and 'discover' solutions on their own."

**Implementation**:

- **Small sections**: Content split into bite-sized chunks
- **Active participation required**: Students must interact (solve problem, explore simulation, find pattern) before next section unlocks
- **No passive consumption**: Can't just scroll through like reading article

**Interactive Component Types**:

1. **Geometric exploration**: Draw paths across Königsberg bridges (graph theory)
2. **Probability simulations**: Run Monte Carlo simulations (Law of Large Numbers)
3. **Tessellation discovery**: Which shapes tile the plane? (Students experiment)
4. **Pattern finding**: Fibonacci sequence, fractals (students generalize from examples)

**Comparison to Traditional Textbooks**:

- **Traditional**: Teacher explains → Student memorizes → Student applies to homework
- **Mathigon**: Student explores → Student discovers pattern → Student generalizes principle → Mathigon validates

**Comparison to PhET**:

- **PhET**: Research-validated design principles (4-6 student interviews per sim)
- **Mathigon**: Active learning philosophy (but less formal research validation than PhET)

**Lesson for SignalShow**: Like Mathigon, SignalShow should **hide DSP theory** until students discover it. Example:

1. **Explore**: Play with sine wave frequency slider, hear pitch change
2. **Discover**: "Higher frequency = higher pitch" (student realizes)
3. **Generalize**: Frequency is oscillations per second (Hz)
4. **Validate**: SignalShow reveals theory, student already understands intuitively

### 3.2 Storytelling and Context

**Mathigon's Principle**: "Using Mathigon requires much more effort and concentration from students, compared to simply watching a video or listening to a teacher. That's why it is important make the content as fun and engaging as possible."

**Storytelling Techniques**:

1. **Real-life applications**: Why learn about circles? (GPS navigation, planetary orbits, engineering)
2. **Historical context**: Königsberg bridge problem (Euler, 1736)
3. **Puzzles**: Sudoku, logic games, magic squares
4. **Interdisciplinary connections**: Math + art (tessellations, fractals), math + music (rhythm, harmony)
5. **Fictional stories**: Narrative arcs across course (characters, mysteries to solve)

**Example (Circles & Pi Course)**:

- **Opening hook**: "For thousands of years, mathematicians have been fascinated by circles. And it's easy to see why!"
- **Historical narrative**: Ancient Babylonians (π ≈ 3.125), Archimedes (π ≈ 3.14), modern computers (π to trillions of digits)
- **Real application**: How GPS uses circles (trilateration)
- **Surprise**: Indiana legislature tried to legally define π = 3.2 (1897, true story!)

**Comparison to MATLAB Documentation**:

- **MATLAB**: Dry, technical ("The `fft` function computes the discrete Fourier transform...")
- **Mathigon**: Engaging, contextual ("Imagine you're listening to music...")

**Lesson for SignalShow**: Like Mathigon, SignalShow should **tell stories**:

- **FFT**: "How does Shazam recognize songs in noisy environments?" (FFT is the secret)
- **Filters**: "How do noise-canceling headphones work?" (Adaptive filters + destructive interference)
- **Modulation**: "How does FM radio transmit sound?" (Frequency modulation)
- **Compression**: "How does Spotify stream music over slow connections?" (Perceptual audio coding)

### 3.3 Personalization and Knowledge Graph

**Mathigon's Principle**: "As users interact with Mathigon, we can slowly build up an internal model of how well they know different related concepts in mathematics: the knowledge graph."

**Knowledge Graph**:

- **Tracks mastery**: Student completed "Pythagoras' Theorem"? → Knows right triangles, squares, square roots
- **Predicts struggle**: Course requires "trigonometry" but student hasn't mastered "angles"? → Provide extra scaffolding
- **Adapts explanations**: Student prefers visual explanations? → Show more diagrams, fewer equations

**Virtual Tutor** (AI-powered):

- **Conversational interface**: Students can ask questions ("What is a hypotenuse?")
- **Tailored hints**: Not generic ("Try again"), but specific ("Think about the longest side of the triangle")
- **Encouragement**: Positive reinforcement ("Great job! You're getting the hang of this")

**Comparison to Duolingo**:

- **Duolingo**: Uses spaced repetition, adapts difficulty based on performance
- **Mathigon**: Similar adaptive learning, but for math concepts (not language vocabulary)

**Lesson for SignalShow**: Like Mathigon, SignalShow should **track DSP concept mastery**:

- **Knowledge graph**: Student mastered time-domain analysis? → Can now attempt frequency-domain
- **Prerequisites**: FFT requires "complex numbers" → If student hasn't seen complex numbers, provide primer
- **Adaptive difficulty**: Student finds filter design easy? → Introduce advanced topics (pole-zero placement, group delay)

---

## 4. Polypad Deep Dive (Most Relevant to SignalShow)

### 4.1 Features and Manipulatives

**Comprehensive Toolset** (100+ manipulatives organized by category):

**Geometry**:

- **Polygons**: Triangles, squares, pentagons, hexagons, circles (drag, rotate, scale)
- **Polyominoes**: Tetris-like shapes (tessellation puzzles)
- **Tangrams**: 7-piece Chinese puzzle
- **Aperiodic Tiles**: Penrose tiles (non-repeating patterns)
- **3D Solids**: Cubes, tetrahedrons, dodecahedrons (rotate in 3D)
- **Linkages**: Mechanical linkages (four-bar, Peaucellier-Lipkin)

**Numbers**:

- **Fraction Bars**: Visual fractions (1/2, 1/4, 1/8, etc.)
- **Number Lines**: Draggable points, intervals
- **Base-10 Blocks**: Ones, tens, hundreds (place value)
- **Number Cubes**: Dice-like manipulatives

**Algebra**:

- **Algebra Tiles**: x², x, 1 tiles (factor polynomials visually)
- **Function Machines**: Input → function → output (black box)
- **Coordinate Grids**: Cartesian plane, plot points
- **Equations**: Balance scale (solve equations by balancing)

**Probability & Data**:

- **Dice**: 6-sided, 20-sided, custom
- **Coins**: Fair/biased coin flips
- **Spinners**: Customizable probability wheels
- **Venn Diagrams**: Set operations
- **Charts**: Bar, histogram, scatter, box plot

**Music** (Unique to Polypad!):

- **Polygons make sounds**: Triangle = high pitch, square = lower pitch
- **Fractions create rhythms**: 1/4 note, 1/8 note, 1/16 note
- **Equations compose music**: y = sin(x) creates waveform, hear it play!
- **Purpose**: Accessibility (auditory + visual), engagement (music is fun!)

### 4.2 User Workflow (Example: Fractions)

**Typical Use Case** (Elementary Student Learning Fractions):

1. **Open Polypad**: Teacher shares template link (pre-loaded with fraction bars)
2. **Explore**: Student drags 1/2 bar, sees it's half the size of 1 whole
3. **Compare**: Drag 1/4 bar next to 1/2 bar → "Two 1/4 bars = one 1/2 bar"
4. **Discover equivalence**: Three 1/3 bars = one 1 whole
5. **Build understanding**: Fractions are parts of a whole (visual + tactile)

**Teacher View** (Classroom Mode):

- **Monitor students**: See all student canvases in real-time
- **Identify struggles**: Which students are stuck?
- **Provide feedback**: Comment on student work, give hints

**Comparison to Physical Manipulatives**:

- **Physical**: Expensive ($100+ for full fraction bar set), easy to lose pieces, not scalable (one set per student)
- **Polypad**: Free, infinite supply, infinite canvas, save/share digitally

### 4.3 Awards and Recognition

**Polypad Testimonials** (Twitter educators):

- **Grant Sanderson (@3blue1brown)**: "Mathigon is one of the greatest math resources out there on the internet, no question."
- **Tim Brzezinski (@TimBrzezinski)**: "This jaw-dropping Mathigon update opens the doors to so many new student exploration & activity possibilities in geometry!"
- **Geoff Krall (@geoffkrall)**: "Polypad is an absolute game changer and I've only known about it a few months."
- **Mark Labuda (@MrMLabuda)**: "Hands down the best virtual manipulative site that I've ever come across."
- **Jayne Breton (@JBretonmath)**: "Just heard a kid exclaim with enthusiasm 'BAM! Polypad baby…'"

**Industry Awards**:

- **BETT Awards** (2022) - Winner (Leading EdTech awards in UK)
- **GESS Awards** (2019) - Winner (Global Educational Supplies & Solutions)
- **Reimagine Education Awards** - Gold Winner (QS World University Rankings)
- **EdTech Digest Cool Tool** - Finalist
- **Common Sense Education** - Top Pick
- **Webby Awards** - Honouree (2017)
- **Lovie Awards** - Gold Winner (2013)

---

## 5. Design Lessons for SignalShow

### 5.1 Adopt Mathigon's Active Learning

**What to Copy**:

1. **Discovery-first**: Hide DSP theory until students discover it through exploration
2. **Section locking**: Students must complete interactive before next concept unlocks
3. **No passive scrolling**: Every section requires interaction (adjust slider, solve problem, explore simulation)

**Example Implementation** (FFT Course):

- **Section 1**: "Play with this sine wave. What happens when you change the frequency slider?"
  - Student adjusts slider (100 Hz → 440 Hz), hears pitch change
  - Student realizes: Higher frequency = higher pitch
  - **Next section unlocks only after student adjusts slider**
- **Section 2**: "Frequency is measured in Hertz (Hz). One Hertz is one oscillation per second."
  - Now student sees theory, but already has intuition from Section 1
- **Section 3**: "What if we combine two sine waves? Predict what you'll hear."
  - Student combines 440 Hz + 880 Hz, hears harmony (octave)
  - **Discovery**: Multiple frequencies can coexist in one signal

### 5.2 Adopt Mathigon's Storytelling

**What to Copy**:

1. **Real-world hooks**: Start every DSP concept with "Why does this matter?"
2. **Historical context**: Fourier's heat equation (1822), Shannon's sampling theorem (1949)
3. **Surprising facts**: MP3 compression discards 90% of audio data (you can't even tell!)
4. **Interdisciplinary**: DSP + music (autotune), DSP + medicine (ultrasound), DSP + astronomy (radio telescopes)

**Example Narratives**:

- **Aliasing**: "In old Western movies, wagon wheels sometimes appear to spin backward. Why?" (Aliasing! Sampling rate < 2× wheel rotation speed)
- **Nyquist Theorem**: "Why do CDs sample at 44.1 kHz?" (Human hearing up to 20 kHz, Nyquist says need 2× → 40 kHz, plus margin → 44.1 kHz)
- **Spectrograms**: "How do dolphins 'see' with sound?" (Echolocation uses spectrograms to analyze reflected pulses)

### 5.3 Build "DSP Polypad" (Virtual Signal Manipulatives)

**Inspiration from Polypad**:

- **Polypad**: Drag polygons, rotate 3D solids, balance equations
- **DSP Polypad**: Drag waveforms, adjust filter knobs, morph spectrograms

**Proposed Manipulatives**:

1. **Waveform Builder**: Drag sine waves onto canvas, combine them (additive synthesis)
2. **Filter Studio**: Drag frequency response curve, hear filtered audio in real-time
3. **Spectrogram Canvas**: Upload audio, see scrolling spectrogram, click frequency bins to isolate
4. **Modulation Lab**: Drag carrier + message signals, see AM/FM output
5. **Convolution Visualizer**: Drag two signals, see their convolution animate
6. **Pole-Zero Plotter**: Drag poles/zeros on z-plane, see frequency response update

**Music Feature** (Like Polypad's Sound):

- **Waveforms make sounds**: Sine wave = pure tone, square wave = buzzy, sawtooth = bright
- **Filters affect timbre**: Lowpass = muffled, highpass = tinny, bandpass = telephone
- **Visual + Auditory**: See waveform + hear sound (dual modalities like Polypad)

---

## 6. Technology Comparison: Mathigon vs. SignalShow

### 6.1 Architecture Comparison

| Component          | Mathigon                   | SignalShow (Planned)              | Notes                                         |
| ------------------ | -------------------------- | --------------------------------- | --------------------------------------------- |
| **Language**       | TypeScript                 | TypeScript/JavaScript             | Same! SignalShow can use Mathigon's libraries |
| **Framework**      | Custom (Mathigon Studio)   | React                             | Mathigon more custom, SignalShow uses React   |
| **Content Format** | Custom Markdown            | Markdown (likely)                 | Mathigon's approach is proven                 |
| **Interactives**   | TypeScript per course      | React components                  | Different but both TypeScript-based           |
| **Math Rendering** | @mathigon/hilbert (MathML) | KaTeX or MathJax                  | Mathigon uses custom renderer                 |
| **Geometry**       | @mathigon/euclid           | Not needed (DSP is 1D/2D signals) | Mathigon has 2D/3D geometry lib               |
| **Animations**     | @mathigon/boost            | Framer Motion or CSS              | Mathigon has gesture recognition              |
| **Server**         | @mathigon/studio (Node.js) | Node.js (likely)                  | Same ecosystem                                |
| **Audio**          | Web Audio (Polypad music)  | Web Audio API                     | Both need audio playback                      |
| **Open-Source**    | ✅ GitHub (371 stars)      | ✅ Planned                        | Both open-source                              |

### 6.2 Feature Comparison

| Feature               | Mathigon                      | SignalShow                      | Advantage            |
| --------------------- | ----------------------------- | ------------------------------- | -------------------- |
| **Active Learning**   | ✅ Discovery-first            | ⚠️ To be implemented            | Mathigon             |
| **Storytelling**      | ✅ Every course has narrative | ⚠️ To be implemented            | Mathigon             |
| **Personalization**   | ✅ Knowledge graph            | ❌ Not planned yet              | Mathigon             |
| **Virtual Tutor**     | ✅ AI-powered hints           | ❌ Not planned yet              | Mathigon             |
| **Manipulatives**     | ✅ Polypad (100+ tools)       | ⚠️ "DSP Polypad" planned        | Mathigon (currently) |
| **Music/Audio**       | ✅ Polypad sounds             | ✅ Core feature (DSP is audio!) | Tie                  |
| **Geometry**          | ✅ Core domain                | ❌ Not needed                   | Mathigon             |
| **Signal Processing** | ❌ Not available              | ✅ Core domain                  | SignalShow           |
| **FFT/Spectrograms**  | ❌ Not available              | ✅ Core feature                 | SignalShow           |
| **Filter Design**     | ❌ Not available              | ✅ Core feature                 | SignalShow           |

**Key Insight**: Mathigon excels at **pedagogy** (active learning, storytelling, personalization). SignalShow should adopt Mathigon's teaching philosophy but apply it to **DSP domain**.

---

## 7. Strategic Recommendations

### 7.1 Short-Term (Year 1): Adopt Mathigon's Pedagogy

**Objectives**:

1. **Active learning**: Build one course (e.g., "Intro to FFT") using discovery-first approach
2. **Section locking**: Students must interact (adjust slider, solve problem) before next section unlocks
3. **Storytelling**: Every concept starts with "Why does this matter?" (real-world application, historical context, or surprising fact)

**Avoid**:

- Traditional lecture format (watch video → do exercises)
- Wall of text (break into small sections with frequent interactions)
- Abstract theory first (students should discover intuitively, then see equations)

**Focus**:

- Make FFT exploration **more engaging than MATLAB** (MATLAB is powerful but dry)
- Use **visual + auditory** (see spectrogram + hear audio, like Polypad's sounds)
- **Instant feedback** (drag slider → see/hear result immediately, no "Run" button)

### 7.2 Medium-Term (Years 2-3): Build "DSP Polypad"

**Objectives**:

1. Create **virtual DSP manipulatives** (waveform builder, filter studio, spectrogram canvas)
2. **Drag-and-drop interface** (like Polypad's polygons, but for signals/filters)
3. **Infinite canvas** (combine multiple signals, chain filters, build complex systems)
4. **Save/share**: Students save DSP compositions, teachers create templates

**Manipulative Library**:

- **Waveforms**: Sine, square, triangle, sawtooth, noise, chirp (drag onto canvas)
- **Filters**: Lowpass, highpass, bandpass, notch (adjust cutoff/Q with sliders)
- **Operations**: Add, multiply, convolve (drag signals together)
- **Analysis**: FFT, spectrogram, autocorrelation (drop signal into analyzer)
- **Modulation**: AM, FM, PSK, QAM (combine carrier + message)

**Why This Matters**:

- Polypad is **most-loved feature** of Mathigon ("game changer" per educators)
- SignalShow needs similar "killer feature" → DSP Polypad could be it
- **Differentiator**: No existing platform has drag-and-drop DSP manipulatives (MATLAB is code-based, Audacity is audio editor, not pedagogical)

### 7.3 Long-Term (Years 4-5): Add Personalization and Virtual Tutor

**Objectives**:

1. **Knowledge graph**: Track which DSP concepts student has mastered (time-domain, frequency-domain, z-domain)
2. **Adaptive content**: If student struggles with complex numbers, provide extra primer before FFT
3. **Virtual tutor**: AI gives contextual hints ("Think about the frequency domain" when student is stuck on FFT problem)
4. **Student questions**: Conversational interface (student asks "What is aliasing?", tutor explains)

**Implementation** (Using LLMs):

- **GPT-4/Claude**: Natural language processing for student questions
- **Prompt engineering**: "You are a DSP tutor. The student is learning FFT and asked 'What is aliasing?' Provide a brief, intuitive explanation with an example."
- **Context-aware**: Tutor knows what student has learned (knowledge graph), provides relevant hints

**Why This Matters**:

- Mathigon's virtual tutor is **unique differentiator** (not just static textbook)
- DSP is complex (many interdependent concepts: sampling, aliasing, Nyquist, windowing, leakage)
- Students need **guidance** (not answers, but nudges in right direction)

---

## 8. Open-Source Strategy

### 8.1 Learn from Mathigon's GitHub Success

**Mathigon's Approach**:

- ✅ **Fully open-source** (textbooks repo: 371 stars, 156 forks)
- ✅ **Modular libraries** (@mathigon/core, @mathigon/fermat, @mathigon/euclid published on NPM)
- ✅ **Community contributions** (39 contributors, CLA required)
- ✅ **Translations** (20+ languages via GitLocalize)

**Lessons for SignalShow**:

1. **Open-source from day 1** (like Mathigon, Falstad, PhET) → Attracts contributors
2. **Modular architecture** (separate libraries for DSP algorithms, UI components, visualizations)
3. **NPM packages** (publish reusable modules: @signalshow/dsp, @signalshow/audio, @signalshow/viz)
4. **Contributor-friendly**: Clear docs, coding standards, CLA (like Mathigon's)
5. **Localization**: Support translations (DSP education is global)

### 8.2 Licensing Considerations

**Mathigon's License**:

- **Custom** (free to use, but CLA required for contributions)
- **Amplify ownership** (post-acquisition, Amplify owns IP but committed to free access)

**Alternatives for SignalShow**:

1. **MIT License** (most permissive, allows commercial use) → Like PhET
2. **GPLv2** (copyleft, derivative works must be open-source) → Like Falstad
3. **Custom** (like Mathigon, with CLA for future monetization flexibility)

**Recommendation**: Start with **MIT** (maximize adoption, community contributions), consider CLA if planning acquisition/commercialization later.

---

## 9. Acquisition and Business Model

### 9.1 Amplify Acquisition Case Study

**Why Amplify Acquired Mathigon**:

1. **Cutting-edge interactivity**: "I have almost never seen a product that inspires such pure intellectual delight" (Larry Berger, CEO)
2. **Proven adoption**: 600K monthly users, 6M lifetime users (strong PMF - Product-Market Fit)
3. **Award-winning**: BETT, GESS, Reimagine Education Gold (validated by industry)
4. **Distribution synergy**: Amplify serves 10M+ students → Can scale Mathigon to all schools
5. **Technology**: Integrate Polypad/Studio into Amplify's math programs (enhances existing products)

**Deal Structure**:

- **Free forever commitment**: Amplify committed to keeping Mathigon free (critical for adoption)
- **Philipp Legner role**: VP Mathigon Studio (founder stays on, leads innovation)
- **Integration**: Polypad integrated into Amplify's in-development math programs

**Lessons for SignalShow**:

1. **Build adoption first** (like Mathigon: 600K users before acquisition) → Proves PMF
2. **Win awards** (validates quality, attracts acquirers)
3. **Target acquirers**: Educational publishers (Amplify, Pearson, McGraw-Hill), EdTech platforms (Coursera, Udemy), or DSP tool companies (MathWorks, National Instruments)
4. **Free-forever strategy**: Like Mathigon, commit to free access (maximizes adoption, makes acquisition more valuable to acquirer who wants distribution)

### 9.2 Alternative Business Models

**If Not Acquisition**:

1. **Freemium**: Free for individuals, paid for schools (like GeoGebra before BYJU'S acquisition)
2. **Grants**: NSF IUSE, Department of Education (like PhET: $70M+ in grants)
3. **Donations**: Patreon, GitHub Sponsors (like Falstad: solo developer, no revenue, but free forever)
4. **Premium content**: Free basics, paid advanced courses (like Brilliant.org)

**Recommendation**: Follow Mathigon's path:

1. **Years 1-3**: Build free platform, grow users (target 100K users)
2. **Year 4**: Raise seed round ($500K like Mathigon) to expand content/features
3. **Years 5-7**: Scale to 1M+ users, win awards (BETT, ISTE, etc.)
4. **Year 8+**: Acquisition by publisher/EdTech company OR continue free with grant funding

---

## 10. Conclusion

### 10.1 Key Takeaways

**On Mathigon as Inspiration**:

- ✅ **Active learning** - Discovery-first, not lecture-first (students explore, find patterns, generalize principles)
- ✅ **Storytelling** - Real applications, historical context, interdisciplinary connections (make abstract concepts engaging)
- ✅ **Personalization** - Knowledge graph tracks mastery, adapts content to student needs
- ✅ **Virtual tutor** - AI-powered conversational hints (not just "Try again", but contextual guidance)
- ✅ **Polypad brilliance** - Best-in-class virtual manipulatives ("game changer" per educators, loved by students)
- ✅ **Open-source success** - GitHub repo (371 stars), modular libraries, community contributions
- ✅ **Amplify acquisition** - Validates commercial viability ($500K seed → acquisition by major publisher in 5 years)

**On SignalShow Differentiation**:

- ✅ **Domain-specific** (DSP) vs. general math (geometry, algebra, probability)
- ✅ **Audio-centric** (hear signals, not just see graphs) vs. visual-only (Mathigon has some audio in Polypad, but not core)
- ✅ **Advanced technical** (FFT, filter design, z-plane) vs. K-12 curriculum
- ⚠️ Smaller market (DSP students vs. all K-12 math students)
- ⚠️ Must compete with MATLAB (industry standard), Mathigon competes with Khan Academy (easier target)

### 10.2 Strategic Positioning

**Elevator Pitch**:

> "SignalShow is Mathigon for signal processing. We bring Mathigon's active learning, storytelling, and virtual manipulatives to DSP education—with spectrograms, FFT, and filter design that Mathigon doesn't offer."

**Positioning Statement**:

> "Like Mathigon revolutionized geometry education with Polypad, SignalShow revolutionizes DSP education with 'DSP Polypad'—drag waveforms, adjust filters, see/hear results in real-time."

### 10.3 Critical Success Factors

1. **Active learning** - Like Mathigon (discovery-first), not like MATLAB (code-first)
2. **Storytelling** - Like Mathigon (real applications, historical context), not like dry textbooks
3. **"DSP Polypad"** - Drag-and-drop signal manipulatives (waveforms, filters, spectrograms)
4. **Visual + Auditory** - Like Mathigon's Polypad sounds, SignalShow must let users **hear** DSP (not just see graphs)
5. **Open-source** - Like Mathigon (GitHub, modular libraries), attract community contributors
6. **Awards** - Like Mathigon (BETT, GESS, Reimagine Education), win EdTech awards to validate quality
7. **Acquisition potential** - Like Mathigon (Amplify acquired after 5 years), target EdTech publishers or DSP tool companies

---

## 11. Action Items

### 11.1 Immediate (Next 30 Days)

1. ✅ Complete this research document
2. ⬜ Explore Mathigon courses (https://mathigon.org/courses)—note active learning, storytelling, section locking
3. ⬜ Explore Polypad (https://polypad.amplify.com/)—note drag-and-drop UX, infinite canvas, sounds
4. ⬜ Study Mathigon GitHub (https://github.com/mathigon/textbooks)—understand content.md format, functions.ts interactives
5. ⬜ Prototype "Waveform Builder" (drag sine waves, combine them, hear result)—SignalShow's first "manipulative"

### 11.2 Short-Term (Next 90 Days)

1. ⬜ Build "Intro to FFT" course using Mathigon's active learning approach
2. ⬜ Section locking: Students must adjust frequency slider before next section unlocks
3. ⬜ Storytelling: Start with "How does Shazam recognize songs?" (FFT is the answer)
4. ⬜ Create 5 DSP manipulatives: Waveform Builder, Filter Studio, Spectrogram Canvas, Modulation Lab, Convolution Visualizer
5. ⬜ Visual + Auditory: Every manipulative plays sound (not just shows graph)

### 11.3 Long-Term (Next 12 Months)

1. ⬜ Build full "DSP Polypad" (10+ manipulatives, infinite canvas, save/share templates)
2. ⬜ Personalization: Track which DSP concepts student mastered (time-domain, frequency-domain, z-domain)
3. ⬜ Virtual tutor: GPT-4 gives contextual hints ("Think about aliasing when sample rate < 2× signal frequency")
4. ⬜ Open-source: Publish on GitHub (modular libraries: @signalshow/dsp, @signalshow/audio, @signalshow/viz)
5. ⬜ Target awards: BETT, ISTE, EdTech Digest (like Mathigon's award wins)
6. ⬜ Grow to 10K+ users (1% of Mathigon's 600K, achievable in DSP niche)

---

## 12. References

### 12.1 Mathigon Resources

- **Main Website**: https://mathigon.org/
- **Polypad**: https://polypad.amplify.com/ (virtual manipulatives)
- **Interactive Courses**: https://mathigon.org/courses
- **About**: https://mathigon.org/about (mission, principles)
- **Press**: https://mathigon.org/press (Amplify acquisition announcement)

### 12.2 GitHub Repositories

- **Textbooks**: https://github.com/mathigon/textbooks (371 stars, main repo)
- **Studio**: https://github.com/mathigon/studio (NodeJS server for interactive courses)
- **Fermat.js**: https://github.com/mathigon/fermat.js (math library)
- **Euclid.js**: https://github.com/mathigon/euclid.js (geometry library)
- **Boost.js**: https://github.com/mathigon/boost.js (browser utilities)
- **All repos**: https://github.com/mathigon (15 repositories)

### 12.3 Awards and Recognition

- **BETT Awards** (2022) - Winner
- **GESS Awards** (2019) - Winner
- **Reimagine Education** - Gold Winner
- **Webby Awards** (2017) - Honouree
- **Lovie Awards** (2013) - Gold Winner
- **Common Sense Education** - Top Pick

### 12.4 Endorsements

- **Grant Sanderson (@3blue1brown)**: "Mathigon is one of the greatest math resources out there on the internet, no question."
- **The Guardian**: "One of the most accessible and engaging maths resources available on the web, a true mathematical wonderland."
- **Larry Berger (Amplify CEO)**: "I have almost never seen a product that inspires such pure intellectual delight in children and adults."

---

**Document Version**: 1.0  
**Date**: October 26, 2025  
**Author**: Research conducted for SignalShow modernization project  
**Previous**: Falstad Research  
**Next**: Wolfram Demonstrations Project Research (final platform)
