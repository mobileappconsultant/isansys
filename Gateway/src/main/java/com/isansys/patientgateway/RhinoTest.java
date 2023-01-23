package com.isansys.patientgateway;

/*
Need to add the below the the gradle file

    // https://mvnrepository.com/artifact/org.mozilla/rhino
    implementation group: 'org.mozilla', name: 'rhino', version: '1.7.13'



import android.content.Context;
import android.util.Log;

import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

public class RhinoTest
{
    private static Context context;


    public RhinoTest(Context context)
    {
        RhinoTest.context = context;
    }


    public static void runTest()
    {
        try
        {
            String response = Utils.convertStreamToString(context.getAssets().open("test_javascript.js"));

            runScript(response);
        }
        catch (java.io.IOException e)
        {

        }
    }


    private static void runScript(String javascript_code)
    {
        org.mozilla.javascript.Context rhino = org.mozilla.javascript.Context.enter();
        rhino.setOptimizationLevel(-1);

        try
        {
            Scriptable scope = rhino.initStandardObjects();

            rhino.evaluateString(scope, javascript_code, "ScriptAPI", 1, null);

            Function function = (Function) scope.get("checkForAlarmConditions", scope);
            Object[] functionParams = new Object[] {new RhinoInterface(), 1, "another test parameter"};

            Object jsResult = function.call(rhino, scope, scope, functionParams);

            String output = org.mozilla.javascript.Context.toString(jsResult);
            Log.e("MOO", "output = " + output);
        }
        finally
        {
            org.mozilla.javascript.Context.exit();
        }
    }
}
*/