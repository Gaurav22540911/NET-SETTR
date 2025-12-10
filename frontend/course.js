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
      localStorage.removeItem("userId");
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

document.getElementById("logoClick").addEventListener("click", () => {
  window.location.href = "index.html";
});

//-------------------------------------
// LOADER
//-------------------------------------
function showLoader() { document.getElementById("loader").classList.remove("hidden"); }
function hideLoader() { document.getElementById("loader").classList.add("hidden"); }

//-------------------------------------
// LOAD COURSE DETAILS
//-------------------------------------
showLoader();
const params = new URLSearchParams(window.location.search);

const courseName = params.get("name");
const courseDesc = params.get("desc");
const courseImg = params.get("img");
const courseAmount = params.get("amount");
const courseId = params.get("courseId");

document.getElementById("course-title").textContent = courseName;
document.getElementById("course-desc").textContent = courseDesc;
document.getElementById("course-image").src = courseImg;
document.getElementById("price-box").innerHTML =
  `<div class="price">₹${Number(courseAmount).toLocaleString("en-IN")}</div>`;

hideLoader();

//-------------------------------------
// LOAD SLIDES
//-------------------------------------
async function loadSlides() {
  const container = document.getElementById("slides-container");
  const userLogin = localStorage.getItem("userLogin");
  let subscribed = false;

  debugger;
  try {
    if (userLogin) {
      const subCheck = await fetch(
        `http://localhost:8080/api/subscriptions/check?loginId=${userLogin}&courseId=${courseId}`
      );
      const subStatus = await subCheck.json();
      subscribed = subStatus.subscribed;
    }

    const slidesRes = await fetch(`http://localhost:8080/api/slides/${courseId}`);
    const slides = await slidesRes.json();

    if (!slides || slides.length === 0) {
      container.innerHTML = "<p>No slides found.</p>";
      return;
    }

    const totalSlides = slides.length;
    const firstSlidePath = slides[0].imagePath.replace("E:/NET-SETTR_PROJECT/DOC/", "");

    container.innerHTML = ""; // Clear

    if (!subscribed) {
      // LOCKED VIEW
      container.innerHTML = `
        <div class="locked-preview">
          <img src="http://localhost:8080/${firstSlidePath}" class="preview-slide" />
          <div class="locked-overlay">
            🔒 Locked — ${totalSlides} Slides<br>Click Buy to Unlock Full Content
          </div>
        </div>
      `;
    } else {
      // UNLOCKED VIEW - View course tile
      container.innerHTML = `
        <div class="locked-preview" id="viewCourseBtn">
          <img src="http://localhost:8080/${firstSlidePath}" class="preview-slide" />
          <div class="locked-overlay">
            ▶ View Course — ${totalSlides} Slides
          </div>
        </div>
      `;

      // Change Buy button to View button
      const buyBtn = document.getElementById("buy-btn");
      buyBtn.textContent = "View Course";
      buyBtn.onclick = () => window.open(`viewer.html?courseId=${courseId}`, "_blank");

      // Clicking preview also opens viewer
      document.getElementById("viewCourseBtn").addEventListener("click", () => {
        window.open(`viewer.html?courseId=${courseId}`, "_blank");
      });
    }

  } catch (err) {
    console.error("Slides load error", err);
    container.innerHTML = `<p style="color:red;">Failed to load course content</p>`;
  }
}


//-------------------------------------
// BUY NOW → Razorpay Integration
//-------------------------------------
document.getElementById("buy-btn").addEventListener("click", async () => {
  showLoader();

  try {
    const response = await fetch("http://localhost:8080/api/payments/create-order", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        amount: Number(courseAmount) * 100,
        currency: "INR",
        receipt: `order_${Date.now()}`
      })
    });

    const order = await response.json();
    hideLoader();

    if (!order.orderId) {
      alert("Failed to create payment order.");
      return;
    }

    const options = {
      key: "rzp_test_RdAIlDO8yCtH5V",
      amount: order.amount,
      currency: order.currency,
      order_id: order.orderId,
      name: "NET-SETTR",
      description: courseName,
      image: "logo.png",

      handler: async function (paymentResponse) {
        showLoader();
        const verifyRes = await fetch("http://localhost:8080/api/payments/verify", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            razorpay_order_id: paymentResponse.razorpay_order_id,
            razorpay_payment_id: paymentResponse.razorpay_payment_id,
            razorpay_signature: paymentResponse.razorpay_signature,
            user_id: localStorage.getItem("userId"),
            course_id: courseId,
            amount: Number(courseAmount)
          })
        });

        const result = await verifyRes.json();
        hideLoader();

        if (result.status === "success") {
          document.getElementById("paymentSuccessModal").classList.remove("hidden");
        } else {
          alert("Verification Failed!");
        }
      },

      theme: { color: "#1f2937" }
    };

    new Razorpay(options).open();

  } catch (error) {
    hideLoader();
    console.error("Payment error:", error);
    alert("Something went wrong.");
  }
});

loadSlides();
