(function() {

    if (window.hasRun) {
      return;
    }
    window.hasRun = true;

        
    

    browser.runtime.onMessage.addListener((m)=>{
        if(m.command=="alert"){
            console.log("asdasdasd");
        }
        if(m.command=="test"){
            console.log("bsabasdb");
            /*
            fetch("http://localhost/extTest/index.php")
            .then(r=>r.json())
            .then(res=>console.log(res))
            .catch(e=>console.log(e)); */
            console.log(window.location.toString());
            try{
              /*
              const Http = new XMLHttpRequest();
              const url='http://localhost/extTest/index.php';
              Http.open("GET", url);
              Http.send();

              Http.onreadystatechange = (e) => {
                console.log(JSON.parse(Http.responseText))
              }
              document.getElementById("login").value="asdasd"; */
              fetch("http://localhost/extTest/index.php").then(res=>res.json()).then(r=>console.log(r));
            }catch(e){
              console.log(e);
            }
        }
    })
  })();
  