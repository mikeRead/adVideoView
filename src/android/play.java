package org.ihopkc.videoplayer;

//change this (com.phonegap.helloworld) to your package name, keep the .R
//example: your.package.name.R;
import com.phonegap.helloworld.R;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;





import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class play extends Activity {
  VideoView videoView;
  ImageView imageView;
  ImageButton imageButton;
  String defaultBannerLink = "https://www.ihopkc.org/give";
  String mainVideoUrl = "";
  String AdVideoUrl = "";
  int position = 0;
  boolean isAd = false;
  int nextAdTime = 0;
  MediaController mediaController;
  boolean showAds = true;
  boolean isLive = true;
  int playCount = 0;
   public void onCreate(Bundle savedInstanceState) {
    
          super.onCreate(savedInstanceState);
        
          setContentView(R.layout.activity_player);
          Bundle bundle = getIntent().getExtras();
          String url = bundle.getString("url");
          showAds = bundle.getBoolean("showAds");
      isLive =  false;
          makePlayer(url);
      }
  private void makePlayer(String URL){
    
    
    mainVideoUrl = URL;
    //end here
      videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse(mainVideoUrl));  
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
      
        videoView.requestFocus();
        

        
        if(showAds){
          updateAd();
          
        }
        else{
          ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
          progressBar.setVisibility(View.VISIBLE);
          imageView = (ImageView)findViewById(R.id.imageButton1);
          imageView.setVisibility(View.GONE);
          videoView.start();
        }
    
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
              if(!isAd){
                return;
              }
              ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
            progressBar.setVisibility(View.VISIBLE);
            
          if(showAds){
              isAd = false;
                videoView.setMediaController(mediaController);
                videoView.setVideoURI(Uri.parse(mainVideoUrl));  
                videoView.seekTo(position);
                videoView.start();
                imageView = (ImageView)findViewById(R.id.imageButton1);
                imageView.setVisibility(View.VISIBLE);
             
                if(nextAdTime > 0){
                   final Handler handler = new Handler();
                   handler.postDelayed(new Runnable() {
                       @Override
                       public void run() {
                       updateAd();
                       }
                     }, nextAdTime); 
              }
            
             }
            } 
        });
      
      
     videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

         @Override
         public void onPrepared(MediaPlayer mp) {
           ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
           progressBar.setVisibility(View.GONE);
         
          
         }
     });
  
        
  }


    
    public void updateAd(){ 

       //dose a rest call to get a ad
       //then runs doData on success
       if(playCount == 0 || videoView.isPlaying() && !isAd){
         getdata();  
       }
       else{
         final Handler handler = new Handler();
         handler.postDelayed(new Runnable() {
               @Override
               public void run() {
               updateAd();
               }
             }, 1000);
       }
       
    }

    //modify the banner onclick and image
    public  void doData(String jsonStr){
      
       
       String overlayImage = "";
         position = videoView.getCurrentPosition();
       try {
         //parse the json to get some needed vars
      JSONObject jObject = new JSONObject(jsonStr);
      JSONObject settings = jObject.getJSONObject("settings");
      nextAdTime = settings.getInt("nextAdTime");
      
      JSONObject ad = jObject.getJSONObject("ad");
      JSONObject overlay = ad.getJSONObject("overlay");
      
      overlayImage = overlay.getString("image");
      AdVideoUrl = ad.getString("video");
      defaultBannerLink  = overlay.getString("link");
      
      isAd = true;
      
      if( (playCount == 0) || !isLive){
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
          progressBar.setVisibility(View.VISIBLE);
          
          imageView = (ImageView)findViewById(R.id.imageButton1);
          imageView.setVisibility(View.GONE);
          videoView.setMediaController(null);
          videoView.setVideoURI(Uri.parse(AdVideoUrl)); 
        videoView.start();  
      }
      playCount++;
      
      
            
    } catch (JSONException e) {
    
    }
       
       // Create an object for subclass of AsyncTask
       GetXMLTask task = new GetXMLTask();
       // Execute the task
       
       task.execute(new String[] { overlayImage });
       
       imageView = (ImageView)findViewById(R.id.imageButton1);

      
       //overlay click listner
       imageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
              Uri clickUrl = Uri.parse(defaultBannerLink);
              Intent launchBrowser = new Intent(Intent.ACTION_VIEW, clickUrl);
              startActivity(launchBrowser);
            }
      });

    
       
       
      //update ad after timeout
    if(isLive && playCount > 1 && nextAdTime > 0){
           final Handler handler = new Handler();
           handler.postDelayed(new Runnable() {
               @Override
               public void run() {
               updateAd();
               }
             }, nextAdTime); 
      }
    }

    //load ad overlay
    private class GetXMLTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);
            }
            return map;
        }
 
        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
 
        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;
 
            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.
                        decodeStream(stream, null, bmOptions);
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }
 
        
        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
 
            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();
 
                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }
    
    
    public void getdata() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.
              ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy); 
            URL url = new URL("http://mars.ihopkc.org/vast/live.php?refresh="+Math.random());
            HttpURLConnection con = (HttpURLConnection) url
              .openConnection();
            readStream(con.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }     

    private void readStream(InputStream in) {
      BufferedReader reader = null;
      String line = "";
      try {
        reader = new BufferedReader(new InputStreamReader(in));
        
        while ((line = reader.readLine()) != null) {
          //System.out.println(line);
          doData(line);
        }

      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
      
     
      
    }

   
    
}