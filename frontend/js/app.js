// ===== Configuration =====
const API_BASE = "http://localhost:8082/api";

// ===== State =====
let authHeader = sessionStorage.getItem("crm_auth") || null;
let currentLeadId = null;
let leadsCache = [];

// ===== DOM refs =====
const loginView = document.getElementById("login-view");
const dashboardView = document.getElementById("dashboard-view");
const loginForm = document.getElementById("login-form");
const loginError = document.getElementById("login-error");
const currentUserEl = document.getElementById("current-user");
const logoutBtn = document.getElementById("logout-btn");

const searchInput = document.getElementById("search-input");
const statusFilter = document.getElementById("status-filter");
const newLeadBtn = document.getElementById("new-lead-btn");
const leadsTbody = document.getElementById("leads-tbody");
const emptyState = document.getElementById("empty-state");

const leadModal = document.getElementById("lead-modal");
const leadForm = document.getElementById("lead-form");
const leadFormError = document.getElementById("lead-form-error");
const modalTitle = document.getElementById("modal-title");
const cancelModalBtn = document.getElementById("cancel-modal-btn");
const notesSection = document.getElementById("notes-section");
const notesList = document.getElementById("notes-list");
const noteForm = document.getElementById("note-form");

const toast = document.getElementById("toast");

// ===== Init =====
document.addEventListener("DOMContentLoaded", () => {
  if (authHeader) {
    verifySession();
  }
});

// ===== Auth =====
loginForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  const username = document.getElementById("login-username").value.trim();
  const password = document.getElementById("login-password").value;
  const candidate = "Basic " + btoa(`${username}:${password}`);

  loginError.textContent = "";
  try {
    const res = await fetch(`${API_BASE}/auth/me`, {
      headers: { Authorization: candidate },
    });
    if (!res.ok) throw new Error("Invalid username or password.");
    const data = await res.json();

    authHeader = candidate;
    sessionStorage.setItem("crm_auth", authHeader);
    currentUserEl.textContent = data.username;
    showDashboard();
    loadLeads();
  } catch (err) {
    loginError.textContent = err.message || "Login failed. Check your credentials and that the backend is running.";
  }
});

logoutBtn.addEventListener("click", () => {
  authHeader = null;
  sessionStorage.removeItem("crm_auth");
  showLogin();
});

async function verifySession() {
  try {
    const res = await fetch(`${API_BASE}/auth/me`, { headers: authHeaders() });
    if (!res.ok) throw new Error();
    const data = await res.json();
    currentUserEl.textContent = data.username;
    showDashboard();
    loadLeads();
  } catch {
    authHeader = null;
    sessionStorage.removeItem("crm_auth");
    showLogin();
  }
}

function authHeaders() {
  return {
    Authorization: authHeader,
    "Content-Type": "application/json",
  };
}

function showDashboard() {
  loginView.classList.add("hidden");
  dashboardView.classList.remove("hidden");
}
function showLogin() {
  dashboardView.classList.add("hidden");
  loginView.classList.remove("hidden");
}

// ===== Leads: load + render =====
async function loadLeads() {
  const status = statusFilter.value;
  const search = searchInput.value.trim();

  const params = new URLSearchParams();
  if (status) params.append("status", status);
  if (search) params.append("search", search);

  try {
    const res = await fetch(`${API_BASE}/leads?${params.toString()}`, { headers: authHeaders() });
    if (!res.ok) throw new Error("Failed to load leads.");
    leadsCache = await res.json();
    renderLeads(leadsCache);
  } catch (err) {
    showToast(err.message, true);
  }
}

function renderLeads(leads) {
  leadsTbody.innerHTML = "";
  emptyState.classList.toggle("hidden", leads.length > 0);

  leads.forEach((lead) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td data-label="Name">${escapeHtml(lead.name)}</td>
      <td data-label="Email">${escapeHtml(lead.email)}</td>
      <td data-label="Phone">${escapeHtml(lead.phone || "—")}</td>
      <td data-label="Source">${escapeHtml(lead.source)}</td>
      <td data-label="Status"><span class="status-pill status-${lead.status}">${lead.status}</span></td>
      <td data-label="Created">${formatDate(lead.createdAt)}</td>
      <td class="row-actions">
        <button class="btn-ghost" data-action="edit" data-id="${lead.id}">Edit</button>
        <button class="btn-ghost" data-action="delete" data-id="${lead.id}">Delete</button>
      </td>
    `;
    leadsTbody.appendChild(tr);
  });
}

leadsTbody.addEventListener("click", (e) => {
  const btn = e.target.closest("button");
  if (!btn) return;
  const id = btn.dataset.id;
  if (btn.dataset.action === "edit") openLeadModal(id);
  if (btn.dataset.action === "delete") deleteLead(id);
});

searchInput.addEventListener("input", debounce(loadLeads, 300));
statusFilter.addEventListener("change", loadLeads);

// ===== Lead modal: create / edit =====
newLeadBtn.addEventListener("click", () => openLeadModal(null));
cancelModalBtn.addEventListener("click", closeLeadModal);

function openLeadModal(id) {
  leadFormError.textContent = "";
  leadForm.reset();
  currentLeadId = id;

  if (id) {
    const lead = leadsCache.find((l) => String(l.id) === String(id));
    modalTitle.textContent = "Edit Lead";
    document.getElementById("lead-id").value = lead.id;
    document.getElementById("lead-name").value = lead.name;
    document.getElementById("lead-email").value = lead.email;
    document.getElementById("lead-phone").value = lead.phone || "";
    document.getElementById("lead-source").value = lead.source;
    document.getElementById("lead-status").value = lead.status;
    notesSection.classList.remove("hidden");
    renderNotes(lead.notes || []);
  } else {
    modalTitle.textContent = "New Lead";
    notesSection.classList.add("hidden");
  }

  leadModal.classList.remove("hidden");
}

function closeLeadModal() {
  leadModal.classList.add("hidden");
  currentLeadId = null;
}

leadForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  leadFormError.textContent = "";

  const payload = {
    name: document.getElementById("lead-name").value.trim(),
    email: document.getElementById("lead-email").value.trim(),
    phone: document.getElementById("lead-phone").value.trim(),
    source: document.getElementById("lead-source").value,
    status: document.getElementById("lead-status").value,
  };

  const isEdit = Boolean(currentLeadId);
  const url = isEdit ? `${API_BASE}/leads/${currentLeadId}` : `${API_BASE}/leads`;
  const method = isEdit ? "PUT" : "POST";

  try {
    const res = await fetch(url, { method, headers: authHeaders(), body: JSON.stringify(payload) });
    if (!res.ok) {
      const errBody = await res.json().catch(() => ({}));
      throw new Error(errBody.message || "Could not save lead. Check the form fields.");
    }
    showToast(isEdit ? "Lead updated." : "Lead created.");
    closeLeadModal();
    loadLeads();
  } catch (err) {
    leadFormError.textContent = err.message;
  }
});

async function deleteLead(id) {
  if (!confirm("Delete this lead? This cannot be undone.")) return;
  try {
    const res = await fetch(`${API_BASE}/leads/${id}`, { method: "DELETE", headers: authHeaders() });
    if (!res.ok) throw new Error("Failed to delete lead.");
    showToast("Lead deleted.");
    loadLeads();
  } catch (err) {
    showToast(err.message, true);
  }
}

// ===== Notes =====
function renderNotes(notes) {
  notesList.innerHTML = notes.length
    ? notes.map((n) => `
        <div class="note-item">
          ${escapeHtml(n.content)}
          <span class="note-time">${formatDate(n.createdAt)}</span>
        </div>`).join("")
    : `<p style="color:var(--text-muted); font-size:13px;">No notes yet.</p>`;
}

noteForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  const content = document.getElementById("note-content").value.trim();
  if (!content || !currentLeadId) return;

  try {
    const res = await fetch(`${API_BASE}/leads/${currentLeadId}/notes`, {
      method: "POST",
      headers: authHeaders(),
      body: JSON.stringify({ content }),
    });
    if (!res.ok) throw new Error("Failed to add note.");

    const notesRes = await fetch(`${API_BASE}/leads/${currentLeadId}/notes`, { headers: authHeaders() });
    const notes = await notesRes.json();
    renderNotes(notes);
    document.getElementById("note-content").value = "";
    showToast("Note added.");
  } catch (err) {
    showToast(err.message, true);
  }
});

// ===== Utilities =====
function escapeHtml(str) {
  const div = document.createElement("div");
  div.textContent = str ?? "";
  return div.innerHTML;
}

function formatDate(iso) {
  if (!iso) return "—";
  const d = new Date(iso);
  return d.toLocaleDateString("en-IN", { day: "2-digit", month: "short", year: "numeric" });
}

function debounce(fn, delay) {
  let timer;
  return (...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => fn(...args), delay);
  };
}

function showToast(message, isError = false) {
  toast.textContent = message;
  toast.classList.toggle("error", isError);
  toast.classList.remove("hidden");
  setTimeout(() => toast.classList.add("hidden"), 2800);
}
