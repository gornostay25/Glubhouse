package io.github.gornostay25.glubhouse;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import io.github.gornostay25.glubhouse.MainActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


public class MainActivity extends AppCompatActivity 
{

  public static String extPlgPlusName = "org.qpython.qpy3";
  String AppPath = "/sdcard/qpython/projects3/";
//  public String ZIPARURL = "https://github.com/gornostay25/Glubhouse/raw/main/Glubhouse.zip";
  TextView Pprogress;
  TextView PprogrText;
  public int InstallPipReq = 8725;
  public int StartApp = 2100;
  public int StartQP = 1065;
  public Activity cont;
  public static boolean isDownError=false;


  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    cont = this;
    if (checkPrgPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && 
        checkPrgPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && 
        checkPrgPermission(Manifest.permission.RECORD_AUDIO))
    {
      Log.d("lll", "permission good");
      AfterPermission();
    }
    else
    {
      setContentView(R.layout.grantPerm);
    }


  }

  public void AfterPermission()
  {
    if (funcs.checkAppInstalledByName(getApplicationContext(), extPlgPlusName))
    {
      if (isValidFilePath(AppPath + "Glubhouse/main.py"))
      {
        //installed
        //open

        OpenQpythonProgram();
      }
      else
      {
        //install

        setContentView(R.layout.downloading);
        Pprogress = findViewById(R.id.downloadingProgress);
        PprogrText = findViewById(R.id.downloadingText);


        if (copyAssets())
        {
          AfterDownload();
        }

        //new DownloadFileFromURL().execute("https://github.com/gornostay25/Glubhouse/raw/main/Glubhouse.zip");
        //new DownloadFileFromURL().execute("https://gornostay25.github.io/Glubhouse/Glubhouse.zip");

      }


    }
    else
    {
      Toast.makeText(getApplicationContext(), "Please install QPython first", Toast.LENGTH_LONG).show();

      WebView wbv = new WebView(this);
      wbv.loadUrl("https://gornostay25.github.io/Glubhouse/qpython");
      setContentView(wbv);
      Log.i("lll", "webview showed");
    }


  }




  public void AfterDownload()
  {
    Toast.makeText(getApplicationContext(), "Grant permission and go back", Toast.LENGTH_LONG).show();
    cont.startActivityForResult(new Intent().setClassName(extPlgPlusName, extPlgPlusName + ".MIndexActivity"), StartQP);

//    boolean isUnp = funcs.unpackZip("/sdcard/qpython/projects3/", "Glubhouse.zip");
//    if (isUnp)
//    {
//      Pprogress.setText("30%");
//      Log.i("lll", "30%");
//      new File("/sdcard/qpython/projects3/Glubhouse.zip").delete();
//      Pprogress.setText("50%");
//      Log.i("lll", "50%");
//
//      Toast.makeText(getApplicationContext(), "Grant permission and go back", Toast.LENGTH_LONG).show();
//      cont.startActivityForResult(new Intent().setClassName(extPlgPlusName, extPlgPlusName + ".MIndexActivity"), StartQP);
//
//
//    }
//    else
//    {
//      Pprogress.setText("error unzip");
//    }

  }

  public void RunPipInstalation()
  {

    String code = "import os,zipfile\n" +
      "os.system('mkdir /sdcard/qpython/projects3/Glubhouse')\n" +
      "with zipfile.ZipFile('/sdcard/Android/data/io.github.gornostay25.glubhouse/files/Glubhouse.zip', 'r') as zip_ref:\n" +
      "    zip_ref.extractall('/sdcard/qpython/projects3/')\n" +
      "os.system('cd /sdcard/qpython/projects3/ && rm Glubhouse.zip')\n" +
      "os.system('cd /sdcard/qpython/projects3/Glubhouse && /data/data/org.qpython.qpy3/files/bin/python3-android5 /data/data/org.qpython.qpy3/files/bin/pip3 install -r requirements.txt')";

    Toast.makeText(getApplicationContext(), "Whait for installing and click ENTER", Toast.LENGTH_LONG).show();
    cont.startActivityForResult(funcs.SendCodeToQPython(code), InstallPipReq);

    Pprogress.setText("70%");
    Log.i("lll", "70%");
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {  
    if (requestCode == InstallPipReq)
    {
      if (data != null)
      {
//        Bundle bundle = data.getExtras();
//        String flag = bundle.getString("flag"); // flag you set
//        String param = bundle.getString("param"); // param you set 
//        String result = bundle.getString("result"); // Result your Pycode generate
//        
        OpenQpythonProgram();

      }
      else
      {
        Toast.makeText(this, "onQPyExec: data is null", Toast.LENGTH_SHORT).show();
        finish();
      }
    }
    else if (requestCode == StartApp)
    {
      //startService();
      cont.finish();

    }
    else if (requestCode == StartQP)
    {
      RunPipInstalation();
    }
  }



  private boolean copyAssets()
  {
    AssetManager assetManager = getAssets();
    InputStream in = null;
    OutputStream out = null;
    String filename = "Glubhouse.zip";
    try
    {
      in = assetManager.open(filename);
      File outFile = new File(getExternalFilesDir(null), filename);
      out = new FileOutputStream(outFile);
      copyFile(in, out);
    }
    catch (IOException e)
    {
      Log.e("tag", "Failed to copy asset file: " + filename, e);
      Pprogress.setText("Failed to copy asset file");
      return false;
    }     
    finally
    {
      if (in != null)
      {
        try
        {
          in.close();
        }
        catch (IOException e)
        {
          // NOOP
        }
      }
      if (out != null)
      {
        try
        {
          out.close();
        }
        catch (IOException e)
        {
          // NOOP
        }
      }
    }  
    return true;

  }
  private void copyFile(InputStream in, OutputStream out) throws IOException
  {
    byte[] buffer = new byte[1024];
    int read;
    while ((read = in.read(buffer)) != -1)
    {
      out.write(buffer, 0, read);
    }
  }

  /*

   if (funcs.checkAppInstalledByName(getApplicationContext(), extPlgPlusName)) {
   if (checkPrgPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && checkPrgPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && checkPrgPermission(Manifest.permission.RECORD_AUDIO)){
   if(isValidFilePath(AppPath+"Glubhouse/main.py")){

   //open
   OpenQpythonProgram();

   }else{
   //unpack
   setContentView(R.layout.downloading);
   Pprogress = findViewById(R.id.downloadingProgress);
   PprogrText = findViewById(R.id.downloadingText);
   new DownloadFileFromURL().execute("https://gornostay25.github.io/Glubhouse/Glubhouse.zip");
   //\new DownloadFileFromURL().execute("http://localhost:8080/Glubhouse.zip");


   }


   }else{

   setContentView(R.layout.grantPerm);
   }

   }else {
   Toast.makeText(getApplicationContext(), "Please install QPython first", Toast.LENGTH_LONG).show();


   WebView wbv = new WebView(this);
   wbv.loadUrl("https://gornostay25.github.io/Glubhouse/qpython");
   setContentView(wbv);
   Log.i("lll",wbv.getTitle());


   }

   */

  public static boolean isValidFilePath(String path)
  {
    File f = new File(path);
    return f.exists();
  }

  //Request permiss
  public void requestForSpecificPermission(View v)
  {
    ActivityCompat.requestPermissions(this, new String[]{
                                        Manifest.permission.READ_EXTERNAL_STORAGE, 
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.RECORD_AUDIO,

                                      }, 101);
  }

  private boolean checkPrgPermission(String permission)
  {
    int result = ContextCompat.checkSelfPermission(this, permission);
    if (result == PackageManager.PERMISSION_GRANTED)
    {
      return true;
    }
    else
    {
      return false;
    }
  }
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
  {
    switch (requestCode)
    {
      case 101:
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
            grantResults[1] == PackageManager.PERMISSION_GRANTED &&
            grantResults[2] == PackageManager.PERMISSION_GRANTED
            )
        {

          AfterPermission();

        }
        else
        {
          Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
          Log.e("lll", "notgranted");
          finish();
        }
        break;
      default:
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }


  @Override
  protected void onNewIntent(Intent intent)
  {
    super.onNewIntent(intent);
    setIntent(intent);
    if (Intent.ACTION_VIEW.equals(intent.getAction()))
    {
      //joinChannelFromIntent();
      Uri data=getIntent().getData();
      List<String> path=data.getPathSegments();
      String id=path.get(path.size() - 1);
      if (path.get(0).equals("room"))
      {
        //joinChannelById(id);
        String code = "#qpy::webapp:Glubhouse\n#qpy://localhost:11358/room/" + id + "\n" +
          "import os,re" +
          "for x in os.popen(\"ps -o args -a\").read().split(\"\\n\")[1:-1]:\n" +
          "    if (re.match(x),'Glubhouse/main.py$'):" +
          "        print('yes',x)" +
          "    else: print('no',x)";
        /*
         #qpy:webapp:Glubhouse
         #qpy://localhost:11358/start
         def noRepeat():
         me = os.popen("ps -o args -p "+str(os.getpid())).read().split("\n")[1:-1][0]
         for x in os.popen("ps -o pid,args -a").read().split("\n")[1:-1]:
         x = x.split(' ',1)
         print(x[1] == me)
         if(x[0] != str(os.getpid()) and x[1] == me):
         sys.exit(0)
         */
        cont.startActivityForResult(funcs.SendCodeToQPython(code), 1220);
      }
      else if (path.get(0).equals("event"))
      {
        //
        String code = "";
        cont.startActivityForResult(funcs.SendCodeToQPython(code), 1220);
      }
    }
  }




  /**
   * Background Async Task to download file
   * */
//  class DownloadFileFromURL extends AsyncTask<String, String, String>
//  {
//
//    
//    /**
//     * Before starting background thread Show Progress Bar Dialog
//     * */
////    @Override
////    protected void onPreExecute()
////    {
////      super.onPreExecute();
////      //set procents
////    }
//
//    /**
//     * Downloading file in background thread
//     * */
//    @Override
//    protected String doInBackground(String[] p)
//    {
//      int count;
//      try
//      {
//        URL url = new URL(ZIPARURL);
//        URLConnection connection = url.openConnection();
//        connection.connect();
//
//        // this will be useful so that you can show a tipical 0-100%
//        // progress bar
//        int lenghtOfFile = connection.getContentLength();
//
//        // download the file
//        InputStream input = new BufferedInputStream(url.openStream(),
//                                                    8192);
//
//        // Output stream
//        OutputStream output = new FileOutputStream("/sdcard/qpython/projects3/Glubhouse.zip");
//
//        byte data[] = new byte[1024];
//
//        long total = 0;
//
//        while ((count = input.read(data)) != -1)
//        {
//          total += count;
//          // publishing the progress....
//          // After this onProgressUpdate will be called
//          publishProgress("" + (int) ((total * 100) / lenghtOfFile));
//
//          // writing data to file
//          output.write(data, 0, count);
//        }
//
//        // flushing output
//        output.flush();
//
//        // closing streams
//        output.close();
//        input.close();
//
//      }
//      catch (Exception e)
//      {
//        Pprogress.setText("Error: "+e.getMessage());
//        Log.e("Error: ", e.getMessage());
//        isDownError = true;
//      }
//
//      return null;
//    }
//
//    /**
//     * Updating progress bar
//     * */
//    protected void onProgressUpdate(String... progress)
//    {
//      // setting progress percentage
//      Pprogress.setText(progress[0] + "%");
//
//    }
//
//    /**
//     * After completing background task Dismiss the progress dialog
//     * **/
//    @Override
//    protected void onPostExecute(String file_url)
//    {
//      // dismiss the dialog after the file was downloaded
//      if (isDownError){
//        
//      }else{
//      Log.i("lll", "Downloaded");
//      PprogrText.setText("Setup program...");
//      Pprogress.setText("0%");
//      AfterDownload();
//
//      }
//    }
//
//  }



  public void OpenQpythonProgram()
  {
    final Intent intent = new Intent(Intent.ACTION_MAIN, null);
    intent.addCategory(Intent.CATEGORY_LAUNCHER);

    intent.setClassName(extPlgPlusName, "org.qpython.qpylib.MPyApi");
    intent.setAction(extPlgPlusName + ".action.MPyApi");
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    Bundle mBundle = new Bundle();
    mBundle.putString("com.quseit.common.extra.CONTENT_URL0", "shortcut");
    mBundle.putString("com.quseit.common.extra.CONTENT_URL1", "project");
    mBundle.putString("com.quseit.common.extra.CONTENT_URL2", "/sdcard/qpython/projects3/Glubhouse");
    intent.putExtras(mBundle);
    cont.startActivityForResult(intent, StartApp);

//		final Intent intent = new Intent(Intent.ACTION_MAIN, null);
//		intent.addCategory(Intent.CATEGORY_LAUNCHER);
//		final ComponentName cn = new ComponentName("org.qpython.qpylib","org.qpython.qpylib.MPyApi");
//		intent.setComponent(cn);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.setAction(extPlgPlusName + ".action.MPyApi");
//		Bundle mBundle = new Bundle();
//		mBundle.putString("com.quseit.common.extra.CONTENT_URL0","shortcut");
//		mBundle.putString("com.quseit.common.extra.CONTENT_URL0","project");
//		mBundle.putString("com.quseit.common.extra.CONTENT_URL0","/sdcard/qpython/projects/Glubhouse");
//		intent.putExtras(mBundle);
//		startActivity( intent);
  }


}
