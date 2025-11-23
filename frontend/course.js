// Parse URL parameters
const params = new URLSearchParams(window.location.search);
const name = params.get('name');
const desc = params.get('desc');
const img = params.get('img');
const amount = params.get('amount');

// Fill in course info
document.getElementById('course-title').textContent = name || "Course Title";
document.getElementById('course-desc').textContent = desc || "Course Description";
document.getElementById('course-image').src = img || "placeholder.png";

// 🧾 Format price
let formattedAmount = "Price not available";
if (amount && !isNaN(amount)) {
  formattedAmount = `₹${Number(amount).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;
}

// ✅ Create and insert price element (below image, above button)
const priceElement = document.createElement('div');
priceElement.classList.add('price');
priceElement.textContent = formattedAmount;

const rightSection = document.querySelector('.right-section');
const buyButton = document.getElementById('buy-btn');
rightSection.insertBefore(priceElement, buyButton);

// ✅ Keep button label clean
buyButton.textContent = "Buy Now";

// 🧾 Handle Buy Now click
buyButton.addEventListener('click', async () => {
  try {
    const amountInRupees = Number(amount);
    if (isNaN(amountInRupees)) {
      alert("Invalid amount for this course.");
      return;
    }

    // Step 1: Create Razorpay order from backend
    const response = await fetch("http://localhost:8080/api/payments/create-order", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        amount: amountInRupees,
        currency: "INR",
        receipt: `course_${name}_${Date.now()}`
      })
    });

    const orderData = await response.json();
    if (!orderData.orderId) {
      alert("Failed to create Razorpay order. Please try again.");
      return;
    }

    // Step 2: Configure Razorpay Checkout
    const options = {
      key: "rzp_test_RdAIlDO8yCtH5V", // your Razorpay test key
      amount: orderData.amount, // in paise
      currency: orderData.currency,
      name: "NET-SETTR",
      description: `Purchase - ${name}`,
      image: "logo.png",
      order_id: orderData.orderId,
      prefill: {
        name: "Test User", // dynamically replace with actual user
        contact: "9876543210"
      },
      theme: { color: "#20232a" },

      // Success Handler
      handler: async function (response) {
        alert("✅ Payment Successful!");
        console.log(response);

        // Step 3: Verify payment and save to DB
        await fetch("http://localhost:8080/api/payments/verify", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            razorpay_order_id: response.razorpay_order_id,
            razorpay_payment_id: response.razorpay_payment_id,
            razorpay_signature: response.razorpay_signature,
            phone_no: "9876543210", // replace with logged-in user phone
            course_id: "1", // replace dynamically if you have real ID
            amount: amountInRupees
          })
        });

        // Optional: Redirect to thank-you page
        window.location.href = "thankyou.html";
      },

      modal: {
        ondismiss: function () {
          console.log("Payment cancelled by user.");
        }
      }
    };

    // Step 4: Open Razorpay checkout
    const rzp = new Razorpay(options);
    rzp.open();

  } catch (error) {
    console.error("Error in Buy Now:", error);
    alert("Something went wrong. Please try again.");
  }
});
