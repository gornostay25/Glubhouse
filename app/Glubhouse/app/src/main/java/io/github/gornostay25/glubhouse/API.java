package io.github.gornostay25.glubhouse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import java.io.Console;
import android.util.Log;


public class API extends Activity{
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    
    
      Intent resultIntent = new Intent();
      Intent Myintent = getIntent();
      
//    if (Myintent.getStringExtra("test") == "13trst2215"){
//      Toast.makeText(getApplicationContext(), "Fuck", Toast.LENGTH_LONG).show();
//      finish();
//    }
    SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
      if(Myintent.getAction() == "getcred"){
        
        Bundle mBundle = new Bundle(); 
        mBundle.putString("user_device", sharedPref.getString("user_device", "")); 
        mBundle.putString("user_id", sharedPref.getString("user_id", ""));
        mBundle.putString("user_token", sharedPref.getString("user_token", ""));
        resultIntent.putExtras(mBundle);

      }else if(Myintent.getAction() == "setcred"){
        
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("user_device", Myintent.getStringExtra("udevice"));
        editor.putString("user_id", Myintent.getStringExtra("uid"));
        editor.putString("user_token", Myintent.getStringExtra("utoken"));
        
        editor.commit();
        editor.apply();
        Bundle mBundle = new Bundle(); 
        mBundle.putString("user_device", sharedPref.getString("user_device", "")); 
        mBundle.putString("user_id", sharedPref.getString("user_id", ""));
        mBundle.putString("user_token", sharedPref.getString("user_token", ""));
        resultIntent.putExtras(mBundle);
        
        
      }else{
        finish();
      }

      setResult(2, resultIntent);
      finish();
    
    
    
  }
}
