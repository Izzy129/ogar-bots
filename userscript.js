// ==UserScript==
// @name         multi ogar bots java test
// @namespace    testing
// @version      0.1
// @description  credits to BadPlayer for gui and some other code
// @author       Izzy129 (Baracuda), BadPlayer
// @match        http://127.0.0.1:3000/*
// @match        https://izzyagar.glitch.me/*
// @run-at       document-end
// @require      https://code.jquery.com/jquery-3.7.1.slim.min.js
// @require      https://cdnjs.cloudflare.com/ajax/libs/socket.io/1.7.3/socket.io.min.js
// @require      https://cdn.jsdelivr.net/particles.js/2.0.0/particles.min.js
// @grant        none
// ==/UserScript==

console.log("ogarbot script loaded");
(function () {
    setTimeout(function () {
        'use strict';
        window.user = {
            x: 0,
            y: 0,
            ip: null,
            origin: null,
            startedBots: false
        };
        document.addEventListener('keydown', function (e) {
            setTimeout(function () {
                var key = e.keyCode || e.which; // deprecated, look into replacement
                switch (key) {
                    case 69:
                        socket.emit('splitDown');
                        console.log("split Down");
                        break;
                    case 82:
                        socket.emit('ejectDown');
                        console.log("eject Down");
                        break;
                    case 67:
                        var msg = prompt("What do you want to spam?", "I'm not a bot");
                        socket.emit('spam', msg);
                        console.log("spam press with message: " + msg);
                        break;
                }
            }, 250);
        });
        document.addEventListener('keyup', function (e) {
            setTimeout(function () {
                var key = e.keyCode || e.which; // deprecated, look into replacement
                switch (key) {
                    case 69:
                        socket.emit('splitUp');
                        console.log("split Up");
                        break;
                    case 82:
                        socket.emit('ejectUp');
                        console.log("eject Up");
                        break;
                    case 67:
                        var msg = prompt("What do you want to spam?", "I'm not a bot");
                        socket.emit('spam', msg);
                        console.log("spam press with message: " + msg);
                        break;
                }
            }, 250);
        });
        // new code here
        WebSocket.prototype._send = WebSocket.prototype.send;
        WebSocket.prototype.send = function (data) {
            this._send(data);
            this.send = function (data) {
                this._send(data);
                if (typeof data == 'string') return; // invalid data can cause crash
                if (data instanceof ArrayBuffer) data = new DataView(data); // fixes "first parse cannot be arraybuffer"
                else if (data instanceof DataView) data = data; // provided data is correct
                else data = new DataView(data.buffer); // converts to arraybuffer

                if (data.byteLength === 21) { // 64 bit, newer protocol?
                    if (data.getInt8(0, true) === 16) {
                        window.user.x = data.getFloat64(1, true);
                        window.user.y = data.getFloat64(9, true);
                    }
                }
                if (data.byteLength === 13) { // 32 bit, multogar protocol
                    if (data.getUint8(0, true) === 16) {
                        window.user.x = data.getInt32(1, true);
                        window.user.y = data.getInt32(5, true);
                    }
                }
                if (data.byteLength === 5 || data.byteLength < 4) { // 16 bit, old protocol
                    if (data.getUint8(0, true) === 16) {
                        window.user.x = data.getInt16(1, true);
                        window.user.y = data.getInt16(3, true);
                    }
                }
                if (this.url !== null) {
                    window.user.ip = this.url;
                }
                window.user.origin = location.origin;
            }
        }

        var socket = io.connect('ws://localhost:8080');

        setInterval(function () {
            socket.emit('movement', {
                x: window.user.x,
                y: window.user.y,
            });
            $('#mouseGuiX').html(window.user.x);
            $('#mouseGuiY').html(window.user.y);
        }, 100);
        setInterval(function () {
            socket.emit('requestCount');

        }, 2000);
        window.start = function () {
            socket.emit('start', {
                ip: window.user.ip !== null ? window.user.ip : 0,
                origin: location.origin
            });
            console.log("start bots packet sent");
        };
        setTimeout(function () { //<div style='box-shadow: 0px 0px 20px black;z-index:9999999; background-color: #000000; -moz-opacity: 0.4; -khtml-opacity: 0.4; opacity: 0.7; zoom: 1; width: 205px; top: 300px; left: 10px; display: block; position: absolute; text-align: center; font-size: 15px; color: #ffffff; font-family: Ubuntu;border: 2px solid #0c31d4;'> <div style='color:#ffffff; display: inline; -moz-opacity:1; -khtml-opacity: 1; opacity:1;font-size: 22px; filter:alpha(opacity=100); padding: 10px;'><a>Trap Client</a></div> <div style=' color:#ffffff; display: inline; -moz-opacity:1; -khtml-opacity: 1; opacity:1; filter:alpha(opacity=100); padding: 10px;'><br>Minions: <a id='minionCount'>Offline</a> </div><button id='start-bots' style='display: block;border-radius: 5px;border: 2px solid #6495ED;background-color: #BCD2EE;height: 50px;width: 120px;margin: auto;text-align: center;'>StartBots </button><marquee>TrapKillo - Owner</marquee> </div>
            $("#canvas").after("<div  id = 'gui' style='box-shadow: 0px 0px 20px black;z-index:9999999; background-color: #000000; -moz-opacity: 0.4; -khtml-opacity: 0.4; opacity: 0.7; zoom: 1; width: 205px; top: 300px; left: 10px; display: block; position: absolute; text-align: center; font-size: 15px; color: #ffffff; font-family: Ubuntu;border: 2px solid #0c31d4; border-radius: 15px 50px;'> <div style='color:#ffffff; display: inline; -moz-opacity:1; -khtml-opacity: 1; opacity:1;font-size: 22px; filter:alpha(opacity=100); padding: 10px;'><a id='Client_Name'>Agar infinity</a></div> <div style=' color:#ffffff; display: inline; -moz-opacity:1; -khtml-opacity: 1; opacity:1; filter:alpha(opacity=100); padding: 10px;'><br>Minions: <a id='minionCount'>Offline</a> <br>X: <a id='mouseGuiX'>0</a> Y: <a id='mouseGuiY'>0</a> <br> <button id='start-bots' style='display: block;border-radius: 5px;border: 2px solid #6495ED;background-color: #BCD2EE;height: 50px;width: 120px;margin: auto;text-align: center;'>StartBots </button></div></div> </div>");
            document.getElementById('start-bots').onclick = function () {
                if (this.startedBots) return; // bots already started
                window.start();
                this.startedBots = true;
            };
        }, 2000);
        socket.on('botCount', function (count) {
            $('#minionCount').html(count);
        });
    }, 2000)
}
)();

var speed = 40;
var hex = new Array("00", "14", "28", "3C", "50", "64", "78", "8C", "A0", "B4", "C8", "DC", "F0");
var r = 1;
var g = 1;
var b = 1;
var seq = 1;

function changetext() {
    var rainbow = "#" + hex[r] + hex[g] + hex[b];
    document.getElementById("gui").style.borderColor = rainbow;
    document.getElementById("Client_Name").style.color = rainbow;
    document.getElementById("minionCount").style.color = rainbow;
}

function change() {
    if (seq == 6) {
        b--;
        if (b == 0)
            seq = 1;
    }
    if (seq == 5) {
        r++;
        if (r == 12)
            seq = 6;
    }
    if (seq == 4) {
        g--;
        if (g == 0)
            seq = 5;
    }
    if (seq == 3) {
        b++;
        if (b == 12)
            seq = 4;
    }
    if (seq == 2) {
        r--;
        if (r == 0)
            seq = 3;
    }
    if (seq == 1) {
        g++;
        if (g == 12)
            seq = 2;
    }
    changetext()
}
setTimeout(function () {
    setInterval(function () {
        change();
    }, speed);
}, 5000);
setTimeout(function () {
    $(function () {
        $("#gui").draggable();
    });
}, 3000);