<?php

$username=$_POST["inputEmail3"];
$password=$_POST["inputPassword3"];

if($username=="doylefermi@gmail.com"&&$password=="doylefermi007")
{
       session_start();
       $_SESSION["user"]="admin";
       header("Location:home.html");

}
else
{
    echo "Invalid username/password";

}

?>
