<?php
    if(isset($_COOKIE['browserid'])){
        $conn = new mysqli("localhost", "root", "");
        mysqli_select_db($conn,"auth");
        $browserid = $_COOKIE['browserid'];
        $q = "SELECT `logged` FROM `users` WHERE `browserid` = '$browserid';";
        $res = mysqli_fetch_array(mysqli_query($conn,$q))[0];
        if(strval($res)=="1"){
            $q1 = "UPDATE `users` SET `logged` = 0 , `openlogin`= 0 WHERE `browserid` = '$browserid';";
            mysqli_query($conn,$q1);
            mysqli_close($conn);
            header("Location: ./");
        }
    }
?>