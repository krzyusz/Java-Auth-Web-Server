<?php
    if(isset($_COOKIE['browserid'])){
        $browserid = $_COOKIE['browserid'];
        $conn = new mysqli("localhost", "root", "");
        mysqli_select_db($conn,"auth");
        mysqli_query($conn,"UPDATE `users` SET `openlogin` = 0 WHERE `browserid` = '$browserid';");
        mysqli_close($conn);
    }
?>