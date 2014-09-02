Video View Plugin
=========
opens a videoView in android and plays any android supported video

Install
---
```sh
phonegap create videoview
cd videoview
phonegap plugin add https://github.com/mikeRead/adVideoView.git
```
Read "Important!" below VVV

IMPORTANT!
----
In most cases,after installing this plugin, you will need to edit line 24 of platforms/android/src/org/ihopkc/videoplayer/play.java ( https://github.com/mikeRead/videoview/blob/master/src/android/play.java )

Find (on line 24) 

> import com.phonegap.helloworld.R;

and replace with your own project name space

for example, if you ran 

```sh 
phonegap create name com.example.project_name_space "CordovaProjectName"
```
you will need to replace (on line 24)
```sh 
import com.phonegap.helloworld.R;
```
with

```sh 
import com.example.project_names_space.R;
```
then you can run 
```sh 
phonegap build android
```
successfully


Usage
---
To open the video in android's video view run this javascript function
```sh 
 var showAds = true;  //or false;
 var isLive  = false; //or true;
 var adServer = "http://mars.ihopkc.org/vast/live.php"; //optinal
 window.androidPlay('http://link/to/android/supported/video.mp4', showAds, isLive, adServer);
```
 Ad server example :
 ```sh 
{
    settings: {
        nextAdTime: 30000 //ms till next ad will play
    },
    ad: {
        overlay: {
            image: "banner image url",
            link: "on click url"
        },
    video: "video url"
    }
}
 ```
---find and change "http://mars.ihopkc.org/vast/live.php" in play.java to your url



----------------------------------------------------------
How to use with vitamio libs and Eclipse IDE
---
Warnings
---
phonegap will not comple after doing the steps below and you must manually complie with eclipse or another ide
1.
---
import the vitamio folder as an android project

to set vitamio lib in phonegap project:

```right click on phonegap project (in package explorer) -> properties -> Android -> click Add -> Click Inint Activity -> OK -> OK```

2.
---
Add permissions and vitamio activity

Edit: "Phonegap Project"/AndroidMainifest.xml

add needed permissons

```sh
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" /> 
```	

add  vitamio init activity
```sh 
<activity android:name="io.vov.vitamio.activity.InitActivity"
    android:configChanges="orientation|screenSize|smallestScreenSize|keyboard|keyboardHidden|navigation"
    android:launchMode="singleTop"
    android:theme="@android:style/Theme.NoTitleBar"
    android:windowSoftInputMode="stateAlwaysHidden" />
```

3.
---
Switch Android's Player libs with vitamio's libs

Edit: "Phonegap Project"/src/org.ihopkc.videoview/play.java

remove android's imports for Meida Player, Media Controller, VideoView and Add imports below
```
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;```


add the fallowing code to onCreate function right after "super.onCreate(savedInstanceState);"
``` 
if (!LibsChecker.checkVitamioLibs(this))
	return;
```
 
 change global var position type form   ```int``` to  ```long ```

4.
---
Switch Android's video view to Vitamio's video view

Edit: "Phonegap Project"/res/layout/activity_player.xml 

change the ```<VideoView>``` tag to ```<io.vov.vitamio.widget.VideoView>``` 

5. (Optional)
---
 remove file name from player
Edit: InitActivity/res/layout/medaiconroller.xml
	find last ```<TextView>``` with ```android:id="@+id/mediacontroller_file_name"``` attribute and add the attrubute ```android:visibility="gone"```

