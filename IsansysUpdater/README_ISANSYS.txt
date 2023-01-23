Versioning stuff came from http://ballardhack.wordpress.com/2010/09/28/subversion-revision-in-android-app-version-with-eclipse/

The Keystore came from http://developer.android.com/tools/publishing/app-signing.html
"ant.properties" was manually created to reference this keystore so we can sign the Release builds. This has the key password "L1feC4r3!" in plaintext as this is the only way to do it automatically


To build in command line...
1. Install ANT via "https://code.google.com/p/winant/"
2. Open a command promt in the project directory. E.g. C:\Users\Mats\Desktop\Gateway Android\trunk\User_Interface
3. Make sure that the Android SDK is in your PATH variable (D:\adt-bundle-windows-x86_64-20140702\sdk\tools). 
	If not then type "set PATH=%PATH%;d:\adt-bundle-windows-x86_64-20140702\sdk\tools"
4. Delete "build.xml", the "bin" and "gen" directories if they exist. This will allow the build to start from a clean slate
5. Type in "android update project -p ." This will generate a build.xml which ANT requires
6. Type in "ant debug" or "ant release"




The PhoneHome code allows remove sending of the Android logs to a Logging Server 
Code from http://mattspitz.me/2013/12/05/phonehome-remote-logging-for-android.html and https://github.com/nebulabsnyc/PhoneHome

