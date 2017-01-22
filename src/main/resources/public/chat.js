//Establish the WebSocket connection and set up event handlers
var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");
webSocket.onmessage = function (msg) { updateChat(msg); };
webSocket.onclose = function () { alert("WebSocket connection closed")};

// CHANNEL CHAT-------------------------------------------------------
//WYSLANIE wiadomosci na kanal
id("sendButton").addEventListener("click", function () { //-> "|message"
    sendMessage(id("message").value);
});

id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) { sendMessage(e.target.value); }
});
//WYJSCIE DO MENU WYBORU KANALU
id("change").addEventListener("click", function () { //-> "|change"
    webSocket.send("|change");
    id("chat").innerHTML = "";
    id("channelChat").style.display="none";
    id("channelSelect").style.display="block";
});

// CHANNEL SELECT --------------------------------------------------
id("createButton").addEventListener("click", function () { //-> "|newchn"
    if(id("channelname").value!="") {
        webSocket.send(id("channelname").value+"|newchn");
        id("channelname").value = "";
    }
});

id("channelname").addEventListener("keypress", function (e) {
    if (e.keyCode === 13 && id("channelname").value!="") {
        webSocket.send(e.target.value+"|newchn");
        id("channelname").value="";
    }
});


//FUNKCJONALNOSC ----------------------------------------------------
function sendMessage(message) {
    if (message !== "") {
        webSocket.send(message+"|message");
        id("message").value = "";
    }
}

function updateChat(msg) {
    var data = JSON.parse(msg.data);
    if (data.channel === "true") {
        insert("chat", data.userMessage);
        id("userlist").innerHTML = "";
        data.userlist.forEach(function (user) {
            insert("userlist", "<li>" + user + "</li>");

        });
    }
    else {
        id("channellist").innerHTML = "";
        data.channellist.forEach(function (channel) {
            insert("channellist", '<li><button id="' + channel + '" onclick="joinChannel(' + "'" + channel + "'" + ')">' + channel  + "</button></li>");
        });
    }
}

function joinChannel(channel) {
    webSocket.send(channel+"|join");
    id("channelSelect").style.display="none";
    id("channelChat").style.display="block"
}

//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}