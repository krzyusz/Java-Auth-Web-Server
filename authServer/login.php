<?php
    $conn = new mysqli("localhost", "root", "");
    mysqli_select_db($conn,"auth");
    if(isset($_POST['aes'])){
        $login = $_POST['aes'];
        $q = "SELECT `id`,`login`,`aes`,`initvector`,`openlogin` FROM `users` WHERE `aes` = '$login';";
        $res = mysqli_query($conn,$q);
        $arr = mysqli_fetch_array($res);
        if($_POST['aes'] == $arr[2] && $_POST['iv'] == $arr[3]){
            if(strval($arr[4])=="1"){
                $id = $arr[0];
                $q1 = "UPDATE `users` SET `logged` = 1 WHERE `id` = '$id';";
                mysqli_query($conn,$q1);
                mysqli_close($conn);
            }else{
                echo "can not login";
            }
        }
    }
?>