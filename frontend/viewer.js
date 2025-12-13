console.log("Viewer loaded");

const params = new URLSearchParams(window.location.search);
const courseId = params.get("courseId");
const noteId = params.get("noteId");

const container = document.getElementById("slidesContainer");

// Disable right click
document.addEventListener("contextmenu", e => e.preventDefault());

async function loadSlides() {
  try {
    const response = await fetch(
      `http://localhost:8080/api/courses/${courseId}/notes/${noteId}/slides`
    );

    const data = await response.json();
    const slides = data.slides;

    if (!slides || slides.length === 0) {
      container.innerHTML = "<p>No slides available</p>";
      return;
    }

    renderSlides(slides);
  } catch (err) {
    console.error("Failed to load slides", err);
    container.innerHTML = "<p>Error loading slides</p>";
  }
}

function renderSlides(slides) {
  container.innerHTML = "";

  slides.forEach((path, index) => {

    // ✅ FIX: remove /DOC from path
    const cleanPath = path.startsWith("/DOC")
      ? path.replace("/DOC", "")
      : path;

    const img = document.createElement("img");
    img.src = `http://localhost:8080${cleanPath}`;
    img.className = "slide-img";

    const count = document.createElement("div");
    count.className = "slide-count";
    count.innerText = `Slide ${index + 1} of ${slides.length}`;

    container.appendChild(img);
    container.appendChild(count);
  });
}

loadSlides();
