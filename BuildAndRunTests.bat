:: Used to build and run Appium tests on Jenkins

:: Arguments are specified as name/value pairs, separated by a colon like so - ARGUMENT_NAME:value, with each name/value pair separated by a space

:: Example Usage: BuildAndRunTests.bat TABLET_SERIAL:123456 SERVER_NAME:Muffin TEST_PACKAGE:tablet_and_server 
:: The above will run the end-to-end tests (tablet_and_server package) on the Muffin development server, using tablet serial number 123456

:: --Default-value arguments--
:: TEST_PACKAGE ANDROID_VERSION and SERVER_NAME have default values (tablet_and_server, 8 and Muffin, respectively) which are used if alternatives are not specified.
:: TEST_PACKAGE can be at the package level (tablet_and_server or tablet_only) or at the class level (e.g. tablet_only.AdminPage)

:: --Optional arguments--
:: If TEST_RUN_ID is specified, it is used to set test results in TestRail automatically - if not specified, TestRail is not updated.
:: If TABLET_SERIAL is set, it determines the serial number of tablet to run the test on. If not set, and there is only one tablet connected, that tablet will be used. 
:: If there are multiple tablets connected, the serial number must be specified or the tests will fail.
:: If BUILD_JOB is specified, the value should be the URL from which to download the GW and UI APKs (e.g. a buildserver job workspace). 
:: If not specified, the locally-built APKs are used instead
:: The APK build job must have any spaces in the URL written as %%20. This allows escaping the % to then use the HTML code for a space (%20) since a literal space is the arguemnt separator

@Echo off

setlocal enabledelayedexpansion

set TABLET_SERIAL=""
set SERVER_NAME=""
set TEST_PACKAGE=""
set ANDROID_VERSION=""
set BUILD_JOB=""
set TEST_RUN_ID=""

set argCount=0

set TEST_RAIL_STRING=
set CMD_LINE_SERIAL_STRING=
set GRADLE_SERIAL_STRING=

for %%x in (%*) do (
    set /A argCount+=1
   
    for /F "tokens=1,2 delims=:" %%a in ("%%x") do (
	
		:: Check the argument name against expected options
		if %%a==TABLET_SERIAL (
		
			set TABLET_SERIAL=%%b
			
			echo TABLET_SERIAL is !TABLET_SERIAL!
			
		) else if %%a==SERVER_NAME (
		
			set SERVER_NAME=%%b
			
			echo SERVER_NAME is !SERVER_NAME!
			
		) else if %%a==TEST_PACKAGE (
		
			set TEST_PACKAGE=%%b
			
			echo TEST_PACKAGE is !TEST_PACKAGE!
		
		) else if %%a==ANDROID_VERSION (
		
			set ANDROID_VERSION=%%b
			
			echo ANDROID_VERSION is !ANDROID_VERSION!
		
		) else if %%a==BUILD_JOB (
		
			set BUILD_JOB=%%b
			
			echo BUILD_JOB is !BUILD_JOB!
		
		) else if %%a==TEST_RUN_ID (
		
			set TEST_RUN_ID=%%b
			
			echo TEST_RUN_ID is !TEST_RUN_ID!
			
		) else (
			echo unknown argument
		)

	)

    set "argVec[!argCount!]=%%~x"
)

echo Number of processed arguments: %argCount%

:: Set strings for tablet serial number args - used if there's more than one tablet connected to the PC
if !TABLET_SERIAL!=="" (
	echo No tablet serial number set
) else (
	set CMD_LINE_SERIAL_STRING=-s !TABLET_SERIAL! 
	set GRADLE_SERIAL_STRING=-PtabletSerialNumber=\"!TABLET_SERIAL!\" 
	
)



:: If not specified, set default values
if !SERVER_NAME!=="" (
    set SERVER_NAME=Muffin
	echo Using default server !SERVER_NAME!

)

if !TEST_PACKAGE!=="" (
    set TEST_PACKAGE=tablet_and_server
	echo Using default test package !TEST_PACKAGE!

)

if !ANDROID_VERSION!=="" (
    set ANDROID_VERSION=8
		echo Using default Android version !ANDROID_VERSION!

)


:: If no build job specified, shouldn't try to download it
if !BUILD_JOB!=="" (
    set GET_REMOTE_APKS=false
) else (
    set GET_REMOTE_APKS=true
)

:: If using test run ID set string to add to command
if !TEST_RUN_ID!=="" (
	echo.
	echo No TestRail ID specified
	echo.
) else (

	set !TEST_RAIL_STRING!=-PtestRunId=!TEST_RUN_ID! 
	echo.
	echo.
	echo Auto-update of TestRail enabled, using !TEST_RAIL_STRING!as ID
	echo.
	echo.
)

echo.
echo.
echo GET_REMOTE_APKS = !GET_REMOTE_APKS!
echo ANDROID_VERSION = !ANDROID_VERSION!
echo SERVER_NAME = !SERVER_NAME!
echo.
echo.
echo Starting Build
echo.
echo.


::build the (non-appium) apps and run the unit tests
call gradlew clean build test check -x lint

::uninstall any existing apps.
D:\Android\sdk\platform-tools\adb !CMD_LINE_SERIAL_STRING!uninstall com.isansys.patientgateway
D:\Android\sdk\platform-tools\adb !CMD_LINE_SERIAL_STRING!uninstall com.isansys.pse_isansysportal

:: Close android settings if it is open
D:\Android\sdk\platform-tools\adb !CMD_LINE_SERIAL_STRING!shell pm clear com.android.settings

::Run the connected tests - i.e. instrumented unit tests
:: SKIP THIS FOR NOW
::call gradlew Gateway:connectedAndroidTest
:: If tests failed, fail job immediately
::if !ERRORLEVEL! NEQ 0 (
::    exit /b 1
::)

if !GET_REMOTE_APKS!==true (
	goto download_apks
) else (
	goto use_local_apks
)



:download_apks

echo.
echo.
echo downloading apks from !BUILD_JOB!
echo.
echo.

:: Pull down the APKs from the specified build job
set GW_FILEPATH='http://buildserver/job/!BUILD_JOB!/ws/PatientGateway.apk'
set UI_FILEPATH='http://buildserver/job/!BUILD_JOB!/ws/UserInterface.apk'

powershell (new-object System.Net.WebClient).DownloadFile(!GW_FILEPATH!, 'PatientGateway.apk')
powershell (new-object System.Net.WebClient).DownloadFile(!UI_FILEPATH!, 'UserInterface.apk')

goto install_gateway



:use_local_apks
echo.
echo.
echo using local apks
echo.
echo.

::copy the previously built apps

echo f|xcopy .\Gateway\build\outputs\apk\release\Gateway-release.apk .\PatientGateway.apk /y
echo f|xcopy .\User_Interface\build\outputs\apk\release\User_Interface-release.apk .\UserInterface.apk /y



:install_gateway
echo.
echo.
echo installing gateway app and allow permissions
echo.
echo.

::install the gateway app
D:\Android\sdk\platform-tools\adb !CMD_LINE_SERIAL_STRING!install .\PatientGateway.apk

:: allow GW permissions

echo GW PERMISSIONS CMD is: gradlew -PappiumTests -PtestServerName=\"!SERVER_NAME!\" !GRADLE_SERIAL_STRING!:User_Interface:testDebugUnitTest --tests *.GatewayPermissions.*

call gradlew -PappiumTests -PtestServerName=\"!SERVER_NAME!\" !GRADLE_SERIAL_STRING!:User_Interface:testDebugUnitTest --tests *.GatewayPermissions.*

:: If installation failed, fail job immediately
if !ERRORLEVEL! NEQ 0 (
    exit /b 1
)

echo.
echo.
echo installing UI app
echo.
echo.

D:\Android\sdk\platform-tools\adb !CMD_LINE_SERIAL_STRING!install .\UserInterface.apk


:: Launch Gateway
echo.
echo.
echo launching gateway
echo.
echo.
::ensure patient gateway app is running
D:\Android\sdk\platform-tools\adb !CMD_LINE_SERIAL_STRING!shell monkey -p com.isansys.patientgateway -c android.intent.category.LAUNCHER 1

:: allow UI permissions


echo.
echo.
echo Allow UI permissions
echo.
echo.

call gradlew -PappiumTests -PtestServerName=\"!SERVER_NAME!\" !GRADLE_SERIAL_STRING!:User_Interface:testDebugUnitTest --tests *.UserInterfacePermissions.*



:: If installation failed, fail job immediately
if !ERRORLEVEL! NEQ 0 (
    exit /b 1
)



::invoke gradle wrapper - installation process page only, to ensure tablet is set up and talking to a gateway
call gradlew -PappiumTests -PtestServerName=\"!SERVER_NAME!\" !GRADLE_SERIAL_STRING!:User_Interface:testDebugUnitTest --tests *.installationProcessPage

:: If installation failed, fail job immediately
if !ERRORLEVEL! NEQ 0 (
    exit /b 1
)

::Restart the gateway app just in case
D:\Android\sdk\platform-tools\adb !CMD_LINE_SERIAL_STRING!shell monkey -p com.isansys.patientgateway -c android.intent.category.LAUNCHER 1

::invoke gradle wrapper - all tests from tablet and server folder
call gradlew -PappiumTests -PtestServerName=\"!SERVER_NAME!\" !GRADLE_SERIAL_STRING!-PandroidVersion=!ANDROID_VERSION! !TEST_RAIL_STRING!:User_Interface:testDebugUnitTest --tests *.appiumtests.!TEST_PACKAGE!.*

set TEST_RESULT_ERROR_LEVEL=!ERRORLEVEL!

::Copy the remaining test reports to the workspace root
echo d|xcopy .\User_Interface\build\reports\tests\testDebugUnitTest .\AppiumTestReports /s /e /y

if !TEST_RESULT_ERROR_LEVEL! NEQ 0 (
    exit /b 1
)
