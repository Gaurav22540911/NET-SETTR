//-------------------------------------
// NAVBAR LOGIN / LOGOUT HANDLING
//-------------------------------------
function updateNavbar() {
  const menu = document.getElementById("navbarMenu");
  const user = localStorage.getItem("userLogin");

  menu.innerHTML = `
    <li class="dropdown">
      <a href="#" class="menu-link">Courses ▼</a>
      <ul class="dropdown-menu" id="courseDropdown"></ul>
    </li>

    <li class="dropdown">
      <a href="#" class="menu-link">Syllabus ▼</a>
      <ul class="dropdown-menu" id="syllabusDropdown"></ul>
    </li>
    ${
      user
        ? `<li><a href="#" id="logoutBtn">Logout</a></li>`
        : `<li><a href="signup.html" class="signup-btn">Sign Up</a></li>
           <li><a href="login.html" class="login-btn">Login</a></li>`
    }
  `;

  if (user) {
    document.getElementById("logoutBtn").addEventListener("click", () => {
      localStorage.removeItem("userLogin");
      //alert("Logged out successfully");
      window.location.href = "index.html";
    });
  }

  loadCourseTypes();
  loadSyllabus();
}
updateNavbar();


//-------------------------------------
// MAIN CONTENT ELEMENTS
//-------------------------------------
const content = document.getElementById("content");
const logoClick = document.getElementById("logoClick");
const popup = document.getElementById("popup");
let hideTimeout = null;


//-------------------------------------
// FUN LOADING SPINNER
//-------------------------------------
function showLoader() {
  const loader = document.getElementById("loader");
  if (loader) loader.classList.remove("hidden");
}

function hideLoader() {
  const loader = document.getElementById("loader");
  if (loader) loader.classList.add("hidden");
}


//-------------------------------------
// FETCH & RENDER COURSES (with filtering by type)
//-------------------------------------
async function loadCourses() {
  showLoader();

  const params = new URLSearchParams(window.location.search);
  const type = params.get("type");

  let url = "http://localhost:8080/api/courses";
  if (type) {
    url += `?type=${type}`;
  }

  try {
    const response = await fetch(url);
    const courses = await response.json();

    const coursesHTML = `
      <div class="courses">
        ${courses
          .map(
            course => `
          <div class="course"
     data-id="${course.course_id}"
     data-name="${course.courseName}"
     data-description="${course.course_description}"
     data-image="${course.image_url}"
     data-amount="${course.amount}">

  <div class="course-img">
    <img src="${course.image_url}" alt="${course.courseName}" />
  </div>

  <div class="course-info">
    <h3>${course.courseName}</h3>
    <p>${course.course_description}</p>

    <div class="course-footer">
      <span class="price">₹${Number(course.amount).toLocaleString("en-IN", {
        minimumFractionDigits: 2
      })}</span>

      <button class="view-course-btn">View Course</button>
    </div>
  </div>
</div>
`
          )
          .join("")}
      </div>
    `;

    content.innerHTML = coursesHTML;

    setupCourseHover();
    setupCourseClick();

  } catch (error) {
    console.error("Error loading courses:", error);
    content.innerHTML = `<p style="color:red; text-align:center;">Failed to load courses.</p>`;
  }

  hideLoader();
}


//-------------------------------------
// LOGO CLICK → Home load
//-------------------------------------
if (logoClick) {
  logoClick.addEventListener("click", () => {
    window.location = "index.html";
  });
}


//-------------------------------------
// POPUP HOVER LOGIC
//-------------------------------------
function setupCourseHover() {
  document.querySelectorAll(".course").forEach(course => {
    course.addEventListener("mouseenter", () => {
      clearTimeout(hideTimeout);

      const rect = course.getBoundingClientRect();
      popup.innerHTML = `
        <strong>${course.dataset.name}</strong>
        <p>${course.dataset.description}</p>
      `;

      popup.style.top = `${rect.bottom + window.scrollY + 10}px`;
      popup.style.left = `${rect.left + window.scrollX + rect.width / 2 - 150}px`;
      popup.style.display = "block";

      setTimeout(() => popup.classList.add("show"), 10);
    });

    course.addEventListener("mouseleave", () => {
      hideTimeout = setTimeout(() => {
        popup.classList.remove("show");
        setTimeout(() => (popup.style.display = "none"), 200);
      }, 250);
    });
  });
}


//-------------------------------------
// CLICK → OPEN COURSE PAGE
//-------------------------------------
function setupCourseClick() {
  debugger;
  document.querySelectorAll(".course").forEach(course => {
    course.addEventListener("click", () => {
      const name = encodeURIComponent(course.dataset.name);
      const desc = encodeURIComponent(course.dataset.description);
      const img = encodeURIComponent(course.dataset.image);
      const amount = encodeURIComponent(course.dataset.amount);
      const id = encodeURIComponent(course.dataset.id);
      console.log("course_id",id);

      window.location.href = `course.html?courseId=${id}&name=${name}&desc=${desc}&img=${img}&amount=${amount}`;

    });
  });
}

// View Course button & image click (safe, same navigation)
document.addEventListener("click", (e) => {
  const btn = e.target.closest(".view-course-btn");
  const img = e.target.closest(".course-img img");

  if (btn || img) {
    e.stopPropagation();
    const course = (btn || img).closest(".course");
    course.click(); // reuse existing logic
  }
});


//-------------------------------------
// LOAD COURSE TYPES FOR DROPDOWN
//-------------------------------------
async function loadCourseTypes() {
  try {
    const response = await fetch("http://localhost:8080/api/courses/types");
    const types = await response.json();

    const dropdown = document.getElementById("courseDropdown");
    dropdown.innerHTML = types
      .map(type => `<li><a href="index.html?type=${encodeURIComponent(type)}">${type}</a></li>`)
      .join("");

  } catch (error) {
    console.error("Error loading course types:", error);
  }
}

//-------------------------------------
// LOAD SYLLABUS FOR DROPDOWN
//-------------------------------------
async function loadSyllabus() {
  try {
    const response = await fetch("http://localhost:8080/api/syllabus");
    const syllabusList = await response.json();

    const dropdown = document.getElementById("syllabusDropdown");

    dropdown.innerHTML = syllabusList
      .map(s => `
        <li>
          <a href="http://localhost:8080${s.fileUrl}" target="_blank">
            ${s.name}
          </a>
        </li>
      `)
      .join("");

  } catch (error) {
    console.error("Error loading syllabus:", error);
  }
}



//-------------------------------------
// INITIAL LOAD
//-------------------------------------
window.onload = loadCourses;
