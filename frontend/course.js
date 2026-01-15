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
      //alert("Logged out successfully");
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

// ================================
// NORMALIZE COURSE AMOUNT (₹)
// ================================
const rawAmount = Number(courseAmount);

// if amount accidentally comes in paise or extra zeros
const normalizedAmount = rawAmount > 10000 ? rawAmount / 100 : rawAmount;

console.log("Normalized Amount (₹):", normalizedAmount);


document.getElementById("course-title").textContent = courseName;
document.getElementById("course-desc").textContent = courseDesc;
document.getElementById("course-image").src = courseImg;
document.getElementById("price-box").innerHTML =
  `<div class="price">₹${normalizedAmount.toLocaleString("en-IN")}</div>`;

hideLoader();

//-------------------------------------
// LOAD SLIDES
//-------------------------------------
function disableBuyButton() {
  const buyBtn = document.getElementById("buy-btn");
  if (!buyBtn) return;

  buyBtn.disabled = true;
  buyBtn.textContent = "Already Purchased";
  buyBtn.style.cursor = "not-allowed";
  buyBtn.style.opacity = "0.6";
}


async function loadCourseContent() {
  const container = document.getElementById("slides-container");
  const infoBox = document.getElementById("subscription-info");
  const buyBtn = document.getElementById("buy-btn");
  container.innerHTML = "";

  const userLogin = localStorage.getItem("userLogin");
if (!userLogin) {
    infoBox.textContent = "⏳ Access valid for 6 months from date of purchase";
    infoBox.className = "subscription-info inactive";

    buyBtn.disabled = false;
    buyBtn.textContent = "Buy Now";

    // Notes remain locked
    //await renderNotes(false);
    //return; // ⛔ STOP execution here
  }


  let subscribed = false;
let hasAccess = false;

  debugger;
  try {
    // 1️⃣ Check subscription
    if (userLogin) {
      // const subRes = await fetch(
      //   `http://localhost:8080/api/subscriptions/check?loginId=${userLogin}&courseId=${courseId}`
      // );
      // const subJson = await subRes.json();
      // subscribed = subJson.subscribed;


const infoBox = document.getElementById("subscription-info");

if (userLogin) {
  const subRes = await fetch(
    `http://localhost:8080/api/subscriptions/details?loginId=${userLogin}&courseId=${courseId}`
  );

  const subJson = await subRes.json();

  subscribed = subJson.subscribed;

  const expired = subJson.expired;
  hasAccess = subscribed && !expired;


  // 🟢 SUBSCRIBED
  if (hasAccess) {
    disableBuyButton();

    const endDate = new Date(subJson.endDate).toLocaleDateString("en-IN", {
      day: "2-digit",
      month: "short",
      year: "numeric"
    });

    infoBox.textContent = `✅ Access active until ${endDate}`;
    infoBox.classList.add("active");
  }

  // 🔴 EXPIRED (future-ready)
  if (subscribed && subJson.expired) {
    infoBox.textContent = "⚠️ Your access has expired. Renew to continue.";
    infoBox.classList.add("inactive");
  }

} 
// 🟡 LOGGED IN BUT NOT SUBSCRIBED
if (!subscribed) {
  infoBox.textContent = "⏳ Access valid for 6 months from date of purchase";
  infoBox.className = "subscription-info inactive";

  buyBtn.disabled = false;
  buyBtn.textContent = "Buy Now";
}



    }

    // ✅ Disable Buy button if already subscribed
if (!hasAccess) {
  buyBtn.disabled = false;
  buyBtn.textContent = subscribed ? "Renew Access" : "Buy Now";
}


    // 2️⃣ Fetch notes
    console.log("CourseId from URL1:", courseId);
    const notesRes = await fetch(
      `http://localhost:8080/api/courses/${courseId}/notes`
    );
    const notes = await notesRes.json();

    if (!notes || notes.length === 0) {
      container.innerHTML = "<p>No notes available.</p>";
      return;
    }

    // 3️⃣ Render notes
    for (const note of notes) {
  const locked = !hasAccess;

  const imgPath = note.previewImage.startsWith("/DOC")
    ? note.previewImage.replace("/DOC", "")
    : note.previewImage;

  container.innerHTML += `
  <div class="note-row">
    <div class="note-left ${locked ? "locked" : "unlocked"}"
         data-note-id="${note.noteId}">
      <img src="http://localhost:8080${imgPath}" />
      <div class="note-overlay">
        ${locked ? "🔒 Locked" : "▶ View"}
      </div>
    </div>

    <div class="note-right">
      <h3>${note.title}</h3>
      <p>${note.description}</p>
    </div>
  </div>
`;

}


    // 4️⃣ Button handling
      document.querySelectorAll(".note-left").forEach(noteEl => {
    noteEl.addEventListener("click", () => {
      if (!hasAccess) {
  document.getElementById("buy-btn")
    .scrollIntoView({ behavior: "smooth" });
  return;
}


      const noteId = noteEl.dataset.noteId;

      window.open(
        `viewer.html?courseId=${courseId}&noteId=${noteId}`,
        "_blank"
      );
    });
  });


  } catch (err) {
    console.error(err);
    container.innerHTML =
      "<p style='color:red'>Failed to load course content</p>";
  }
}



//-------------------------------------
// BUY NOW → Razorpay Integration
//-------------------------------------
document.getElementById("buy-btn").addEventListener("click", async () => {
  if (document.getElementById("buy-btn").disabled) {
    return; // ⛔ prevent double payment
  }

  // 🔒 FORCE LOGIN BEFORE PAYMENT
  const userLogin = localStorage.getItem("userLogin");

  if (!userLogin) {
    window.location.href =
      `signup.html?redirect=course.html${encodeURIComponent(window.location.search)}`;
    return;
  }

  showLoader();


  console.log("Course Amount (₹):", normalizedAmount);
console.log("Amount sent to Razorpay (paise):", normalizedAmount * 100);

  try {
    const response = await fetch("http://localhost:8080/api/payments/create-order", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
  amount: normalizedAmount,
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
debugger;
    const options = {
      key: "rzp_test_RdAIlDO8yCtH5V",
      amount: order.amount,
      currency: order.currency,
      order_id: order.orderId,
      name: "NET-SETTR",
      description: courseName,
     // image: "logo.png",
      prefill: {
  contact: localStorage.getItem("userLogin") || ""
},


      handler: async function (paymentResponse) {
        showLoader();
        const verifyRes = await fetch("http://localhost:8080/api/payments/verify", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            razorpay_order_id: paymentResponse.razorpay_order_id,
            razorpay_payment_id: paymentResponse.razorpay_payment_id,
            razorpay_signature: paymentResponse.razorpay_signature,
            //user_id: localStorage.getItem("userId"),
            loginId: localStorage.getItem("userLogin"),
            course_id: courseId,
            amount: normalizedAmount
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

document.addEventListener("DOMContentLoaded", () => {
  loadCourseContent();
});

