

function extListener(){
    document.addEventListener("click", (e)=>{

        function sendMessage(tabs){
            browser.tabs.sendMessage(tabs[0].id, { command: "alert"});
        }

        function sendMessage1(tabs){
            browser.tabs.sendMessage(tabs[0].id, { command: "test"});
        }

        function reportError(error) {
            console.error(`error: ${error}`);
          }

        if(e.target.id == "asd"){
            browser.tabs.query({active: true, currentWindow: true}).then(sendMessage).catch(reportError);
        }
        if(e.target.id == "fds"){
            browser.tabs.query({active: true, currentWindow: true}).then(sendMessage1).catch(reportError);
        }

    });

}


function reportExecuteScriptError(error) {
    console.error(`Failed content script: ${error.message}`);
}


browser.tabs.executeScript({file: "/scripts/script.js"}).then(extListener).catch(reportExecuteScriptError);
