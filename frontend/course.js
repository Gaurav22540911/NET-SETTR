//-------------------------------------
// NAVBAR LOGIN / LOGOUT HANDLING
//-------------------------------------
function updateNavbar() {
  const menu = document.getElementById("navbarMenu");
  const user = localStorage.getItem("userLogin");

  if (user) {
    menu.innerHTML = `
      <li><a href="index.html">Home</a></li>
      <li><a href="#" id="logoutBtn">Logout</a></li>
    `;

    document.getElementById("logoutBtn").addEventListener("click", () => {
      localStorage.removeItem("userLogin");
      alert("Logged out successfully");
      location.href = "index.html";
    });

  } else {
    menu.innerHTML = `
      <li><a href="index.html">Home</a></li>
      <li><a href="signup.html" class="signup-btn">Sign Up</a></li>
      <li><a href="login.html" class="login-btn">Login</a></li>
    `;
  }
}
updateNavbar();


//-------------------------------------
// LOGO CLICK → HOME PAGE
//-------------------------------------
document.getElementById("logoClick").addEventListener("click", () => {
  window.location.href = "index.html";
});


//-------------------------------------
// LOADER (SHOW / HIDE)
//-------------------------------------
function showLoader() {
  document.getElementById("loader").classList.remove("hidden");
}
function hideLoader() {
  document.getElementById("loader").classList.add("hidden");
}


//-------------------------------------
// LOAD COURSE DETAILS FROM URL PARAMS
//-------------------------------------
showLoader();

const params = new URLSearchParams(window.location.search);

const courseName = params.get("name");
const courseDesc = params.get("desc");
const courseImg = params.get("img");
const courseAmount = params.get("amount");

document.getElementById("course-title").textContent = courseName;
document.getElementById("course-desc").textContent = courseDesc;
document.getElementById("course-image").src = courseImg;

document.getElementById("price-box").innerHTML =
  `<div class="price">₹${Number(courseAmount).toLocaleString("en-IN")}</div>`;

hideLoader();


//-------------------------------------
// BUY NOW → RAZORPAY PAYMENT
//-------------------------------------
document.getElementById("buy-btn").addEventListener("click", async () => {
  showLoader();

  try {
    const response = await fetch("http://localhost:8080/api/payments/create-order", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        amount: Number(courseAmount),
        currency: "INR",
        receipt: `order_${Date.now()}`
      })
    });

    const order = await response.json();

    if (!order.orderId) {
      alert("Failed to create payment order.");
      hideLoader();
      return;
    }

    const options = {
      key: "rzp_test_RdAIlDO8yCtH5V",
      amount: order.amount,
      currency: "INR",
      order_id: order.orderId,

      name: "NET-SETTR",
      description: courseName,
      image: "logo.png",

      prefill: {
        name: localStorage.getItem("userLogin") || "Guest User"
      },

      handler: function () {
        alert("Payment Successful!");

        // Optional redirect
        window.location.href = "thankyou.html";
      },

      modal: {
        ondismiss: function () {
          console.log("Payment window closed.");
        }
      },

      theme: {
        color: "#1f2937"
      }
    };

    hideLoader();
    new Razorpay(options).open();

  } catch (error) {
    console.error("Payment Error:", error);
    hideLoader();
    alert("Something went wrong. Please try again.");
  }
});
