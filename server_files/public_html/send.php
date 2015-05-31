<?php
//1000 recipient limit

              $content = $_POST["content"];
              $option1 = $_POST["option1"];
              $option2 =  $_POST["option2"];

include_once 'GCM/db_functions.php' ;

$db = new DB_Functions() ;

$result=$db->sendtoAll($content);
echo $result;

?>
