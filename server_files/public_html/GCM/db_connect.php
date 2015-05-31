<?php
  
class DB_Connect {
  
   var $con;
  
    // Connecting to database
    public function connect() {
        require_once 'GCM/config.php';
        // connecting to mysql
        $con = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD,DB_DATABASE);
     
      if($con==null)
           echo "Connection null";
        // return database handler
        return $con;
    }
  
    // Closing database connection
    public function close($con) {
        mysqli_close($this->con);
    }
  
}
?>