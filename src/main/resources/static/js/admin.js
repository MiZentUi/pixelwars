function snackbar_alert(message) {
    let snackbar = document.getElementById("snackbar");
    snackbar.getElementsByTagName("p")[0].textContent = message;
    snackbar.style.display = "grid";
    setTimeout(() => { snackbar.style.display = "none" }, 2000);
}

function get_users() {
    fetch("/admin/get_users").then(response => response.json()).then(json => set_users(json));
}

function set_users(json) {
    const users = document.getElementById("users");
    usersHTML = "";
    filter = "";
    for (let i in json) {
        let user = json[i];
        usersHTML += `
            <div>
                <img src="${user["picture"]}" alt="">
                <div class="user-info">
                    <div>
                        Email: ${user["email"]}<br>
                        Name: ${user["name"]}<br>
                        Subject: ${user["subject"]}<br>
                    </div>
                    <div>Status: <input id="${user["id"]}" class="status-input" placeholder="${user["status"]}" type="text" size="10"></div>
                </div>
            </div>
        `;
        filter += user["status"];
    }
    page_statuses = "";
    [...document.getElementsByClassName("status-input")].forEach((input) => { page_statuses += input.getAttribute("placeholder"); });
    if (filter != page_statuses) {
        users.innerHTML = usersHTML;
        [...document.getElementsByClassName("status-input")].forEach(input => input.addEventListener("change", () => {
            send_user_status(parseInt(input.getAttribute("id")), input.value);
            input.value = "";
        }));
    }
}

function send_user_status(id, status) {
    return fetch("/admin/set_user_status", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ "id": id, "status": status }) }).then((response) => {
        if (response.status == 200) {
            setTimeout(snackbar_alert, 0, "Done");
        } else {
            setTimeout(snackbar_alert, 0, "Error!");
        }
        get_users();
    });
}

window.addEventListener("load", () => {
    get_users();
    setInterval(get_users, 1000);
});