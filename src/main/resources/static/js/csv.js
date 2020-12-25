// ******************************************************************
// REFERENCES:
// Create CSV file:
//   https://codepen.io/Doxxtor/pen/yLyaPPz
// Read CSV file with Papa Parse library:
//   https://alcales.com/leer-fichero-csv-usando-javascript-y-html5/
//   https://www.youtube.com/watch?v=WrI19Qp6Uoc
// ******************************************************************

var stompClient = null;
var connected = false;
var cont = 1;
var contenidoCSV = [];
var tamCSV = 0;

// Connect WebSocket
function connect() {
    // Clean last data
    $("#CSVresult").html("");
    contenidoCSV = [];
    console.log('Intentando conectar ...');
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/topic/websocketClient', function (shortURL) {
            showMessage(JSON.parse(shortURL.body).content);
        });
    });
}

// Disconnect WebSocket
function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
        connected = false;
    }
    console.log("Disconnected");
}

// Treat receive message
function showMessage(message) {
    message = window.location.protocol + "//" + window.location.host + message;

    // Give feedback on screen
    var msgSplitted = message.split(",");
    if (msgSplitted[2]=="VALID"){
        $("#CSVresult").append("<tr><td> URL " + msgSplitted[1] + " shortened successfully</td><td> | " 
            + ((tamCSV-cont)-1) + " left |</td></tr>");
    }else{
        $("#CSVresult").append("<tr><td> URL " + msgSplitted[1] + " NOT shortened</td><td> | " 
            + ((tamCSV-cont)-1) + " left |</td></tr>");
    }

    // Save msg received
    contenidoCSV.push(message);
    cont +=1;
    if (cont==tamCSV) {
        $("#CSVresult").append("<button onclick=\"downloadFile()\">Download CSV file</button>");
        cont = 1;
        tamCSV = 0;
        disconnect();
    }
}

// Send a message
function sendURL(url) {
    stompClient.send("/app/websocketServer", {}, JSON.stringify({'name': url}));
}

// Convert String Array to CSV
function arrayObjToCsv(ar) {
	//comprobamos compatibilidad
	if(window.Blob && (window.URL || window.webkitURL)){
		var contenido = "",
			d = new Date(),
			blob,
			reader,
			save,
			clicEvent;
		//creamos contenido del archivo
		for (var i = 0; i < ar.length; i++) {
			contenido += ar[i] + "\n";
		}
		//creamos el blob
		blob =  new Blob(["\ufeff", contenido], {type: 'text/csv'});
		//creamos el reader
		var reader = new FileReader();
		reader.onload = function (event) {
			//escuchamos su evento load y creamos un enlace en dom
			save = document.createElement('a');
			save.href = event.target.result;
			save.target = '_blank';
			//le damos nombre al archivo
			save.download = "URLsAcortadas.csv";
			try {
				//creamos un evento de click
				clicEvent = new MouseEvent('click', {
					'view': window,
					'bubbles': true,
					'cancelable': true
				});
			} catch (e) {
				//probablemente implemente la forma antigua de crear un enlace
				clicEvent = document.createEvent("MouseEvent");
				clicEvent.initEvent('click', true, true);
			}
			//disparamos el evento
			save.dispatchEvent(clicEvent);
			//liberamos el objeto window.URL
			(window.URL || window.webkitURL).revokeObjectURL(save.href);
		}
		//leemos como url
		reader.readAsDataURL(blob);
	}else {
		//el navegador no admite esta opción
		alert("Su navegador no permite esta acción");
	}
};

// Download file button function
function downloadFile() {
    arrayObjToCsv(contenidoCSV);
}

// Sends all URLs in the file
function treatURLs(results) {
    var data = results.data;
    tamCSV = data.length;
    for (i in data) {
        if (data[i]!="") sendURL(data[i].toString());
    }
}

// Upload file action
$(function () {
    $( "#submit-file" ).click(function() { 
        if (!connected){
            connected = true;
            connect();
            // Wait 2 seconds for doing the WebSocket conecction
            setTimeout(() => {  
                console.log("Tiempo de conexión agotado");
                Papa.parse(document.getElementById('csv').files[0], {
                    download: true,
                    header: false,
                    complete: function(results){
                        console.log(results);
                        treatURLs(results);
                    }
                });
            },2000);
        } else {
            console.log("Conexion establecida pendiente");
        }
    });
});