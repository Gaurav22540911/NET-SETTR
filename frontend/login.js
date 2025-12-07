// login.js
const loginForm = document.getElementById("login-form");

loginForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  const loginId = document.getElementById("loginId").value.trim();
  const password = document.getElementById("password").value;

  if (!loginId) return alert("Please enter email or phone.");
  const isPhone = /^[0-9]+$/.test(loginId);
  if (isPhone && loginId.length !== 10) return alert("Phone must be 10 digits.");
  if (!password || password.length < 8) return alert("Password must be at least 8 characters.");

  // DEVICE ID LOGIC
  let deviceId = localStorage.getItem("device_id");
  if (!deviceId) {
    deviceId = crypto.randomUUID();
    localStorage.setItem("device_id", deviceId);
  }

  const body = { loginId, password, deviceId };

  try {
    const res = await fetch("http://localhost:8080/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });

    const data = await res.json();

    if (data.status === "LOGIN_SUCCESS") {
      localStorage.setItem("userLogin", loginId);
      alert("Login successful!");
      window.location.href = "index.html";
    }
    else if (data.status === "DEVICE_MISMATCH") {
      // show modal later; temporary alert
      const confirmSwitch = confirm(
        "⚠ Your account is active on another device.\nDo you want to switch to this device?"
      );

      if (confirmSwitch) {
        await fetch("http://localhost:8080/api/auth/force-device-switch", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ loginId, deviceId }),
        });

        localStorage.setItem("userLogin", loginId);
        alert("Switched to this device.");
        window.location.href = "index.html";
      }
    }
    else {
      alert(data.message || "Login failed.");
    }

  } catch (err) {
    console.error("Login error", err);
    alert("Server error, try again");
  }
});
