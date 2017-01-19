//Establish the WebSocket connection and set up event handlers
var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");
webSocket.onmessage = function (msg) { handleMessage(msg); };
webSocket.onclose = function () { alert("WebSocket connection closed") };
webSocket.onopen = login();

//Send message if "Send" is clicked
id("send").addEventListener("click", function () {
    sendMessage(id("message").value);
});

//Send message if enter is pressed in the input field
id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) { sendMessage(e.target.value); }
});

id("addchannel").addEventListener("click", function () {
    webSocket.send("addChannel|");
});

id("exitchannel").addEventListener("click", function () {
    webSocket.send("channelExit|");
});

function channelEnter(channel){
    webSocket.send("channelEnter|" + channel);
}


function login(){
    var username = getCookie("username");// getCookie("username");//= getCookie("username");
    if(username != ""){
        alert("logging as " + username + "...");
        webSocket.send("name|" + username);
        return;
    }
    else
        setUsername();
}

function setUsername(){

    var username = prompt("Type your username: ");

    if (username == null){
        alert("You can't be without username");
        setUsername();
        return;
    }

    username = username.replace(/[^a-zA-Z0-9]*/g, '');

    if (username == ""){
        alert("You can't be without username");
        setUsername();
        return;
    }

    setCookie("username", username);

    webSocket.send("name|" + username);
}

//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
    if (message !== "") {
        webSocket.send("user|" + message);
        id("message").value = "";
    }
}

//Update the chat-panel
function handleMessage(msg) {

    var data = JSON.parse(msg.data);
    if(data.reason == "taken_username"){
        alert("nazwa juz zajeta");
        setUsername();
        return;
    }

    if(data.reason == "message")
        insert("chat", data.userMessage);

    id("channellist").innerHTML = "";

    data.channellist.forEach(function (channel) {

        var znacznik = document.createElement('button');
        znacznik.onclick = function () {channelEnter(channel);}
        var t = document.createTextNode(channel);
        znacznik.appendChild(t);

        var kontener = id("channellist");
        kontener.appendChild(znacznik);
    });

}

function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for(var i = 0; i <ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

function setCookie(name, value) {
    document.cookie = name + "=" + value + ";";
}

//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}