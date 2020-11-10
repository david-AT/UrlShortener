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
                        $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + msg.uri
                            + "'>"
                            + msg.uri
                            +"</a></div>");

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

        $("#qrgenerator").submit(
            function (event) {
                event.preventDefault();
                $.ajax({
                    type: "POST",
                    url: "/linkQR",
                    data: $(this).serialize(),
                    success: function (msg) {
                        $("#QRresult").html(
                            "<img src=\"data:image/png;base64, " + msg.qr + "\" />"
                                  + "<div class='alert alert-success lead'><a target='_blank' href='"
                                  + "data:image/png;base64, " + msg.qr
                                  + "'>"
                                  + msg.uri+".png"
                                  + "</a></div>");

                    },
                    error: function () {
                        $("#QRresult").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });

        $("#showUserAgents").submit(
            function (event) {
                event.preventDefault();
                $.ajax({
                    type: "POST",
                    url: "/userAgents",
                    data: $(this).serialize(),
                    success: function (msg) {
                        const chrome = msg[0];
                        const firefox = msg[1];
                        const ie = msg[2];
                        const opera = msg[3];

                        const windows = msg[6];
                        const linux = msg[7];
                        const ios = msg[4];
                        const android = msg[5];
                        $("#Agentsresult").html(
                            "<div class='alert alert-success lead'><p class='lead'><b>Navegadores:<b></p>"
                            + "<p id=\"chrome\"></p>"
                            + "<p id=\"firefox\"></p>"
                            + "<p id=\"ie\"></p>"
                            + "<p id=\"opera\"></p>"
                            + "<br>"
                            + "<p class='lead'><b>Sistemas operativos:<b></p>"
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
                    type: "POST",
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