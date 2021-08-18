/*
Basically creates a free webhost that cant really get hacked or DDoSed.
Although discord webhooks can be deleted pretty easily all you need to do is have a discord bot that detects
when the webhook is deleted and instantly create another one updating the domain.

Since the data being sent dosent have to be instant the application can just cache data in one big message until it succesfully sends.
*/
using(WebClient client = new WebClient()) {
  string[] json = client.DownloadString("http://database.url.net/").Replace(" ", "").Split(',');
  string id = json[1].ToString().Replace("\"id\":\"", "").Replace("\"", "");
  string token = json[7].ToString().Replace("\"token\":\"", "").Replace("\"}", "");
  using (WebClient client2 = new WebClient()){
    string url = "https://discord.com/api/webhooks/" + id.ToString() + "/" + token.ToString() + "";
    client2.UploadValues(url, new NameValueCollection(){
      { "username", "user" },
      { "content", "test" }
    });
   }
  }
