/*
No SQLi protection rn although its a simple edition this is private and database has million of entries so I need minimal exec time.
//$stmt->bind_param("i", $user);
*/
<?php
  $date = new DateTime();
  $time = $date->getTimestamp();
  $servername = 'host';
  $username = 'proxy';
  $password = 'pass';
  $dbname = 'userDB';
  $con=new mysqli($servername,$username,$password,$dbname);
  $post = file_get_contents('php://input');
  $data = json_decode($post);

  $auth = $data->auth;
  $request = $data->request;
  $contents = $data->contents;
  if($con->connect_error){
    echo 'Connection Faild: '.$con->connect_error;
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
    if(strcmp($request, "get") == 0) {
      $decoded = base64_decode($contents);
      $lines = explode("\n", $decoded);
      foreach ($lines as $temp) {
        $stmt = $con->prepare("SELECT * FROM users WHERE user like \"%$temp%\" OR ip like \"%$temp%\"");
        $stmt->execute();
        $result = $stmt->get_result();
        $data = $result->fetch_all(MYSQLI_NUM);

        if ($data) {
            foreach ($data as $row) {
              echo $row[1] . " | " . $row[2] . " | " . $row[3] . " | " . $row[4];
            }
        } else {
            die();
        }
        mysqli_stmt_close($stmt);
      }
    }
    if(strcmp($request, "alt") == 0) {
      $decoded = base64_decode($contents);
      $lines = explode("\n", $decoded);
      foreach ($lines as $temp) {
        $stmt = $con->prepare("SELECT * from users WHERE user like \"%$temp%\" OR ip like \"%$temp%\" LIMIT 1");
        $stmt->execute();
        $result = $stmt->get_result();
        $data = $result->fetch_all(MYSQLI_NUM);

        if ($data) {
            foreach ($data as $row) {
              $stmt1 = $con->prepare("SELECT distinct(user) from users WHERE deviceToken = \"$row1[4]\"");
              $stmt1->execute();
              $result1 = $stmt1->get_result();
              $data1 = $result1->fetch_all(MYSQLI_NUM);

              if ($data1) {
                  foreach ($data1 as $row1) {
                    echo $row1[1] . " | " . $row1[2] . " | " . $row1[3] . " | " . $row1[4];
                  }
              } else {
                  die();
              }
            }
        } else {
            die();
        }
        mysqli_stmt_close($stmt);
      }
    }
  }
?>
