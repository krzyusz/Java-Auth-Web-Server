<?php
    $conn = new mysqli("localhost", "root", "");
    mysqli_select_db($conn,"auth");
    if(isset($_COOKIE['browserid'])){
        $browserid = $_COOKIE['browserid'];
        $q=  "SELECT `logged` FROM `users` WHERE `browserid` = '$browserid';";
        $res = mysqli_query($conn,$q);
        if(strval(mysqli_fetch_array($res)[0]) == "1"){
            echo "ok";
        }else{
            echo "ds";
        }
    }
    mysqli_close($conn);
?>