console.log("Viewer loaded");

const params = new URLSearchParams(window.location.search);
const noteId = params.get("noteId");
const loginId = localStorage.getItem("userLogin");

const container = document.getElementById("slidesContainer");
const counter = document.getElementById("slideCounter");

if (!loginId) {
  document.body.innerHTML = "<h2>Unauthorized</h2>";
  throw new Error("Not logged in");
}

// Client-side deterrents
document.addEventListener("contextmenu", e => e.preventDefault());
document.addEventListener("dragstart", e => e.preventDefault());
document.addEventListener("selectstart", e => e.preventDefault());

let slideElements = [];

async function loadSlides() {
  try {
    const res = await fetch(
      `http://localhost:8080/api/notes/${noteId}/slides?loginId=${encodeURIComponent(loginId)}`
    );

    if (!res.ok) throw new Error("Unauthorized");

    const indexes = await res.json();

    if (!indexes || indexes.length === 0) {
      container.innerHTML = "<p>No slides available</p>";
      return;
    }

    container.innerHTML = "";
    slideElements = [];

    // ✅ FIX 1: Set correct initial counter
    counter.innerText = `Slide 1 of ${indexes.length}`;

    indexes.forEach((idx, i) => {
      const img = document.createElement("img");

      img.src =
        `http://localhost:8080/api/notes/${noteId}/slides/${idx}` +
        `?loginId=${encodeURIComponent(loginId)}`;

      img.className = "slide-img";
      img.loading = "lazy";
      img.dataset.index = i + 1;

      container.appendChild(img);
      slideElements.push(img);
    });

    // ✅ FIX 2: Delay observer until layout stabilizes
    setTimeout(() => {
      setupScrollCounter(indexes.length);
    }, 150);

  } catch (err) {
    console.error(err);
    container.innerHTML =
      "<p>Access denied or subscription expired</p>";
  }
}

function setupScrollCounter(total) {
  const observer = new IntersectionObserver(
    entries => {
      // Pick the FIRST visible slide (top-most)
      const visible = entries
        .filter(e => e.isIntersecting)
        .sort(
          (a, b) =>
            a.target.getBoundingClientRect().top -
            b.target.getBoundingClientRect().top
        );

      if (visible.length > 0) {
        const current = visible[0].target.dataset.index;
        counter.innerText = `Slide ${current} of ${total}`;
      }
    },
    {
      threshold: 0.55
    }
  );

  slideElements.forEach(slide => observer.observe(slide));
}

loadSlides();
