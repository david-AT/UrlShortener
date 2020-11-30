$(document).ready(
    function () {
        $("#shortener").submit(
            function (event) {
                event.preventDefault();
                $.ajax({
                    type: "POST",
                    url: "/link",
                    data: $(this).serialize(),
                    success: function (msg) {
                        console.log(msg)
                        if (msg.qr != null) {
                            resultado = "<div class ='alert alert-success lead'><div target='_blank' href='"
                            + msg.uri
                            + "'>"
                            + msg.uri
                            + "</div>"

                            + "<div class ='alert alert-success lead'><a target='_blank' href='"
                            + "data:image/png;base64, " + msg.qr
                            + "'>"
                            + msg.uri+ ".png"
                            + "</a></div>"
                        }
                        else {
                            resultado = "<div class ='alert alert-success lead'><a target='_blank' href='"
                            + msg.uri
                            + "'>"
                            + msg.uri
                            + "</a></div>"
                        }
                        $("#result").html(resultado);
                    },
                    error: function () {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
        
        $("#CSVshortener").submit(
            function (event) {
                event.preventDefault();
                $.ajax({
                    type: "POST",
                    url: "/linkCSV",
                    data: new FormData(this),
                    contentType: false,
                    processData: false,
                    success: function (msg) {
                        var blob = new  Blob([msg])
                        var link = document.createElement("a")
                        link.href = window.URL.createObjectURL(blob)
                        link.download = "URLsRecortadas.csv"
                        document.body.appendChild(link)
                        $("#CSVresult").html(
                            "<div class='alert alert-success lead'><a target='_blank' >"
                            + "Download CSV"
                            + "</a></div>");
                        document.getElementById('CSVresult').onclick = function(){ link.click() } 
                    },
                    error: function () {
                        $("#CSVresult").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });

        $("#showUserAgents").submit(
            function (event) {
                event.preventDefault();
                $.ajax({
                    type: "GET",
                    url: "/userAgents",
                    data: $(this).serialize(),
                    dataType: "json",
                    success: function (msg) {
                        const chrome = msg.Chrome;
                        const firefox = msg.Firefox;
                        const ie = msg.InternetExplorer;
                        const opera = msg.Opera;

                        const windows = msg.Windows;
                        const linux = msg.Linux;
                        const ios = msg.iOS;
                        const android = msg.Android;
                        $("#Agentsresult").html(
                            "<div class='alert alert-success lead'><p class='lead'><b>Navegadores:<b></p>"
                            + "<p id=\"chrome\"></p>"
                            + "<p id=\"firefox\"></p>"
                            + "<p id=\"ie\"></p>"
                            + "<p id=\"opera\"></p>"
                            + "</div>"
                            + "<div class='alert alert-success lead'><p class='lead'><b>Sistemas Operativos:<b></p>"
                            + "<p id=\"windows\"></p>"
                            + "<p id=\"linux\"></p>"
                            + "<p id=\"ios\"></p>"
                            + "<p id=\"android\"></p>"
                            + "</div>");
                        document.getElementById("chrome").innerHTML = chrome;
                        document.getElementById("firefox").innerHTML = firefox;
                        document.getElementById("ie").innerHTML = ie;
                        document.getElementById("opera").innerHTML = opera;
                        document.getElementById("windows").innerHTML = windows;
                        document.getElementById("linux").innerHTML = linux;
                        document.getElementById("ios").innerHTML = ios;
                        document.getElementById("android").innerHTML = android;
                    },
                    error: function () {
                        $("#Agentsresult").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });

        $("#DBInfo").submit(
            function (event) {
                event.preventDefault();
                $.ajax({
                    type: "GET",
                    url: "/DBInfo",
                    data: $(this).serialize(),
                    success: function (msg) {
                        if (msg.length == 0) {
                            $("#DBresult").html("<p class='lead' style=\"color:#ff0000\"><b>Data Base is empty</b></p><br/>");
                        } else {
                            var htmlInfo = "";
                            msg.forEach(element => 
                                htmlInfo = htmlInfo + "<p class='lead'>" + element + "</p><br/>");
                            $("#DBresult").html(htmlInfo);
                        }
                    },
                    error: function () {
                        $("#DBresult").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
    });