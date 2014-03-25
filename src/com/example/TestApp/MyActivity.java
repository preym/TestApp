package com.example.TestApp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import com.microsoft.hsg.Response;
import com.microsoft.hsg.android.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class MyActivity extends Activity implements HealthVaultInitializationHandler {

  private static HealthVaultService service;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    connect();


  }

  private void connect() {
    service = HealthVaultService.initialize(this);
    HealthVaultSettings settings = service.getSettings();

    if (settings.getConnectionStatus() == HealthVaultService.ConnectionStatus.Connected) {
      OnConnected();

    } else {
      Logger.debug("Not Connected");
      settings.setMasterAppId("f0ae4fbc-4267-485b-9835-248f4ff2378d");
      settings.setServiceUrl("https://platform.healthvault-ppe.com/platform/wildcat.ashx");
      settings.setShellUrl("https://account.healthvault-ppe.com");
      settings.setIsMultiInstanceAware(true);
      service.connect(this, this);
      OnConnected();
    }

  }

  @Override
  public void OnConnected() {
    Logger.debug("connected Successfully");
    new BackgroundTask().execute();
  }

  @Override
  public void onError(Exception e) {
    Logger.debug("error:" + e.getMessage());
  }

  private static class BackgroundTask extends AsyncTask {

    @Override
    protected Object doInBackground(Object... params) {
      String userId = "";
      String recordId = "";
      List<PersonInfo> personList = service.getPersonInfoList();
      Logger.debug(personList + "");

      if (personList != null) {
        for (PersonInfo info : personList) {
          Logger.debug("Id:" + info.getPersonId() + "--" + "Name:" + info.toString() + "--" + info.getRecords());
          userId = info.getPersonId();
          List<Record> records = info.getRecords();
          if (records != null) {
            for (Record record : records) {
              Logger.debug("Id" + record.getId() + "--" + "Name" + record.getName() + "--" + "personId" + record.getPersonId()

                  + record.toString());
              recordId = record.getId();
            }
          }
        }
      }

//      try {
//        HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post")
//            .header("accept", "application/json")
//            .field("parameter", "value")
//            .field("foo", "bar")
//
//            .asJson();
//      } catch (Exception e) {
//
//      }

      Response response = service.getThings(recordId, userId);
//      response.getInputStream().toString();
//
      Logger.debug(getStringFromInputStream(response.getInputStream()));


//      try {
//        String URL = "";
//        HttpClient httpclient = new DefaultHttpClient();
//        HttpResponse response = httpclient.execute(new HttpGet(URL));
//
//        StatusLine statusLine = response.getStatusLine();
//        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
//          ByteArrayOutputStream out = new ByteArrayOutputStream();
//          response.getEntity().writeTo(out);
//          out.close();
//          String responseString = out.toString();
//          //..more logic
//        } else {
//          //Closes the connection.
//          response.getEntity().getContent().close();
//
//        }
//      } catch (Exception e) {


      return null;
    }


    private static String getStringFromInputStream(InputStream is) {

      BufferedReader br = null;
      StringBuilder sb = new StringBuilder();

      String line;
      try {

        br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) {
          sb.append(line);
        }

      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (br != null) {
          try {
            br.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }

      return sb.toString();

    }


  }

}
