const loginForm = document.getElementById("login-form");
const loader = document.getElementById("loader");
const modal = document.getElementById("modal");
const modalText = document.getElementById("modalText");
const modalCancel = document.getElementById("modalCancel");
const modalConfirm = document.getElementById("modalConfirm");

function showLoader(show) {
  loader.classList.toggle("hidden", !show);
}

function showModal(text, onConfirm) {
  modalText.innerText = text;
  modal.classList.remove("hidden");

  modalCancel.onclick = () => modal.classList.add("hidden");
  modalConfirm.onclick = () => {
    modal.classList.add("hidden");
    onConfirm();
  };
}

loginForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  const loginId = document.getElementById("loginId").value.trim();
  const password = document.getElementById("password").value;

  const isPhone = /^[0-9]+$/.test(loginId);
  if (!loginId || (isPhone && loginId.length !== 10) || password.length < 8) {
    showModal("Please enter valid credentials.", () => {});
    return;
  }

  let deviceId = localStorage.getItem("device_id");
  if (!deviceId) {
    deviceId = crypto.randomUUID();
    localStorage.setItem("device_id", deviceId);
  }

  showLoader(true);

  try {
    const res = await fetch("http://localhost:8080/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ loginId, password, deviceId })
    });

    const data = await res.json();
    showLoader(false);

    if (data.status === "LOGIN_SUCCESS") {
      localStorage.setItem("userLogin", loginId);
      window.location.href = "index.html";
    }
    else if (data.status === "DEVICE_MISMATCH") {
      showModal(
        "Your account is active on another device. Switch to this device?",
        async () => {
          await fetch("http://localhost:8080/api/auth/force-device-switch", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ loginId, deviceId })
          });
          localStorage.setItem("userLogin", loginId);
          window.location.href = "index.html";
        }
      );
    }
    else {
      showModal(data.message || "Login failed.", () => {});
    }

  } catch (err) {
    showLoader(false);
    showModal("Server error. Please try again.", () => {});
  }
});
