<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
</head>
<body>
    <form action="" method="post"> 
        <p>Login:</p>
        <input type="text" name="login">
        <p>Hasło:</p>
        <input type="text" name="pswd">

        <input type="submit" value="zaloguj">
    </form>
    <script src="jquery-3.4.1.min.js"></script>
    <script>
        r = "";
        timeout = setInterval(() => {
            $.ajax({
            url: "checklogin.php"
        }).done(res=>{
            if(res=="ok"){
                clearInterval(timeout);
                var newDoc = document.open("text/html", "replace");
                newDoc.write("zalogowano");
                str = "<form action='./logout.php' method='post'><input type='submit' value='wyloguj'></form>"
                newDoc.write(str);
                newDoc.close();
            }else if(res=="ds"){
                //console.log("wylogowano");
            }
            r = res;
        });
        }, 200);
        
        window.addEventListener("beforeunload", function (e) {
            if(r="ds"){
                $.ajax({url:"setopenlogin.php"});
            }                       //Webkit, Safari, Chrome
        });
        
    </script>
</body>
</html>

<?php
    if(isset($_COOKIE['browserid'])){
        $conn = new mysqli("localhost", "root", "");
        mysqli_select_db($conn,"auth");
        $browserid = $_COOKIE['browserid'];
        $q = "UPDATE `users` SET `openlogin` = '1' WHERE `browserid` = '$browserid';";
        mysqli_query($conn,$q);
        mysqli_close($conn);
    }
    if(isset($_POST['login'])){
        $conn = new mysqli("localhost", "root", "");
        mysqli_select_db($conn,"auth");
        $log = $_POST['login'];
        $pass = hash("sha256",$_POST['pswd']);
        $q1 = "SELECT `password` FROM `users` WHERE `login` = '$log';";
        $res = mysqli_query($conn,$q1);
        if(mysqli_fetch_array($res)[0]==$pass){
            $q = "SELECT `browserid` FROM `users` WHERE `login` = '$log'; ";
            $r = mysqli_query($conn,$q);
            if(trim(mysqli_fetch_array($r)[0])==""){
                $browserid = hash("md5",bin2hex(random_bytes(16)));
                $q2 = "UPDATE `users` SET `browserid` = '$browserid' WHERE `login` = '$log';";
                mysqli_query($conn,$q2);
                setcookie("browserid",$browserid,time()+(3600*24*365));
                unset($_POST);
                mysqli_close($conn);
            }
        }
        
    }
?>