function snackbar_alert(message) {
    let snackbar = document.getElementById("snackbar");
    snackbar.getElementsByTagName("p")[0].textContent = message;
    snackbar.style.display = "grid";
    setTimeout(() => { snackbar.style.display = "none" }, 2000);
}

const canvas_size = 200;

let pixels = []
let selectedPixel = null;
let prevColor = "white";

function get_canvas() {
    return fetch("/canvas/get_pixels").then(response => response.json()).then(json => set_pixels(json));
}

function get_pixels_count() {
    return fetch("/canvas/get_pixels_count").then(response => response.json()).then((json) => {
        const pixels_count = document.getElementById("pixels-count");
        pixels_count.innerText = json["count"];
    });
}

function get_top_users() {
    return fetch("/canvas/get_top_users", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ count: 3 }) }).then(response => response.json()).then((json) => {
        const top_users = document.getElementsByClassName("top-users")[0]
        let topUsersHTML = "";
        for (let i in json) {
            let user = json[i];
            topUsersHTML += `
                <div class="user" title="${"user_info" in user ? user["user_info"] : ""}">
                    <img src="${user["picture"]}" alt="">
                    <div class="info">
                        <p class="username">${user["name"]}</p>
                        <div class="footer">
                            <p>Pixels count: ${user["pixel_count"]}</p>
                        </div>
                    </div>
                </div>
            `;
        }
        document.getElementById("top-users").innerHTML = topUsersHTML;
        if (topUsersHTML == "") {
            top_users.style.display = "none";
        } else {
            top_users.style.display = "flex";
        }
    });
}

function get_announcement() {
    return fetch("/canvas/get_announcement").then(response => response.json()).then((json) => {
        document.getElementById("announcement").innerText = json["announcement"];
    });
}

function announce(announcement) {
    return fetch("/canvas/set_announcement", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ "announcement": announcement }) }).then((response) => {
        if (response.status == 200) {
            setTimeout(snackbar_alert, 0, "Done");
        } else if (response.status = 403) {
            setTimeout(snackbar_alert, 0, "You are not admin!");
        } else {
            setTimeout(snackbar_alert, 0, "Error!");
        }
    });
}

function call_handlers() {
    setTimeout(get_canvas, 0);
    setTimeout(get_pixels_count, 0);
    setTimeout(get_top_users, 0);
    setTimeout(get_announcement, 0);
}

function set_pixels(json) {
    for (let i in json) {
        let pixel_json = json[i];
        if (pixel_json["x"] < canvas_size && pixel_json["y"] < canvas_size) {
            let pixel = pixels[pixel_json["x"]][pixel_json["y"]];
            if (!pixel.classList.contains("pixel-active")) {
                pixel.style.backgroundColor = pixel_json["color"];
                if ("author" in pixel_json) {
                    pixel.setAttribute("title", pixel_json["author"]);
                }
            }
        }
    }
}

function send_pixel(x, y, color) {
    return fetch("/canvas/set_pixel", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ "x": x, "y": y, "color": color }) }).then((response) => {
        if (response.status == 200) {
            selectedPixel = null;
            setTimeout(snackbar_alert, 0, "Done");
            response.json().then((json) => {
                countdown(parseInt(json["timeout"]));
            });
        } else {
            if (selectedPixel != null) {
                selectedPixel.style.backgroundColor = prevColor;
                selectedPixel = null;
            }
            if (response.status == 230) {
                setTimeout(snackbar_alert, 0, `Wait ${response.json()["timeout"]} seconds`);
            } else if (response.status = 403) {
                setTimeout(snackbar_alert, 0, "You are banned!");
            } else {
                setTimeout(snackbar_alert, 0, "Error!");
            }
        }
    });
}

let timeout = false;

async function countdown(seconds) {
    timeout = true;
    const placeButton = document.getElementById("place-button");
    placeButton.innerHTML = `${String(Math.floor(seconds / 60)).padStart(2, "0")}:${String(seconds % 60).padStart(2, "0")}`;
    seconds--;
    const interval = setInterval(() => {
        placeButton.innerHTML = `${String(Math.floor(seconds / 60)).padStart(2, "0")}:${String(seconds % 60).padStart(2, "0")}`;
        seconds--;
        if (seconds < 0) {
            timeout = false;
            placeButton.innerHTML = "Place a tile";
            clearInterval(interval);
        }
    }, 1000);
}

window.addEventListener("load", () => {
    const container = document.getElementById("container");
    const canvas = document.getElementById("canvas");
    const placeButton = document.getElementById("place-button");
    const palette = document.getElementById("palette");
    const colorsDiv = document.getElementById("colors");
    const colors = [...document.getElementsByClassName("color")];
    const rejectButton = document.getElementById("reject-button");
    const acceptButton = document.getElementById("accept-button");
    const logout = document.getElementById("logout");

    let canvas_scale = 1;
    let paint_mode = false;

    let startX = 0;
    let startY = 0;
    let mouseDownX = 0;
    let mouseDownY = 0;

    function initCanvas() {
        for (let i = 0; i < canvas_size; i++) {
            const row = document.createElement("div");
            let list_row = []
            row.style.display = "flex";
            for (let j = 0; j < canvas_size; j++) {
                const pixel = document.createElement("div");
                pixel.style.display = "inline-block";
                pixel.classList.add("pixel");
                pixel.setAttribute("x", i);
                pixel.setAttribute("y", j);
                pixel.addEventListener("mouseup", (event) => {
                    if (paint_mode && Math.abs(event.clientX - mouseDownX) < 5 && Math.abs(event.clientY - mouseDownY) < 5) {
                        if (selectedPixel != null) {
                            selectedPixel.classList.toggle("pixel-active");
                            selectedPixel.style.backgroundColor = prevColor;
                        }
                        selectedPixel = event.target;
                        prevColor = event.target.style.backgroundColor;
                        selectedPixel.classList.toggle("pixel-active");
                        event.target.style.backgroundColor = colorsDiv.getElementsByClassName("color-active")[0].style.backgroundColor;
                    }
                    startX = 0;
                    startY = 0;
                });
                list_row.push(pixel);
                row.appendChild(pixel);
            }
            pixels.push(list_row);
            canvas.appendChild(row);
        }
    }

    initCanvas();
    call_handlers();
    setInterval(call_handlers, 1000);

    container.addEventListener("wheel", (event) => {
        const delta_scale = 0.05;
        if (event.deltaY != 0) {
            canvas_scale -= delta_scale * event.deltaY / Math.abs(event.deltaY);
            canvas_scale = Math.min(Math.max(250 * 0.8 / Math.max(window.innerWidth, window.innerHeight), canvas_scale), 5);
            canvas.style.transformOrigin = `${event.clientX}px ${event.clientY}px`
            canvas.style.scale = canvas_scale;
        }
    });

    document.getElementById("inc-scale").addEventListener("click", () => {
        const delta_scale = 0.2;
        canvas_scale += delta_scale;
        canvas_scale = Math.min(Math.max(250 * 0.8 / Math.max(window.innerWidth, window.innerHeight), canvas_scale), 5);
        canvas.style.scale = canvas_scale;
    });

    document.getElementById("dec-scale").addEventListener("click", () => {
        const delta_scale = 0.2;
        canvas_scale -= delta_scale;
        canvas_scale = Math.min(Math.max(250 * 0.8 / Math.max(window.innerWidth, window.innerHeight), canvas_scale), 5);
        canvas.style.scale = canvas_scale;
    });

    container.addEventListener("mousedown", (event) => {
        startX = event.clientX;
        startY = event.clientY;
        mouseDownX = startX;
        mouseDownY = startY;
        container.style.cursor = "grabbing";
        container.addEventListener("mousemove", mouseMove);
        container.addEventListener("mouseup", mouseTouchUp);
    });

    container.addEventListener("touchstart", (event) => {
        for (let i = 0; i < event.changedTouches.length; i++) {
            startX = event.changedTouches[i].clientX;
            startY = event.changedTouches[i].clientY;
            mouseDownX = startX;
            mouseDownY = startY;
            container.style.cursor = "grabbing";
        }
        container.addEventListener("touchmove", touchMove);
        container.addEventListener("touchend", mouseTouchUp);
    });

    function mouseMove(event) {
        container.style.cursor = "grabbing";
        let newX = startX - event.clientX;
        let newY = startY - event.clientY;
        startX = event.clientX;
        startY = event.clientY;
        canvas.style.left = `${canvas.offsetLeft - newX}px`;
        canvas.style.top = `${canvas.offsetTop - newY}px`;
    }

    function touchMove(event) {
        container.style.cursor = "grabbing";
        for (let i = 0; i < event.changedTouches.length; i++) {
            let newX = startX - event.changedTouches[i].clientX;
            let newY = startY - event.changedTouches[i].clientY;
            startX = event.changedTouches[i].clientX;
            startY = event.changedTouches[i].clientY;
            canvas.style.left = `${canvas.offsetLeft - newX}px`;
            canvas.style.top = `${canvas.offsetTop - newY}px`;
        }
    }

    function mouseTouchUp() {
        first_two_touch = true;
        container.style.cursor = "auto";
        container.removeEventListener("mousemove", mouseMove);
        container.removeEventListener("touchmove", touchMove);
    }

    placeButton.addEventListener("click", () => {
        if (!timeout) {
            paint_mode = true;
            palette.style.display = "block";
        }
    });

    rejectButton.addEventListener("click", () => {
        paint_mode = false;
        palette.style.display = "none";
        if (selectedPixel != null) {
            selectedPixel.classList.toggle("pixel-active");
            selectedPixel.style.backgroundColor = prevColor;
        };
    });

    acceptButton.addEventListener("click", () => {
        paint_mode = false;
        palette.style.display = "none";
        if (selectedPixel != null) {
            selectedPixel.classList.toggle("pixel-active");
            send_pixel(selectedPixel.getAttribute("x"), selectedPixel.getAttribute("y"), selectedPixel.style.backgroundColor);
        }
    });

    colors.forEach(color => color.addEventListener("click", (event) => {
        colors.forEach(color => color.classList.remove("color-active"));
        event.target.classList.add("color-active");
        if (selectedPixel != null) {
            selectedPixel.style.backgroundColor = event.target.style.backgroundColor;
        }
    }));

    if (logout != null) {
        logout.addEventListener("click", () => {
            window.open("/logout", "_self");
        });
    }

    if (colors != undefined) {
        colors[0].classList.toggle("color-active");
    }
});