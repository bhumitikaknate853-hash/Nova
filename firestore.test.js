const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("unauthenticated user cannot read or write", async () => {
  const unauthedDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthedDb.doc(`users/${ALICE_UID}`).get());
  await assertFails(unauthedDb.doc(`users/${ALICE_UID}`).set({
    userId: ALICE_UID,
    displayName: "Alice",
    email: "alice@example.com",
    createdAt: new Date(),
    updatedAt: new Date(),
  }));
});

test("Alice can create and read her own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.doc(`users/${ALICE_UID}`).set({
    userId: ALICE_UID,
    displayName: "Alice",
    email: "alice@example.com",
    createdAt: new Date(),
    updatedAt: new Date(),
  }));
  await assertSucceeds(aliceDb.doc(`users/${ALICE_UID}`).get());
});

test("Bob cannot read or write Alice's data", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(aliceDb.doc(`users/${ALICE_UID}`).set({
    userId: ALICE_UID,
    displayName: "Alice",
    email: "alice@example.com",
    createdAt: new Date(),
    updatedAt: new Date(),
  }));

  await assertFails(bobDb.doc(`users/${ALICE_UID}`).get());
  await assertFails(bobDb.doc(`users/${ALICE_UID}`).set({
    userId: BOB_UID,
    displayName: "Hacked",
    email: "hacker@example.com",
    createdAt: new Date(),
    updatedAt: new Date(),
  }));
});

test("Alice can manage chat sessions and messages", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  const sessionRef = aliceDb.doc(`users/${ALICE_UID}/chat_sessions/session_1`);
  await assertSucceeds(sessionRef.set({
    id: "session_1",
    userId: ALICE_UID,
    title: "New Chat",
    agentId: "general",
    lastMessage: "Hello",
    messageCount: 1,
    createdAt: new Date(),
    updatedAt: new Date(),
  }));

  const msgRef = aliceDb.doc(`users/${ALICE_UID}/chat_sessions/session_1/messages/msg_1`);
  await assertSucceeds(msgRef.set({
    id: "msg_1",
    userId: ALICE_UID,
    sessionId: "session_1",
    role: "user",
    content: "Hello Nova",
    agentId: "general",
    hasImage: false,
    createdAt: new Date(),
  }));

  // Bob cannot read Alice's messages
  await assertFails(bobDb.doc(`users/${ALICE_UID}/chat_sessions/session_1/messages/msg_1`).get());
});

test("Alice can create and read memories and creations", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();

  await assertSucceeds(aliceDb.doc(`users/${ALICE_UID}/memories/mem_1`).set({
    id: "mem_1",
    userId: ALICE_UID,
    key: "favorite_color",
    value: "cyan",
    category: "preference",
    createdAt: new Date(),
    updatedAt: new Date(),
  }));

  await assertSucceeds(aliceDb.doc(`users/${ALICE_UID}/creations/create_1`).set({
    id: "create_1",
    userId: ALICE_UID,
    prompt: "Cyberpunk cityscape with teal neon",
    aspectRatio: "1:1",
    style: "Sci-Fi",
    status: "completed",
    imageUrl: "https://example.com/image.png",
    createdAt: new Date(),
  }));
});
