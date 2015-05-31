<?php
 
class DB_Functions {
 
    private $db;
    private $con;

    //put your code here
    // constructor
    function __construct() {
        include_once 'GCM/db_connect.php';
        // connecting to database
        $this->db = new DB_Connect();
        $this->con=$this->db->connect();
    }
 
    // destructor
    function __destruct() {
     $this->db->close($con);    
    }
 
    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($gcm_regid) {
        // insert user into database
        // $result = mysqli_query($this->con,"INSERT INTO subscriptions(fb_id) VALUES('$fb_id')");
        $result = mysqli_query($this->con,"INSERT INTO users(gcm_reg_no) VALUES('$gcm_regid')");
        echo "Trying to insert";
        if (!$result) {
    printf("Error: %s\n", mysqli_error($this->con));
    exit();
}
        // check for successful store
        if ($result) {
            // get user details
            $id = mysqli_insert_id($this->con); // last inserted id
            $result = mysqli_query($this->con,"SELECT * FROM users WHERE id = $id") or die(mysqli_error($con));
            // return user details
            if (mysqli_num_rows($result) > 0) {
                return mysqli_fetch_array($result);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
  public function getnumofusers() {
        // get num
        $result = mysqli_query($this->con,"SELECT * FROM users WHERE 1");
        if (!$result) {
    printf("Error: %s\n", mysqli_error($this->con));
    exit();
}
        // return num
           return mysqli_num_rows($result);
    }
          public function getnumofmsg() {
        // get num
        $result = mysqli_query($this->con,"SELECT allcount FROM stats WHERE 1");
        if (!$result) {
    printf("Error: %s\n", mysqli_error($this->con));
    exit();
}
        // return num
              $result=mysqli_fetch_assoc($result);
           return ($result['allcount']);
    }
       public function incrementcount() {
        // get num
        $result = mysqli_query($this->con,"UPDATE stats SET allcount = allcount + 1;");
        if (!$result) {
    printf("Error: %s\n", mysqli_error($this->con));
    exit();
}
    }


public function sendtoAll($content) {
    //analytics not implemented
    //1000 recipient limit
        // Select all reg ids
        $result = mysqli_query($this->con,"SELECT gcm_reg_no FROM users WHERE 1");

       $reg=array();
       while($registration_ids=mysqli_fetch_array($result,MYSQLI_ASSOC))
       {
          $reg[]=$registration_ids["gcm_reg_no"];
        }
        
    //    $reg=json_encode($reg);

         $message = array("content" => $content);
        // include config
        include_once 'GCM/config.php';
 
        // Set POST variables
        $url = 'https://android.googleapis.com/gcm/send';
 
        $fields = array(
            'registration_ids' => $reg,
            'data' => $message,
        );
 
        $headers = array(
            'Authorization: key=' . GOOGLE_API_KEY,
            'Content-Type: application/json'
        );
        // Open connection
        $ch = curl_init();
 
        // Set the url, number of POST vars, POST data
        curl_setopt($ch, CURLOPT_URL, $url);
 
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
 
        // Disabling SSL Certificate support temporarly
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
 
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
 
        // Execute post
        $result = curl_exec($ch);
        if ($result === FALSE) {
            die('Curl failed: ' . curl_error($ch));
        }
 
        // Close connection
        curl_close($ch);
      //analytics implemented
     //   print_r($fields);
        $r=json_decode($result,true);
        $num_of_success=$r["success"];
        $num_of_failure=$r["failure"];
        echo "Successfully sent ".$num_of_success." messages";
        echo "<br>Failed to sent ".$num_of_failure." messages";
     //   echo"<br>".$result;
        mysqli_query($this->con,"UPDATE stats SET allcount = allcount + $num_of_success;");
    }

     public function getregid($fb_id){
        
        $result = mysqli_query($this->con,"SELECT gcm_reg_no FROM users WHERE fb_id = $fb_id");
        if (!$result) {
        
        printf("Error: %s\n", mysqli_error($this->con));
        exit();
     }
     $result=mysqli_fetch_array($result,MYSQLI_ASSOC);
     return $result["gcm_reg_no"];
}

public function subscribe($event_id,$fb_id){
   
    $sub = mysqli_query($this->con,"SELECT * FROM subscriptions WHERE fb_id = $fb_id");
     $sub=mysqli_fetch_assoc($sub);
     if($sub[$event_id]==0)
    {
        $result = mysqli_query($this->con,"UPDATE subscriptions SET $event_id=1 WHERE fb_id = $fb_id");
        if (!$result) {
        print_r($sub);
        printf("Error: %s\n", mysqli_error($this->con));
        exit();
     }
    return "success";
}
else
{
    return "already";
}
}



     public function geteventname($event_id){
        // get num

        $result = mysqli_query($this->con,"SELECT event_name FROM events WHERE event_id = '$event_id'");
        if (!$result) {
        printf("Error: %s\n", mysqli_error($this->con));
        exit();
     }
     $result=mysqli_fetch_array($result,MYSQLI_ASSOC);
     return $result["event_name"];
}

  public function getevents() {
        // get num
        $result = mysqli_query($this->con,"SELECT * FROM events WHERE 1");
        if (!$result) {
    printf("Error: %s\n", mysqli_error($this->con));
    exit();
}
        // return num
     return $result;
}




public function sendtoevent($event_id,$content) {
    //analytics not implemented
    //1000 recipient limit
        // Select all reg ids
        $result = mysqli_query($this->con,"SELECT gcm_reg_no FROM users WHERE fb_id IN (SELECT fb_id FROM subscriptions WHERE $event_id=1)");

       $reg=array();
       while($registration_ids=mysqli_fetch_array($result,MYSQLI_ASSOC))
       {
          $reg[]=$registration_ids["gcm_reg_no"];
        }
        
    //    $reg=json_encode($reg);

         $message = array("content" => $content);
        // include config
        include_once 'GCM/config.php';
 
        // Set POST variables
        $url = 'https://android.googleapis.com/gcm/send';
 
        $fields = array(
            'registration_ids' => $reg,
            'data' => $message,
        );
 
        $headers = array(
            'Authorization: key=' . GOOGLE_API_KEY,
            'Content-Type: application/json'
        );
        // Open connection
        $ch = curl_init();
 
        // Set the url, number of POST vars, POST data
        curl_setopt($ch, CURLOPT_URL, $url);
 
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
 
        // Disabling SSL Certificate support temporarly
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
 
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
 
        // Execute post
        $result = curl_exec($ch);
        if ($result === FALSE) {
            die('Curl failed: ' . curl_error($ch));
        }
 
        // Close connection
        curl_close($ch);
      //analytics implemented
     //   print_r($fields);
        $r=json_decode($result,true);
        $num_of_success=$r["success"];
        $num_of_failure=$r["failure"];
        echo "Successfully sent ".$num_of_success." messages";
        echo "<br>Failed to sent ".$num_of_failure." messages";
     //   echo"<br>".$result;
        mysqli_query($this->con,"UPDATE stats SET allcount = allcount + $num_of_success;");
    }



}
 
?>
