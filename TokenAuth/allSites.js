if(window.location.toString().includes("poczta.o2.pl/zaloguj")){
    console.log("sending request for poczta.o2.pl");
    const data = {
        "type":"newconnection",
        "browserId":"asdasd",
        "hash":"5fd924625f6ab16a19cc9807c7c506ae1813490e4ba675f843d5a10e0baacdb8",
        "userid":"jdorka"
    }
    const data2 = {
        "type":"newconnection",
        "browserId":"asdasd",
        "hash":"5fd924625f6ab16a19cc9807c7c506ae1813490e4ba675f843d5a10e0baacdb8",
        "userid":"jda"
    }

    fetch("http://192.168.1.37:7070/",{
        method: 'POST',
        body: JSON.stringify(data),
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(res=>res.json())
    .then(r=>{
        console.log(r);

        connectionId= r[0].connectionId;
        hash = r[0].hash; 
        //console.log("Connection id: "+connectionId + ", hash: " + hash);
        siteUrl = "poczta.o2.pl";
        var loginData = {
            "type":"login",
            "connectionId":connectionId,
            "hash":hash,
            "siteURL":siteUrl};

            fetch("http://192.168.1.37:7070/",{
                method: 'POST',
                body: JSON.stringify(loginData),
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(res=>res.json()).then(r=>{
                console.log(r);
                interval = setInterval(function(){
                    var reqData = {
                        "type":"getLoginData",
                        "connectionId":connectionId,
                        "hash":hash,
                        "siteURL":siteUrl};

                    fetch("http://192.168.1.37:7070/",{
                        method: 'POST',
                        body: JSON.stringify(reqData),
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).then(res=>res.json()).then(r=>{
                        console.log(r);
                        if(Array.isArray(r)){
                            clearInterval(interval);
                            document.getElementById("login").value= r[0].login;
                            document.getElementById("password").value= r[0].passw;
                            document.getElementById("login-button").click();
                        }
                    }).catch(e=>console.log(e))
                },500)
                setTimeout(function(){ clearInterval(interval) }, 15000);

            }).catch(e=>console.log(e));
        
        

    }).catch(e=>console.log(e));
  }