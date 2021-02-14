<?php
    if(isset($_POST['login'])){
        $conn = new mysqli("localhost", "root", "");
        mysqli_select_db($conn,"auth");
        $log = $_POST['login'];
        $res = mysqli_query($conn,"SELECT * FROM users WHERE login LIKE '$log';");
        $arr = mysqli_fetch_array($res);
        echo $arr[0]."  ".$arr[1]."  ".$arr[2]."  ";
        if(hash('sha256',$_POST['pswd'])==$arr[2]){
            $aes = $_POST['aes'];
            $iv = $_POST['iv'];
            $id = $arr[0];
            $query = "UPDATE users SET `aes` = '$aes', `initvector` = '$iv' WHERE `id` = '$id';";
            mysqli_query($conn,$query);
            mysqli_close($conn);
        }
        mysqli_close($conn);
    }



?>