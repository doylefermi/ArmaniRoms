<?php
 
// response json
$json = array();
 
/**
 * Registering a user device
 * Store reg id in users table
 */
if (isset($_POST["fb_id"]) && isset($_POST["event_id"])) {
   $fb_id = $_POST["fb_id"]; 
   $event_id = $_POST["event_id"];
   
    // Store user details in db
    include_once 'GCM/db_functions.php';
    include_once 'GCM/GCM.php';
 
    $db = new DB_Functions();
    $gcm = new GCM();
    $gcm_regid=$db->getregid($fb_id);
    
    $event=$db->geteventname($event_id);
    $res = $db->subscribe($event_id,$fb_id);
 
    $registatoin_ids = array($gcm_regid);
    if($res=="success")
    {
    $message = array("content" => "Subscribed to ".$event);
     }
     elseif($res=="already")
     {
         $message = array("content" => "Already subscribed to ".$event);
     }
     else
     {
         $message = array("content" => "Somethings wrong.Server returned:".$res);
     }
    $result = $gcm->send_notification($registatoin_ids, $message);
 
    echo $result;
} else {
    // user details missing
    echo "Incomplete Fields";
}
?>
