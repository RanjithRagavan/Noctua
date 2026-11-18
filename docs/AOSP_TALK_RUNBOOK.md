# Noctua at the AOSP and AAOS Meetup
## Detailed 30-minute talk, pitch, and live-demo runbook

**Talk title:** Noctua: On-Device AI on Android with ExecuTorch

**Subtitle:** From app-local inference to an AOSP-native OS-level agent

**Format:** 30 minutes, followed by Q&A

**Audience:** AOSP, BSP, platform, and AAOS developers

This is a speaker runbook, not a slide deck. Quoted paragraphs are suggested
words; the actions and file paths tell you exactly what to show. Rehearse the
transitions, not a word-for-word recitation.

---

## 1. The pitch in one breath

> “On-device AI does not tell us which process owns the model. I will take the
> same ExecuTorch readiness model from Noctua’s Android app, move execution
> behind AIDL into an isolated native AOSP daemon, and then add the policy and
> action layer that turns inference into an OS-level agent—while raw biometric
> history and Oura credentials stay on the app side of the Binder boundary.”

### The three audience takeaways

By the end, the audience should be able to answer:

1. **How does a PyTorch model become an on-device Android inference path?**
   Export a fixed tensor contract to `.pte`, then execute it through
   ExecuTorch’s Android or C++ runtime.
2. **What actually changes when inference becomes an AOSP platform service?**
   AIDL, ServiceManager, Soong, init, SELinux, process isolation, and platform
   app signing—not the model’s tensor contract.
3. **Why is a model call not yet an agent?**
   An agent also needs a consented trigger, sensing, deterministic policy, a
   bounded action, and an audit trail.

### The story arc

```text
A score without coaching
        ↓
Noctua: local forecast + explainable coaching
        ↓
The same model, moved across a process boundary
        ↓
A native AOSP inference service with explicit security rules
        ↓
An OS agent: TRIGGER → SENSE → INFER → DECIDE → ACT → AUDIT
```

---

## 2. Be exact about the two demo paths

There are two related demos. Keep them distinct on stage.

| Path | What it proves | Requirement |
|---|---|---|
| **Android Studio / ordinary APK** | The current Noctua product path: local feature extraction, ExecuTorch through the Android AAR/JNI, coaching UI, and an honest `APP PROCESS` execution badge | Normal emulator or phone; no Oura account in demo mode |
| **AOSP native service** | The same `.pte` model loaded by native C++ in `noctua_agent`, discovered through ServiceManager and called over Binder | Linux AOSP build host, integrated image, and a compatible `userdebug` device/emulator |

The ordinary APK does **not** become an AOSP service client merely by installing
it on a custom device. The platform variant must use `platform_apis`, generated
AIDL Java bindings, platform signing, and the `BinderForecaster` integration.

The best stage setup is:

- the ordinary emulator to show the existing app path; and
- an already-built AOSP `userdebug` target for the native terminal proof.

If only the ordinary emulator is available, show the app and AOSP architecture
screens, then walk through the native source and commands. Do not imply that
`noctua_agent` is running when it is not.

**Do not attempt a first-time AOSP build or flash as part of the live talk.**

---

## 3. Thirty-minute run of show

| Time | Section | Audience should remember |
|---|---|---|
| 00:00–01:30 | Hook and promise | “Where inference runs is an architectural decision.” |
| 01:30–04:00 | Noctua product demo | Local forecast and coaching; no cloud inference |
| 04:00–07:00 | PyTorch → ExecuTorch export | Fixed `[1,8] → [1,1]` contract; small model on purpose |
| 07:00–09:30 | Existing Android inference | Kotlin → Android runtime/JNI → `.pte` in the app process |
| 09:30–12:00 | AOSP architecture | Move execution, not the raw health database |
| 12:00–16:30 | Live demo | Execution badge, process boundary, service/client proof |
| 16:30–20:00 | Native code deep dive | AIDL, validation, tensor creation, `forward()`, registration |
| 20:00–23:00 | Image integration and security | Soong + init + SELinux; least privilege |
| 23:00–26:30 | From inference to OS agent | Separate neural inference from deterministic policy/action |
| 26:30–28:30 | Production boundaries | API stability, authorization, resources, users, updates |
| 28:30–30:00 | Recap and closing pitch | Same model, explicit boundary, bounded agent |
| Afterwards | Q&A | Use the answers in section 12 |

Keep a visible timer. At 20 minutes, start image integration even if the code
walkthrough is not finished. At 28:30, stop adding detail and close cleanly.

---

## 4. Suggested slide sequence

1. **Title:** Noctua — From App Inference to an AOSP OS-Level Agent
2. **The problem:** A readiness score is not coaching
3. **Noctua today:** Oura data → features → local forecast + insights
4. **Model contract and export:** `[1,8] → .pte → [1,1]`
5. **App process:** Kotlin → ExecuTorch Android/JNI → model
6. **AOSP process boundary:** App → AIDL/Binder → native daemon → model
7. **Live proof:** Badge, ServiceManager, client output
8. **Native implementation:** `INoctuaAgent`, `Module`, `forward()`
9. **Platform integration:** Soong, init, SELinux
10. **Agent loop:** Trigger, sense, infer, decide, act, audit
11. **Production checklist and close**

Use the app and terminal instead of static slides for items 3, 6, and 7 whenever
possible. The screenshots under `docs/screenshots/` are the visual fallback.

---

## 5. Prepare tonight: Android Studio demo

### 5.1 Build and launch

Open the **Noctua repository root**, not only `example-app`, in Android Studio.

1. Let Gradle sync finish.
2. Select the **example-app** run configuration.
3. Select a known emulator, such as Pixel 7 Pro API 33.
4. Press **Run**.
5. Wait for the deterministic demo dataset to load; no Oura login is needed.

Equivalent terminal verification from the repository root:

```bash
./gradlew test :example-app:assembleDebug
./gradlew :example-app:installDebug
```

The second command requires a running emulator or connected device.

### 5.2 Rehearse this exact tap path

1. Open **Dashboard**.
2. Find **INFERENCE EXECUTION PATH**.
3. Confirm that the ordinary build says **APP PROCESS**.
4. Read the path: `Noctua app → JNI / AAR → .pte model`.
5. Tap **View AOSP architecture** or the **AOSP** bottom-navigation item.
6. Scroll through:
   - Native service boundary
   - Unchanged model contract
   - From inference to an OS agent
   - AOSP integration checklist
   - Terminal proof commands
7. Return to **Dashboard**, then open **AI Coach** for the product context.

If the badge says **LINEAR FALLBACK**, do not claim that the neural model ran.
Restart with the verified build or use the transparent fallback as an
opportunity to explain Noctua’s failure behavior.

### 5.3 Open editor tabs in advance

Open them in this order so you are not searching during the talk:

1. `model/export_readiness_forecaster.py`
2. `noctua-ai/src/main/kotlin/com/noctua/ai/FeatureExtractor.kt`
3. `noctua-ai/src/main/kotlin/com/noctua/ai/ExecuTorchForecaster.kt`
4. `aosp-noctua-agent/service/aidl/com/noctua/aosp/INoctuaAgent.aidl`
5. `aosp-noctua-agent/service/src/NoctuaAgentService.cpp`
6. `aosp-noctua-agent/service/src/main.cpp`
7. `aosp-noctua-agent/Android.bp`
8. `aosp-noctua-agent/service/noctua_agent.rc`
9. `aosp-noctua-agent/sepolicy/noctua_agent.te`
10. `aosp-noctua-agent/framework/NoctuaAgentCoordinator.java`

Increase editor and terminal font sizes before screen sharing.

---

## 6. Prepare tonight: optional native AOSP proof

Only use this path if the complete image has already been built and flashed
successfully before the presentation.

From the repository root, the high-level integration is:

```bash
cd aosp-noctua-agent
export EXECUTORCH_ROOT=/absolute/path/to/executorch
export ANDROID_NDK_ROOT=/absolute/path/to/android-ndk
./scripts/build_executorch.sh arm64-v8a
# For an x86_64 Cuttlefish/emulator target, build x86_64 as well.

cp -R . "$AOSP/vendor/noctua/aosp-noctua-agent"
```

Include `device-config.mk` in the product/device configuration, then use the
normal AOSP flow:

```bash
source build/envsetup.sh
lunch <your-userdebug-target>
m noctua_agent noctua_client noctua_model
# Build and flash the complete images using the target's normal flow.
```

Pin the exporter and runtime to compatible ExecuTorch releases. The integration
script targets the ExecuTorch 1.4.1 source layout. Do not silently mix a `.pte`
from one incompatible release with a runtime from another.

### 6.1 Native preflight on the actual presentation device

```bash
adb devices
adb shell getenforce
adb shell service check noctua_agent
adb shell noctua_client
adb shell noctua_client 0.6 -0.5 -0.3 -0.4 -0.2 0.1 -0.4 0.3
```

The result is intentionally not a hard-coded expected score. Its shape is:

```text
readiness=<0..100> raw=<model output> inference=<N> us runtime=ExecuTorch AOSP / portable CPU
```

Capture known-good terminal output for your own backup only if it came from the
real device. Label any illustrative output as an output shape, never as a live
measurement.

### 6.2 If you want the app badge to say AOSP NATIVE SERVICE

Complete the platform-app integration as well:

- merge `app/NoctuaAospDemo.bp.snippet` into the real Soong app module;
- link `noctua_agent_aidl-java` and use `platform_apis: true`;
- sign with the platform certificate;
- integrate `BinderForecaster.kt` as the preferred forecaster, retaining the
  existing app-local loader and linear fallback; and
- run synchronous Binder inference on `Dispatchers.IO`, not the UI thread.

The native shell client is the independent fallback demo if app integration is
not ready. It still exercises AIDL, Binder, the daemon, ExecuTorch, and the same
model.

---

## 7. Full speaker script and actions

### 00:00–01:30 — Hook and promise

**Show:** Title slide. Keep the phone/emulator closed for the first 20 seconds.

**Say:**

> “A wearable can tell me that my readiness is 62. It does not necessarily tell
> me whether I should train hard, take a walk, or recover. Noctua started as an
> open-source answer to that coaching gap, with inference running locally using
> ExecuTorch.
>
> But for an AOSP audience, I want to go one level deeper. ‘On-device’ only says
> which machine runs the model. It does not say which process owns it, who can
> call it, what crosses IPC, or how the OS constrains it.
>
> So in the next 30 minutes I will keep one small model fixed and move its
> execution from an app process to a native AOSP service. Then I will show the
> policy and action boundary that turns that service into an OS-level agent.”

**Advance when you say:** “Keep one small model fixed.”

**Why this works:** It immediately gives platform developers a process,
security, and resource-management question rather than another app demo.

---

### 01:30–04:00 — Product context: forecast plus coaching

**Show:** Dashboard on the ordinary Android Studio emulator.

**Do:**

1. Point to the current readiness/forecast UI.
2. Open **AI Coach** briefly.
3. Return to **Dashboard** and point to **INFERENCE EXECUTION PATH**.

**Say:**

> “Noctua pulls the Oura collections into a typed wellness snapshot, derives
> sleep debt, HRV relative to a personal baseline, readiness trend, temperature,
> sleep efficiency, and activity, then produces two things: explainable coaching
> rules and a next-day readiness forecast.
>
> This is no-cloud-inference coaching. Oura is still the cloud source for the
> ring data; the additional inference and coaching step does not send that
> history to a cloud AI service. This demo uses deterministic local data, so the
> talk does not depend on an Oura login or conference Wi-Fi.”

**Point to the badge and say:**

> “The badge matters. This ordinary APK says `APP PROCESS`, so ExecuTorch is
> running through the app’s AAR/JNI path. It is not secretly calling the AOSP
> service I will show later.”

**Transition:**

> “Before moving the process boundary, let’s fix the model contract.”

---

### 04:00–07:00 — PyTorch to ExecuTorch

**Show:** `model/export_readiness_forecaster.py`.

Highlight this architecture:

```python
self.net = nn.Sequential(
    nn.Linear(8, 32),
    nn.ReLU(),
    nn.Linear(32, 16),
    nn.ReLU(),
    nn.Linear(16, 1),
)
```

Then highlight the export sequence:

```python
aten = torch_export(model, example)
edge = to_edge(aten)
program = edge.to_executorch()
```

**Say:**

> “The model is deliberately tiny: eight inputs, layers of 32 and 16, one
> output—833 parameters in total. I am not using a small model because the
> platform problem is trivial. I am using it so the platform mechanics remain
> visible instead of spending the talk on model size and quantization.
>
> The input is a float32 tensor of shape `[1,8]`; the output is `[1,1]`, a
> readiness score. That contract is more important than the layer count. It must
> remain identical in Python, Kotlin, AIDL, and C++.
>
> Export uses `torch.export`, lowers to Edge with `to_edge`, and emits an
> ExecuTorch program with `to_executorch`. The resulting `.pte` is the artifact
> both runtimes consume.”

**Be transparent:**

> “This sample has a synthetic warm-start to demonstrate the complete pipeline.
> It is not a clinically trained or medically validated model.”

**Transition:**

> “Now let’s see the exact values that enter that tensor.”

---

### 07:00–09:30 — Existing app-local inference

**Show:** `FeatureExtractor.kt`, specifically `WellnessFeatures.toVector()`.

```kotlin
fun toVector(): FloatArray = floatArrayOf(
    (sleepDebtHours / 10f).coerceIn(-1f, 1f),
    ((hrvZScore ?: 0f) / 3f).coerceIn(-1f, 1f),
    // ...six more normalized features
)
```

**Say:**

> “Feature extraction stays deterministic and testable. Missing values receive
> neutral defaults, and every value is clamped into roughly the same normalized
> range. The eight positions are stable: sleep debt, HRV z-score, readiness
> trend, two trailing means, temperature, efficiency, and steps.”

**Show:** `ExecuTorchForecaster.kt`.

Point to `Module.load`, `Tensor.fromBlob`, `EValue.from`, and `forward`.

**Say:**

> “On Android, Noctua loads the `.pte`, wraps the float array as a `[1,8]`
> tensor, wraps that in an `EValue`, and calls `forward`. The adapter is
> reflective so the core library remains usable without the runtime; if the
> neural path is unavailable, Noctua can fall back to a transparent linear
> forecaster.
>
> Notice what I am preserving in the AOSP version: feature extraction, model
> artifact, and output meaning. Only the execution host and IPC path change.”

**Transition:**

> “So why move such a small model into the OS at all?”

---

### 09:30–12:00 — The AOSP process boundary

**Show:** The app’s **AOSP** tab, especially **Native service boundary**.

**Say:**

> “For a single personal app, app-local inference is often the right answer.
> Moving it into AOSP adds real integration cost. The platform design becomes
> useful when trusted platform components need a shared inference capability,
> centralized resource policy, framework actions, or a security boundary that is
> explicit in the image.
>
> Noctua’s design moves execution, not the raw health database. The app still
> owns Oura credentials and detailed history. Only eight normalized floats cross
> Binder. The daemon returns a score, the raw scalar, service-side timing, and a
> runtime label.”

Draw or point to:

```text
Noctua platform app                  AOSP native process
FeatureExtractor                     noctua_agent
HeuristicInsightEngine               INoctuaAgent AIDL
BinderForecaster  ─── 8 floats ───▶  ExecuTorch C++ Module
Compose UI                           readiness_forecaster.pte
```

**Say:**

> “The daemon is separate from `system_server`. A native model-load failure
> should not take down Android’s core system process. A separate process also
> gives me a narrow SELinux domain, independent memory and thread policy, and
> direct use of ExecuTorch’s C++ runtime.”

**Transition:**

> “Let’s make that boundary observable.”

---

### 12:00–16:30 — Live demo

Keep this section to four and a half minutes. The sequence is deliberate:
current truth first, architecture second, native proof third, code last.

#### 12:00–12:45 — Identify the current process

**Do:** Show **Dashboard → INFERENCE EXECUTION PATH**.

**Say on the ordinary APK:**

> “This says `APP PROCESS`: Noctua app, JNI/AAR, then the `.pte`. The UI is
> reporting the actual execution host rather than using ‘on-device’ as a vague
> label.”

**Say on a completed platform build:**

> “This says `AOSP NATIVE SERVICE`: the app resolves `noctua_agent` through
> ServiceManager and the same forecast now crosses Binder.”

#### 12:45–13:30 — Show the platform boundary

**Do:** Tap **View AOSP architecture**.

**Say:**

> “Here are the four boundaries: the platform app, the AIDL interface, the
> isolated daemon, and the read-only model on the system partition. Raw history
> and OAuth tokens remain outside the daemon.”

#### 13:30–14:00 — Show the unchanged contract

**Do:** Point to **Unchanged model contract** on the same screen.

**Say:**

> “`WellnessFeatures.toVector()` still produces eight floats. The model still
> returns one readiness value. I have not invented a second model for AOSP.”

#### 14:00–14:45 — Prove platform registration

**Only on a pre-verified AOSP device, run:**

```bash
adb shell getenforce
adb shell service check noctua_agent
```

**Say:**

> “`service check` asks ServiceManager for the name `noctua_agent`. This is not
> a local socket hidden inside the app; it is a named platform service. I also
> keep SELinux enforcing during the demo.”

**Without a native device, say instead:**

> “This regular emulator does not contain that custom platform service, so I
> will not run a command whose result I cannot demonstrate. The registration and
> client source are next; these are the exact commands for the integrated image.”

Show the commands on the AOSP tab or in `docs/screenshots/aosp_commands.png` as
a command reference, not as fabricated proof.

#### 14:45–15:45 — Run neutral and stressed inputs

**Only on a pre-verified AOSP device, run:**

```bash
adb shell noctua_client
adb shell noctua_client 0.6 -0.5 -0.3 -0.4 -0.2 0.1 -0.4 0.3
```

**Say:**

> “The default input is eight zeroes. The second input represents higher sleep
> debt, HRV below baseline, a negative readiness trend, lower trailing scores,
> and reduced sleep efficiency. I am comparing behavior and timing, not
> promising a fixed score.
>
> The reported timing is measured inside the service around serialized model
> execution. It is not an end-to-end Binder latency benchmark.”

If the stressed score is not lower, do not explain it away. Say that the sample
model is synthetic and use the result to discuss training and evaluation.

#### 15:45–16:30 — Show the native boundary

**Show:** `NoctuaAgentService.cpp`.

```cpp
auto input = make_tensor_ptr({1, static_cast<int>(kFeatureCount)}, features);
const auto outputs = module_.forward(input);
```

**Say:**

> “This is the same operation as the Android path, now directly in C++: create
> the `[1,8]` tensor, call `forward`, read the scalar, and return it through the
> typed result parcelable.”

**Transition:**

> “The two lines of inference are easy. Making them a safe platform service is
> the real work.”

---

### 16:30–20:00 — Native code deep dive

#### AIDL: make the contract explicit

**Show:** `INoctuaAgent.aidl` and `ForecastResult.aidl`.

```aidl
interface INoctuaAgent {
    ForecastResult forecast(in float[] features);
    @utf8InCpp String getRuntimeInfo();
}
```

```aidl
parcelable ForecastResult {
    int readinessScore;
    float rawScore;
    long inferenceMicros;
    @utf8InCpp String runtime;
}
```

**Say:**

> “AIDL gives me typed inputs and outputs, generated C++ and Java bindings,
> ServiceManager discovery, Binder death handling, and normal AOSP SELinux
> integration. I am using synchronous request/reply because this tiny forecast
> is synchronous. A streaming LLM would need a different interface with
> cancellation and backpressure.”

#### Validate at the trust boundary

**Show:** `isValidFeatureVector` and the start of `forecast`.

**Say:**

> “The daemon does not trust the caller to honor comments in an AIDL file. It
> checks that there are exactly eight values, every value is finite, and every
> value is in `[-1,1]`. It checks the output as well before converting it to a
> user-facing score.”

#### Make native execution safe to call

Point to:

```cpp
const Error error = module_.load_forward();
```

```cpp
std::lock_guard<std::mutex> lock(mutex_);
```

**Say:**

> “The forward method is loaded eagerly. The service does not register itself
> if model initialization fails. Binder can dispatch concurrent calls, so a
> mutex serializes this demo’s `Module::forward` calls. That is simple and
> appropriate for the tiny model; larger workloads need a real queue, resource
> limits, cancellation, and backpressure.”

#### Register only a working service

**Show:** `service/src/main.cpp`.

**Say:**

> “`main` loads the model first, then calls `addService("noctua_agent")`, starts
> the Binder thread pool, and joins it. The ordering matters: a client should
> not discover a service whose model could not initialize.”

**Transition:**

> “Now the image has to install and constrain all of this correctly.”

---

### 20:00–23:00 — Soong, init, and SELinux

#### Soong: build the runtime for Android

**Show:** `Android.bp` and, briefly, `scripts/build_executorch.sh`.

**Say:**

> “The app AAR is not a reusable AOSP system library. I cross-compile the
> ExecuTorch C++ runtime and portable operator library for the target ABI, then
> expose them as Soong prebuilt shared libraries. `noctua_agent` links those
> libraries and the generated C++ AIDL backend.
>
> The model is installed by `prebuilt_etc` under
> `/system/etc/noctua/readiness_forecaster.pte`. The daemon, shell client, and
> model are added to `PRODUCT_PACKAGES`.
>
> I start with portable CPU kernels to minimize integration risk. XNNPACK,
> Vulkan, QNN, or another accelerator can come later as an export and runtime
> decision; the AIDL contract does not need to change.”

#### init: define the process identity

**Show:** `service/noctua_agent.rc`.

```text
service noctua_agent /system/bin/noctua_agent
    class late_start
    user system
    group system
    seclabel u:r:noctua_agent:s0
```

**Say:**

> “init gives the daemon its executable, lifecycle, user, groups, and SELinux
> domain. This cannot be reproduced reliably by pushing one binary onto a
> device; the binary, registration, model, and policy belong in the image.”

#### SELinux: grant only the intended path

**Show:** `sepolicy/noctua_agent.te`, `service_contexts`, and
`platform_app.te`.

**Say:**

> “The daemon gets its own domain, Binder use, service registration, and
> read/map access to the model. It deliberately gets no `net_domain`; inference
> does not need network access.
>
> The demo permits `shell` and `platform_app`, not blanket `untrusted_app`
> access. One important nuance: SELinux domain access is not per-package
> authorization. If I need per-package control, I put a signature-level
> permission check in a framework front-end.”

**Transition:**

> “At this point I have a secure inference call—not yet an agent.”

---

### 23:00–26:30 — Turn inference into an OS-level agent

**Show:** AOSP tab → **From inference to an OS agent**.

```text
TRIGGER → SENSE → INFER → DECIDE → ACT → AUDIT
```

Walk through one row at a time:

| Stage | Say this |
|---|---|
| **TRIGGER** | “Start with an explicit user request. A scheduled job, boot event, or health-data update requires consent and user-aware lifecycle handling.” |
| **SENSE** | “Noctua’s Oura adapter and `FeatureExtractor` produce normalized features. A future provider adapter could use Health Connect where appropriate.” |
| **INFER** | “The isolated native daemon performs only the model execution.” |
| **DECIDE** | “A deterministic framework policy decides what the score means. Keep this separate from the neural network so behavior is reviewable.” |
| **ACT** | “Start with a local, non-sensitive suggestion. Do not silently change DND, alarms, networking, or power policy.” |
| **AUDIT** | “Record model/runtime version, latency, decision, and action—not raw biometrics. This is a production requirement, not a reason to log health history.” |

**Show:** `framework/NoctuaAgentCoordinator.java`.

```java
ForecastResult forecast = agent.forecast(features);
AgentAction action = forecast.readinessScore < RECOVERY_THRESHOLD
        ? AgentAction.SUGGEST_RECOVERY
        : AgentAction.NO_ACTION;
dispatch(action);
```

**Say:**

> “The coordinator is the policy/action half. Below a transparent threshold of
> 60, it chooses `SUGGEST_RECOVERY`; otherwise it does nothing. The action is a
> local notification saying that the on-device forecast suggests a lighter day.
> It contains no HRV history, sleep periods, tokens, or detailed biometrics.”

**Be clear about implementation scope:**

> “The native daemon and this coordinator demonstrate the infer, decide, and
> act boundaries. Production trigger registration, full audit storage,
> per-user lifecycle, and authorization still need to be integrated and tested
> for the target product.”

**Key line:**

> “The model proposes a score. Platform policy decides whether to act. The user
> remains in control.”

---

### 26:30–28:30 — What production hardening means

**Show:** A short checklist slide or the AOSP guide.

**Say:**

> “A production service needs more than a successful `forward()` call:
>
> - Freeze and version the AIDL interface; this demo uses `unstable: true`.
> - Add caller authentication and per-package authorization where required.
> - Keep Binder calls off the UI thread and define timeouts and death handling.
> - Add rate limits, memory limits, thermal and battery policy, and workload
>   queues before running larger models.
> - Key state by `UserHandle`, avoid cross-user caches, and stop work when the
>   user is stopped.
> - Define signed, compatible, rollback-safe model updates. The read-only system
>   partition model in this demo naturally updates with the system image.
> - Audit behavior without logging raw health data.”

**Connect to larger models:**

> “For an LLM coach, I would change the AIDL boundary to asynchronous streaming
> with cancellation and backpressure. Memory, storage, thermal behavior, and
> model delivery become first-class platform concerns. The tiny forecaster is
> the first step because it lets us validate those boundaries independently.”

---

### 28:30–30:00 — Recap and closing pitch

**Show:** The architecture slide or AOSP tab. Stop live typing.

**Say:**

> “Three things to take away.
>
> First, preserve the model contract: eight normalized features in, one
> readiness value out. That let the same `.pte` move from Android’s app runtime
> to native C++ without redesigning the product.
>
> Second, make the execution boundary explicit. AIDL defines what crosses the
> boundary; ServiceManager makes it discoverable; Soong and init install it;
> SELinux constrains it. The raw health history and credentials do not cross
> that boundary.
>
> Third, an OS agent is not just a model in a daemon. Trigger, sense, infer,
> decide, act, and audit are separate responsibilities. Keeping policy outside
> the model makes the action bounded, explainable, and reviewable.
>
> Noctua’s app path and AOSP path are both useful. The architectural point is to
> choose the process and privilege boundary deliberately—not hide it behind the
> phrase ‘on-device AI.’ Thank you.”

**Final slide text:**

```text
Same model. Explicit process boundary. Bounded OS action.
github.com/RanjithRagavan/Noctua
```

---

## 8. Demo failure playbook

### The Android Studio app will not launch

Spend no more than 45 seconds troubleshooting.

1. Switch to `docs/screenshots/dashboard.png`.
2. Switch to `docs/screenshots/aosp_architecture.png`.
3. Say: “I will use the captured app view and keep the source walkthrough
   live.”
4. Continue at the export/model-contract section.

### The ordinary emulator reports APP PROCESS

That is correct. It is not a failure. Explain that the native path requires an
AOSP image, platform signing, init, and SELinux integration.

### The app reports LINEAR FALLBACK

Do not call it ExecuTorch inference. Explain that Noctua deliberately falls
back to a deterministic model when the neural runtime or model cannot load.
Use the preverified build or screenshots for the neural product path.

### `service check noctua_agent` fails

Try at most one quick check:

```bash
adb shell logcat -s NoctuaAgent
```

Then stop debugging live and say:

> “The service is not registered on this image, so I will use the source and
> the previously verified app path rather than spend the slot on image
> debugging.”

Useful later diagnostics are:

```bash
adb shell ps -A | grep noctua
adb shell logcat -s NoctuaAgent
adb shell dmesg | grep -i avc
adb shell dumpsys -l | grep noctua
```

Do not make the device permissive to rescue the demo.

### The client returns an error

Check whether the arguments are exactly eight finite values in `[-1,1]`. If the
error persists, show `NoctuaAgentService.cpp` and explain the validation and
failure boundary. Do not promise a score you have not observed.

### The score or latency surprises you

Say:

> “That is the measured output of this sample model and runtime, rather than a
> canned value. The model is synthetic, and this single timing is not a
> benchmark.”

### Screen sharing or USB fails

Use the screenshots and source. The story still works because the model
contract, process diagram, and agent loop carry the argument. Internet access is
not required for demo mode.

---

## 9. Five-minute compressed version

Use this if the slot is shortened.

| Time | Action and message |
|---|---|
| 00:00–00:30 | “Noctua adds local readiness forecasting and coaching to Oura data. I will move the same model from an app process into AOSP.” |
| 00:30–01:00 | Show Dashboard and `APP PROCESS`; state the no-cloud-inference scope. |
| 01:00–01:30 | Show `[1,8] → readiness_forecaster.pte → [1,1]`; say that this contract stays fixed. |
| 01:30–02:30 | Show the AOSP tab: app → AIDL/Binder → isolated native daemon → model. |
| 02:30–03:15 | On a verified AOSP device only, run `service check noctua_agent` and `noctua_client`. Otherwise show the exact commands and source without implying execution. |
| 03:15–04:00 | Show `make_tensor_ptr({1,8}, features)` and `module_.forward(input)`; mention validation, mutex, and eager model load. |
| 04:00–04:35 | Show `TRIGGER → SENSE → INFER → DECIDE → ACT → AUDIT`; the coordinator applies a threshold and posts a local suggestion. |
| 04:35–05:00 | Close: “Only eight normalized floats cross Binder. The daemon has no network domain. The model infers; platform policy decides; the user controls the action.” |

---

## 10. What to claim—and what not to claim

### Safe, precise claims

- “No cloud AI inference is used for Noctua’s forecast and coaching path.”
- “The ordinary app runs ExecuTorch inside the app process.”
- “The AOSP demo preserves the same `.pte` and `[1,8] → [1,1]` contract.”
- “Only eight normalized floats and the forecast result cross this Binder API.”
- “The daemon has a dedicated SELinux domain and no `net_domain` rule.”
- “The coordinator demonstrates deterministic policy and a local notification.”
- “The sample model uses a synthetic warm-start and is not medical software.”

### Avoid these claims

- “Oura data has never been in any cloud.” Oura Cloud is the source of the ring
  data; Noctua avoids an additional cloud AI inference step.
- “This works on stock Android as an ordinary APK.” The native platform path
  does not.
- “This is a HAL.” It is a native/framework system service.
- “The native service is live” when only the ordinary emulator has been run.
- “Inference is always N milliseconds” from one or two samples.
- “The agent is fully production-ready.” Trigger lifecycle, authorization,
  audit, multi-user behavior, and resource controls need product integration.
- “The score is medical advice.” It is a small demonstration forecaster.

---

## 11. Final rehearsal checklist

### Tonight

- [ ] Pull the exact `main` revision you will present.
- [ ] Run `./gradlew test :example-app:assembleDebug`.
- [ ] Launch **example-app** in Android Studio and rehearse the tap path.
- [ ] Verify `APP PROCESS` on the ordinary build.
- [ ] Open the ten source tabs listed above.
- [ ] If using AOSP hardware, verify both client commands on that exact device.
- [ ] Capture only genuine backup output from that verified run.
- [ ] Load the dashboard, architecture, and command screenshots locally.
- [ ] Rehearse once with the native path and once with the fallback path.
- [ ] Time the full talk; preserve the final 90 seconds for the close.

### Sixty minutes before the talk

- [ ] Charge the laptop and device; bring the required cable/adapter.
- [ ] Disable personal notifications and sensitive screen content.
- [ ] Increase IDE, terminal, and emulator font sizes.
- [ ] Confirm `adb devices` shows the intended target only, if possible.
- [ ] Reopen Android Studio on the repository root.
- [ ] Reopen the app in demo mode and the AOSP tab.
- [ ] Keep the full guide open at `aosp-noctua-agent/README.md`.
- [ ] Do not upgrade dependencies, re-export the model, or reflash at the venue.

### On stage

- [ ] Say the one-breath pitch slowly.
- [ ] Make the execution boundary visible before discussing implementation.
- [ ] Never hide a failed or unavailable demo path.
- [ ] At 20 minutes, move to Soong/init/SELinux.
- [ ] At 28:30, deliver the three-part recap.
- [ ] Leave Q&A with the repository link visible.

---

## 12. Likely AOSP questions and concise answers

### “Why not just keep this in the app?”

For one personal app, you should. App-local inference is simpler and is the
right default. The OS service becomes valuable when trusted platform components
need shared inference, central resource policy, or framework actions. The point
of the demo is to expose that trade-off, not claim every model belongs in AOSP.

### “Is this a HAL?”

No. This is an application-level inference contract exposed as a native system
service. A vendor HAL is appropriate when abstracting vendor hardware. An
accelerator backend can sit underneath this service without changing the AIDL.

### “Why not load ExecuTorch into system_server?”

Fault isolation, a smaller privilege surface, independent memory/thread policy,
and direct use of the C++ runtime. A bad model load or native crash in a
separate daemon should not crash `system_server`.

### “Why Binder instead of a Unix socket?”

Typed AIDL, generated backends, ServiceManager discovery, caller identity,
death handling, standard tooling, and SELinux integration. A socket would move
bytes; Binder provides the platform contract.

### “Does this run on stock Android?”

The app-local path does. The native service path requires an AOSP image,
platform APIs and signing for the app variant, init integration, and SELinux
rules. An ordinary `untrusted_app` is intentionally not granted access.

### “Does the Oura Cloud dependency contradict the privacy claim?”

No, if the claim is precise. Oura supplies the ring data through its cloud API.
Noctua does not add a cloud AI inference service for forecasting and coaching.
Within the AOSP design, raw records and Oura credentials stay on the app side;
only normalized features cross Binder, and the daemon has no network rule.

### “Why is the model so small?”

To make export, IPC, native execution, and security easy to inspect. The sample
has 833 parameters and a synthetic warm-start. It is a platform demonstration,
not a clinically validated predictor.

### “How would you use an accelerator?”

Start with portable CPU kernels, validate the contract, then introduce a backend
such as XNNPACK, Vulkan, or QNN where supported. That affects export, runtime
linking, operator support, and validation—not the eight-float AIDL contract.

### “Is `inferenceMicros` end-to-end latency?”

No. It is measured inside the native service around serialized execution. It
excludes the client’s Binder transport and UI work, and eager model loading is
done before service registration. Use separate client-side measurements for
end-to-end latency.

### “Why is the AIDL marked unstable?”

To keep the sample small. A production platform interface should be frozen and
versioned, with an explicit compatibility policy across model, runtime, and
system image updates.

### “How do you update the model?”

In this demo it is read-only on the system partition and updates with the
image. A production downloadable-model design needs signature verification,
version compatibility, atomic activation, rollback, storage limits, and an
audit record. That delivery system is not implemented in this sample.

### “How do you authorize individual apps?”

Do not treat the `platform_app` SELinux domain as per-package authorization.
Use a framework front-end with a signature-level permission or explicit caller
checks, then enforce user/package policy there while retaining SELinux between
processes.

### “What changes for an on-device LLM coach?”

Use asynchronous streaming AIDL with cancellation and backpressure; define
memory, storage, thermal, battery, queueing, and model-update policies. The
agent loop remains, but the inference service needs a much richer resource and
lifecycle design.

### “Is the audit stage implemented?”

The runbook defines the required audit boundary, but the sample coordinator is
minimal and focuses on decide/act. Production audit storage and reporting still
need implementation. Audit model/runtime version, latency, decision, and action
without raw biometric history.

---

## 13. Repository map for the talk

```text
model/export_readiness_forecaster.py
  PyTorch model and .pte export

noctua-ai/src/main/kotlin/com/noctua/ai/FeatureExtractor.kt
  WellnessFeatures and the eight-value normalized vector

noctua-ai/src/main/kotlin/com/noctua/ai/ExecuTorchForecaster.kt
  Existing app-process Android runtime path

aosp-noctua-agent/service/aidl/
  INoctuaAgent and ForecastResult

aosp-noctua-agent/service/src/
  Native model loading, validation, inference, and registration

aosp-noctua-agent/client/noctua_client.cpp
  Deterministic shell-driven native demo

aosp-noctua-agent/app/BinderForecaster.kt
  Drop-in platform-app ReadinessForecaster

aosp-noctua-agent/framework/NoctuaAgentCoordinator.java
  Deterministic decide/act layer and local notification

aosp-noctua-agent/Android.bp
  AIDL backends, ExecuTorch prebuilts, daemon, client, model

aosp-noctua-agent/service/noctua_agent.rc
  init daemon definition

aosp-noctua-agent/sepolicy/
  Dedicated domain and explicit caller rules

aosp-noctua-agent/README.md
  Complete build, integration, diagnostics, and security guide
```
