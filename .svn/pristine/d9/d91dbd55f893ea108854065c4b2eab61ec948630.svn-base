package com.isansys.patientgateway.serverlink;

import android.util.Log;

import com.isansys.patientgateway.AES;
import com.isansys.patientgateway.serverlink.webservices.IsansysAuthentication;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/* Code from https://futurestud.io/tutorials/retrofit-2-creating-a-sustainable-android-client */

public class ServiceGenerator
{
    private final String TAG = "ServiceGenerator";

    private String apiBaseUrl = "http://192.168.1.198:85/PatientMeasurementDatabase/json/reply/";

    private boolean use_encryption = true;
    private boolean use_authentication = true;

    private Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create());

    //private static final HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private final HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC);

    private final IsansysEncryptionInterceptor isansysEncryptionInterceptor = new IsansysEncryptionInterceptor();
    private final IsansysAuthenticationInterceptor isansysAuthenticationInterceptor = new IsansysAuthenticationInterceptor();

    private final IsansysAuthentication auth;

    public ServiceGenerator(IsansysAuthentication authentication)
    {
        auth = authentication;
    }

    /*  Encrypted Message objects  */
    @SuppressWarnings("unused")
    public static class IsansysEncryptedMessage
    {
        public String EncryptedBody;
        public String EncryptedSymmetricKey;
        public String KeyId;
        public String MethodName;
        public int EncryptionType;
    }


    private class IsansysEncryptionInterceptor implements Interceptor
    {
        @Override public Response intercept(Interceptor.Chain chain) throws IOException
        {
            Request request = chain.request();

            long t1 = System.nanoTime();

            // Read the intercepted request body and convert into a String
            RequestBody request_body = request.body();

            // If uploading the log files, then they are sent as "files" and NOT json, so do NOT encrypt them here as this code will try and convert to Base64 strings
            boolean use_encryption = true;

            if (request_body.contentType() != null)
            {
                if (request_body.contentType().type().equals("multipart"))
                {
                    use_encryption = false;
                }
            }

            // Get the full URL for the Webservice that was being called (e.g. http://192.168.1.199/PatientMeasurementDatabase/json/reply/LifetouchHeartBeatADDMultiple)
            String webservice_address = request.url().toString();

            // Get the webservice name only (e.g. LifetouchHeartBeatADDMultiple)
            String webservice_name = webservice_address.substring(webservice_address.lastIndexOf("/") + 1);

            if (use_encryption)
            {
                try
                {
                    Buffer buffer = new Buffer();
                    request_body.writeTo(buffer);
                    String original_body = buffer.readUtf8();

                    // Make a new Isansys Encrypted message object
                    IsansysEncryptedMessage encrypted_message = new IsansysEncryptedMessage();

                    // Encrypt the plaintext JSON and add to Isansys Encrypted Message object
                    if (!original_body.isEmpty())
                    {
                        encrypted_message.EncryptedBody = AES.encryptCBSWithPadding(original_body);
                    }

                    // Tell the Server what Encryption method was used
                    encrypted_message.EncryptionType = AES.OverTheWireEncryptionType.CBS_WITH_PKCS5PADDING.ordinal();

                    // Tell the Server where the decrypted data should go to
                    encrypted_message.MethodName = webservice_name;

                    // Convert the Isansys Encrypted Message object to JSON
                    String new_body = new ObjectMapper().writeValueAsString(encrypted_message);

                    // Replace the webservice name with Isansys Encrypted Message (e.g. http://192.168.1.199/PatientMeasurementDatabase/json/reply/IsansysEncryptedMessage)
                    webservice_address = webservice_address.replaceAll(webservice_name, "IsansysEncryptedMessage");

                    Log.e(TAG, String.format("Sending request %s", request.url()));
                    Log.e(TAG, "new_body = " + new_body);

                    // Build up a new request with the new URL and Body
                    HttpUrl.Builder urlBuilder = HttpUrl.parse(webservice_address).newBuilder();

                    MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");

                    RequestBody body = RequestBody.create(new_body, mediaType);

                    request = request.newBuilder()
                            .url(urlBuilder.build())
                            .header("Content-Type", body.contentType().toString())
                            .header("Content-Length", String.valueOf(body.contentLength()))
                            .method(request.method(), body)
                            .build();
                }
                catch (JsonGenerationException
                        | InvalidKeyException
                        | NoSuchAlgorithmException
                        | NoSuchPaddingException
                        | InvalidAlgorithmParameterException
                        | IllegalBlockSizeException
                        | BadPaddingException e1)
                {
                    Log.d(TAG, "Failed to create IsansysEncryptedMessage. " );
                }
            }

            // Do the actual webservice request
            Response response = chain.proceed(request);

            if (use_encryption)
            {
                // Get the Encrypted Body from the Response
                String encrypted_response_body = response.body().string();
                Log.e(TAG, "Rx'ed encrypted_response_body = " + encrypted_response_body);

                // Process Encrypted Response.  Will try to detect an Encrypted Response, if not one, will return the original.
                String plaintext_response_body = unpackEncryptedResponse(encrypted_response_body);

                long t2 = System.nanoTime();
                long time_taken = (long)((t2 - t1) / 1e6d);
                Log.e(TAG, "Received response for " + response.request().url() + " (" + webservice_name + ") : in " + time_taken + "ms");

                // Build up a new "Response" object to return with the plaintext response
                MediaType contentType = response.body().contentType();
                ResponseBody body = ResponseBody.create(plaintext_response_body, contentType);

                return response.newBuilder().body(body).build();
            }
            else
            {
                return response;
            }
        }
    }


    private class IsansysAuthenticationInterceptor implements Interceptor
    {
        @Override
        public Response intercept(Chain chain) throws IOException
        {
            Request request = chain.request();

            final String username = auth.username();
            final String password = auth.password(request.url() + username);

            String credential = Credentials.basic(username, password);
            request = request.newBuilder().header("Authorization", credential).build();

            // Do the actual webservice request
            Response response = chain.proceed(request);

            return response;
        }
    }


    private String unpackEncryptedResponse(String msg)
    {
        String localJson = msg;

        try
        {
            JSONObject json_object = new JSONObject(localJson);
            String encrypted_body = json_object.getString("EncryptedBody");
            // We don't use the Encryption Type yet, but it is here when we want to extend the functionality.
            //AES.OverTheWireEncryptionType encryption_type = AES.OverTheWireEncryptionType.values()[Integer.parseInt(json_object.getString("EncryptionType"))];
            Log.d(TAG, "Encrypted response detected. Extracting....");
            localJson = AES.decryptCBSWithPadding(encrypted_body);
        }
        catch (Exception e)
        {
            Log.d(TAG, "ServiceGenerator No EncryptedMessage, so assume Not Encrypted");
        }

        return localJson;
    }


    public void changeApiBaseUrl(String newApiBaseUrl, boolean useHttps)
    {
        Log.d(TAG, "changeApiBaseUrl : newApiBaseUrl = " + newApiBaseUrl + ". useHttps = " + useHttps);

        if (useHttps)
        {
            apiBaseUrl = "https://"  + newApiBaseUrl + "/PatientMeasurementDatabase/json/reply/";
        }
        else
        {
            apiBaseUrl = "http://"  + newApiBaseUrl + "/PatientMeasurementDatabase/json/reply/";
        }

        Log.d(TAG, "changeApiBaseUrl : Setting apiBaseUrl to " + apiBaseUrl);

        builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(apiBaseUrl);
    }


    public void useEncryption(boolean desired_use_encryption)
    {
        use_encryption = desired_use_encryption;
    }


    public void useAuthentication(boolean desired_use_authentication)
    {
        use_authentication = desired_use_authentication;
    }


    public <S> S createService(Class<S> serviceClass)
    {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        if (use_encryption)
        {
            httpClient.addInterceptor(isansysEncryptionInterceptor);
        }

        if(use_authentication)
        {
            httpClient.addInterceptor(isansysAuthenticationInterceptor);
        }

        httpClient.addInterceptor(logging);

        builder.client(httpClient.build());
        Retrofit retrofit = builder.build();
        return retrofit.create(serviceClass);
    }
}
