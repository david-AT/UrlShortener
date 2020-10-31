var aux = null;
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
                        var outputImg = document.createElement('img');
                        outputImg.src = 'data:image/png;base64,'+msg;
                        if ( aux == null ) {
                            document.body.appendChild(outputImg);
                        }
                        else {
                            document.body.replaceChild(outputImg, aux);
                        }
                        aux = outputImg;
                        $("#QRresult").html(
                            "<div class='alert alert-success lead'>"
                            + "QR generated"
                            +"</div>");
                    },
                    error: function () {
                        $("#QRresult").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
    });