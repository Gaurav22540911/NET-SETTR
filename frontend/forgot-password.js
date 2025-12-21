const loader = document.getElementById("loader");
const modal = document.getElementById("modal");
const modalText = document.getElementById("modalText");
const modalOk = document.getElementById("modalOk");

const stepLogin = document.getElementById("step-login");
const stepOtp = document.getElementById("step-otp");
const stepReset = document.getElementById("step-reset");

const sendOtpBtn = document.getElementById("sendOtpBtn");
const verifyOtpBtn = document.getElementById("verifyOtpBtn");
const resetPasswordBtn = document.getElementById("resetPasswordBtn");

let loginIdGlobal = "";

/* Helpers */
function showLoader(show) {
  loader.classList.toggle("hidden", !show);
}

function showModal(msg) {
  modalText.innerText = msg;
  modal.classList.remove("hidden");
  modalOk.onclick = () => modal.classList.add("hidden");
}

function setBtnLoading(btn, loading, text) {
  if (loading) {
    btn.dataset.text = btn.innerText;
    btn.innerText = text;
    btn.disabled = true;
  } else {
    btn.innerText = btn.dataset.text;
    btn.disabled = false;
  }
}

/* OTP auto focus */
document.querySelectorAll(".otp-inputs input").forEach((input, i, arr) => {
  input.addEventListener("input", () => {
    if (input.value && arr[i + 1]) arr[i + 1].focus();
  });
});

/* STEP 1 – Send OTP */
sendOtpBtn.onclick = async () => {
  const loginId = document.getElementById("loginId").value.trim();
  if (!loginId) return showModal("Enter email or phone");

  debugger;
  loginIdGlobal = loginId;
  showLoader(true);
  setBtnLoading(sendOtpBtn, true, "Sending OTP...");

  try {
    await fetch("http://localhost:8080/api/auth/forgot-password", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ loginId })
    });

    stepLogin.classList.add("hidden");
    stepOtp.classList.remove("hidden");
  } catch {
    showModal("Failed to send OTP");
  } finally {
    showLoader(false);
    setBtnLoading(sendOtpBtn, false);
  }
};

/* STEP 2 – Verify OTP */
verifyOtpBtn.onclick = async () => {
  const otp = [...document.querySelectorAll(".otp-inputs input")]
    .map(i => i.value)
    .join("");

  if (otp.length !== 6) return showModal("Enter valid OTP");

  showLoader(true);
  setBtnLoading(verifyOtpBtn, true, "Verifying...");

  try {
    const res = await fetch("http://localhost:8080/api/auth/verify-reset-otp", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ loginId: loginIdGlobal, otp })
    });

    if (!res.ok) return showModal("Invalid or expired OTP");

    stepOtp.classList.add("hidden");
    stepReset.classList.remove("hidden");
  } catch {
    showModal("OTP verification failed");
  } finally {
    showLoader(false);
    setBtnLoading(verifyOtpBtn, false);
  }
};

/* STEP 3 – Reset Password */
resetPasswordBtn.onclick = async () => {
  const p1 = document.getElementById("newPassword").value;
  const p2 = document.getElementById("confirmPassword").value;

  if (p1.length < 8) return showModal("Password must be 8+ characters");
  if (p1 !== p2) return showModal("Passwords do not match");

  showLoader(true);
  setBtnLoading(resetPasswordBtn, true, "Resetting...");

  try {
    const res = await fetch("http://localhost:8080/api/auth/reset-password", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ loginId: loginIdGlobal, newPassword: p1 })
    });

    if (!res.ok) return showModal("Reset failed");

    showModal("Password reset successful!");
    setTimeout(() => window.location.href = "login.html", 1500);
  } catch {
    showModal("Server error");
  } finally {
    showLoader(false);
    setBtnLoading(resetPasswordBtn, false);
  }
};
