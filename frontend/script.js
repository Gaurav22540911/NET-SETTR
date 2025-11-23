//-------------------------------------
// NAVBAR LOGIN / LOGOUT HANDLING
//-------------------------------------
function updateNavbar() {
  const menu = document.getElementById("navbarMenu");
  const user = localStorage.getItem("userLogin");

  if (user) {
    // USER LOGGED IN → Show Logout only
    menu.innerHTML = `
      <li><a href="#" id="logoutBtn">Logout</a></li>
    `;

    document.getElementById("logoutBtn").addEventListener("click", () => {
      localStorage.removeItem("userLogin");
      alert("Logged out successfully");
      location.reload();
    });

  } else {
    // USER NOT LOGGED IN → Show Sign Up + Login
    menu.innerHTML = `
      <li><a href="signup.html">Sign Up</a></li>
      <li><a href="login.html">Login</a></li>
    `;
  }
}

updateNavbar(); // Call first



//-------------------------------------
// MAIN CONTENT ELEMENTS
//-------------------------------------
const content = document.getElementById('content');
const logoClick = document.getElementById('logoClick');
const popup = document.getElementById('popup');
let hideTimeout = null;


//-------------------------------------
// FETCH & RENDER COURSES
//-------------------------------------
async function loadCourses() {
  try {
    const response = await fetch("http://localhost:8080/api/courses");
    const courses = await response.json();

    const coursesHTML = `
      <div class="courses">
        ${courses.map(course => `
          <div class="course"
               data-name="${course.course_name}"
               data-description="${course.course_description}"
               data-image="${course.image_url}"
               data-amount="${course.amount !== null ? course.amount : 0}">
            <img src="${course.image_url}" alt="${course.course_name}" />
            <h3>${course.course_name}</h3>
            <p>${course.course_description}</p>
            <span class="price">
              ₹${Number(course.amount || 0).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
            </span>
          </div>
        `).join("")}
      </div>
    `;

    content.innerHTML = coursesHTML;
    setupCourseHover();
    setupCourseClick();

  } catch (error) {
    console.error("Error loading courses:", error);
    content.innerHTML = `<p style="color:red; text-align:center;">Failed to load courses.</p>`;
  }
}



//-------------------------------------
// LOGO CLICK → RELOAD COURSES
//-------------------------------------
logoClick.addEventListener('click', () => loadCourses());



//-------------------------------------
// POPUP HOVER LOGIC
//-------------------------------------
function setupCourseHover() {
  document.querySelectorAll('.course').forEach(course => {

    course.addEventListener('mouseenter', () => {
      clearTimeout(hideTimeout);

      const rect = course.getBoundingClientRect();
      const name = course.dataset.name;
      const desc = course.dataset.description;

      popup.innerHTML = `<strong>${name}</strong><br><p>${desc}</p>`;

      const popupWidth = 300;
      const leftPos = rect.left + window.scrollX + (rect.width / 2) - (popupWidth / 2);

      popup.style.top = `${rect.bottom + window.scrollY + 10}px`;
      popup.style.left = `${leftPos}px`;
      popup.style.display = "block";

      setTimeout(() => popup.classList.add('show'), 10);
    });

    course.addEventListener('mouseleave', () => {
      hideTimeout = setTimeout(() => {
        popup.classList.remove('show');
        setTimeout(() => popup.style.display = "none", 200);
      }, 250);
    });

  });

  popup.addEventListener('mouseenter', () => clearTimeout(hideTimeout));

  popup.addEventListener('mouseleave', () => {
    hideTimeout = setTimeout(() => {
      popup.classList.remove('show');
      setTimeout(() => popup.style.display = "none", 200);
    }, 250);
  });
}



//-------------------------------------
// CLICK → OPEN COURSE PAGE
//-------------------------------------
function setupCourseClick() {
  document.querySelectorAll('.course').forEach(course => {

    course.addEventListener('click', () => {
      const name = encodeURIComponent(course.dataset.name);
      const desc = encodeURIComponent(course.dataset.description);
      const img = encodeURIComponent(course.dataset.image);
      const amount = encodeURIComponent(course.dataset.amount);

      window.location.href =
        `course.html?name=${name}&desc=${desc}&img=${img}&amount=${amount}`;
    });

  });
}



//-------------------------------------
// INITIAL LOAD
//-------------------------------------
loadCourses();
