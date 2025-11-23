// login.js
const loginForm = document.getElementById('login-form');

loginForm.addEventListener('submit', async (e) => {
  e.preventDefault();

  const loginId = document.getElementById('loginId').value.trim();
  const password = document.getElementById('password').value;

  // Basic client-side validation
  if (!loginId) {
    alert('Please enter email or phone.');
    return;
  }

  // if loginId looks numeric, check length 10 (phone)
  const isPhone = /^[0-9]+$/.test(loginId);
  if (isPhone && loginId.length !== 10) {
    alert('Phone number must be 10 digits (without country code).');
    return;
  }

  if (!password || password.length < 8) {
    alert('Password must be at least 8 characters.');
    return;
  }

  const body = { loginId, password };

  try {
    const res = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });

    // If your backend returns JSON, parse it. Current backend returns plain text in prior setup.
    const text = await res.text();

    if (res.ok && text.toLowerCase().includes('successful')) {
      // save loginId in localStorage (used by navbar)
      localStorage.setItem('userLogin', loginId);

      // optionally store a nicer display name if backend returns it later
      // localStorage.setItem('userName', 'Gaurav');

      alert('Login successful!');
      // redirect back to home (index.html)
      window.location.href = 'index.html';
    } else {
      // show server message (invalid password, not verified, etc.)
      alert(text || 'Login failed.');
    }
  } catch (err) {
    console.error('Login error', err);
    alert('Login failed — could not reach server. Make sure backend is running.');
  }
});
