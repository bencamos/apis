<?php
  $date = new DateTime();
  $time = $date->getTimestamp();
  $servername = 'host';
  $username = 'user';
  $password = 'pass';
  $dbname = 'database';
  $con=new mysqli($servername,$username,$password,$dbname);
  $post = file_get_contents('php://input');
  $data = json_decode($post);

  $auth = $data->auth;
  $request = $data->request;
  $contents = $data->contents;
  if($con->connect_error){
    echo 'Connection Faild: ';
    return;
  }
  if(strcmp($auth, "apikey") == 0) {
    if(strcmp($request, "add") == 0) {
      $decoded = base64_decode($contents);
      $lines = explode("\n", $decoded);
      foreach ($lines as $temp) {
        $temp2 = explode(" | ", $temp);
        $user = $temp2[0];
        $ip = $temp2[1];
        $deviceToken = $temp2[2];
        $date = intval(microtime(true) * 1000);
        $res=$con->query("INSERT INTO users (user, ip, updated, deviceToken) VALUES ('$user', '$ip', '$date', '$deviceToken')");
      }
    }
  }
?>
