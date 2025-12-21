const signupForm = document.getElementById("signup-form");
const otpPopup = document.getElementById("otp-popup");
const verifyBtn = document.getElementById("verify-btn");
const loadingOverlay = document.getElementById("loadingOverlay");
const toast = document.getElementById("toast");

const strengthFill = document.getElementById("strengthFill");
const strengthText = document.getElementById("strengthText");

let signupEmail = "";

/* UTIL */
function showToast(msg) {
  toast.innerText = msg;
  toast.style.display = "block";
  setTimeout(() => toast.style.display = "none", 3000);
}

function showLoader(text) {
  document.getElementById("loadingText").innerText = text;
  loadingOverlay.style.display = "flex";
}

function hideLoader() {
  loadingOverlay.style.display = "none";
}

/* PASSWORD STRENGTH */
password.addEventListener("input", () => {
  const val = password.value;
  let score = 0;

  if (val.length >= 8) score++;
  if (/[A-Z]/.test(val)) score++;
  if (/[0-9]/.test(val)) score++;
  if (/[^A-Za-z0-9]/.test(val)) score++;

  const widths = ["0%", "25%", "50%", "75%", "100%"];
  const colors = ["red", "orange", "#facc15", "#22c55e"];

  strengthFill.style.width = widths[score];
  strengthFill.style.background = colors[score - 1] || "#e5e7eb";

  strengthText.innerText =
    score <= 1 ? "Weak" :
    score === 2 ? "Medium" :
    score === 3 ? "Good" : "Strong";
});

/* SIGNUP */
signupForm.addEventListener("submit", async e => {
  e.preventDefault();

  const phoneValue = document.getElementById("phone").value.trim();
  const passwordVal = document.getElementById("password").value.trim();

  if (phoneValue.length !== 10 || isNaN(phoneValue)) {
    showToast("Invalid phone number");
    return;
  }

  if (passwordVal.length < 8) {
    showToast("Password too short");
    return;
  }

  showLoader("Sending OTP...");

  const res = await fetch("http://localhost:8080/api/auth/signup", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      fullName: document.getElementById("fullname").value,
      email: document.getElementById("email").value,
      phoneNo: phoneValue,
      password: passwordVal
    })
  });

  const msg = await res.text();
  hideLoader();
  showToast(msg);

  if (msg.includes("OTP sent")) {
    signupEmail = document.getElementById("email").value;
    otpPopup.style.display = "flex";
  }
});

/* OTP AUTO MOVE */
document.querySelectorAll(".otp-inputs input").forEach((input, i, arr) => {
  input.addEventListener("input", () => {
    if (input.value && arr[i+1]) arr[i+1].focus();
  });
});

/* VERIFY OTP */
verifyBtn.addEventListener("click", async () => {
  const otp = [...document.querySelectorAll(".otp-inputs input")]
    .map(i => i.value)
    .join("");

  if (otp.length !== 6) return showToast("Invalid OTP");

  showLoader("Verifying OTP...");

  const res = await fetch(
    `http://localhost:8080/api/auth/verify-otp?email=${signupEmail}&otp=${otp}`,
    { method: "POST" }
  );

  const msg = await res.text();
  hideLoader();
  showToast(msg);

  if (msg.includes("successful")) {
    window.location.href = "login.html";
  }
});
