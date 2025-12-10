console.log("Viewer page loaded");

const params = new URLSearchParams(window.location.search);
const courseId = params.get("courseId");
let slides = [];
let currentIndex = 0;

async function loadSlides() {
  try {
    const response = await fetch(`http://localhost:8080/api/slides/${courseId}`);
    slides = await response.json();

    if (!slides || slides.length === 0) {
      alert("No slides available!");
      return;
    }

    showSlide(0);
  } catch (err) {
    console.error("Failed to load slides", err);
    alert("Unable to load slides");
  }
}

function showSlide(index) {
  if (!slides[index]) return;

  currentIndex = index;
  const imgPath = slides[index].imagePath.replace("E:/NET-SETTR_PROJECT/DOC/", "");
  document.getElementById("slideImage").src = `http://localhost:8080/${imgPath}`;
  document.getElementById("slideCount").innerText = `Slide ${index + 1} of ${slides.length}`;
}

// navigation buttons
document.getElementById("nextBtn").addEventListener("click", () => {
  if (currentIndex < slides.length - 1) {
    showSlide(currentIndex + 1);
  }
});

document.getElementById("prevBtn").addEventListener("click", () => {
  if (currentIndex > 0) {
    showSlide(currentIndex - 1);
  }
});

// keyboard navigation support
document.addEventListener("keydown", (event) => {
  if (event.key === "ArrowRight") document.getElementById("nextBtn").click();
  if (event.key === "ArrowLeft") document.getElementById("prevBtn").click();
});

loadSlides();
