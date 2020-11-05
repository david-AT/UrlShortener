var aux = null;
var aux2 = null;
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
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                                + "URL Shortened:"
                                + msg.uri
                                + "'>"
                                + msg.uri
                                +"</a></div>"
                                //+ "<img src=data:image/jpg;base64," + msg.qr +" />"
                                + "<div class='alert alert-success lead'><a target='_blank' href='"
                                + msg.qr
                                + "'>"
                                + msg.qr
                                +"</a></div>");
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
                        var browser = msg[0];
                        var so = msg[1];
                        $("#Agentsresult").html(
                            "<div class='alert alert-success lead'><p class='lead'>Navegador:</p><p id=\"browser\"></p>"
                            + "<p class='lead'>Sistema Operativo:</p><p id=\"so\"></p></div>");
                        document.getElementById("browser").innerHTML = browser;
                        document.getElementById("so").innerHTML = so;
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
                            $("#DBresult").html("<div class='alert alert-danger lead'>ERROR</div>");
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