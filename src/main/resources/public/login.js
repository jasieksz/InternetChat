
id("loginButton").addEventListener("click", function () {
    document.cookie = "username=" + id("username").value;
    location.href = "chat";
});

//Send message if enter is pressed in the input field
id("loginform").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) {
        document.cookie = "username=" + (e.target.value);
        location.href = "chat";
    }
});

function id(id) {
    return document.getElementById(id);
}