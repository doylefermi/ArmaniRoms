<?php
 
// response json
$json = array();
 
/**
 * Registering a user device
 * Store reg id in users table
 */
if (isset($_POST["regId"])) {

    $gcm_regid = $_POST["regId"]; // GCM Registration ID
    
    // Store user details in db
    include_once 'GCM/db_functions.php';
    include_once 'GCM/GCM.php';
 
    $db = new DB_Functions();
    $gcm = new GCM();
 
    $res = $db->storeUser($gcm_regid);
 
    $registatoin_ids = array($gcm_regid);
    $message = array("content" => "Registration complete;http://www.mi.com/in/redmi1s/");
 
    $result = $gcm->send_notification($registatoin_ids, $message);
 
    echo $result;
} else {
    // user details missing
    echo "Incomplete Fields";
}
?>
