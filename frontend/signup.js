const signupForm = document.getElementById("signup-form");
const otpPopup = document.getElementById("otp-popup");
const verifyBtn = document.getElementById("verify-btn");

let signupEmail = "";

// SIGNUP
signupForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  const fullName = document.getElementById("fullname").value.trim();
  const email = document.getElementById("email").value.trim();
  const phone = document.getElementById("phone").value.trim();
  const password = document.getElementById("password").value.trim();

  if (phone.length !== 10 || isNaN(phone)) {
    alert("Phone number must be 10 digits");
    return;
  }

  if (password.length < 8) {
    alert("Password must be at least 8 characters");
    return;
  }

  const body = {
    fullName,
    email,
    phoneNo: phone,
    password
  };

  try {
    const res = await fetch("http://localhost:8080/api/auth/signup", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body)
    });

    const msg = await res.text();
    alert(msg);

    if (msg.includes("OTP sent")) {
      signupEmail = email;
      otpPopup.style.display = "flex";
    }
  } catch (error) {
    alert("Signup failed. Check backend.");
  }
});

// OTP VERIFY
verifyBtn.addEventListener("click", async () => {
  const otp = document.getElementById("otp-input").value.trim();

  if (otp.length !== 6) {
    alert("Enter valid 6-digit OTP");
    return;
  }

  const url = `http://localhost:8080/api/auth/verify-otp?email=${signupEmail}&otp=${otp}`;

  const res = await fetch(url, { method: "POST" });
  const msg = await res.text();
  alert(msg);

  if (msg.includes("successful")) {
    window.location.href = "login.html";
  }
});
