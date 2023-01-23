package com.isansys.pse_isansysportal;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

public class PopupWebpage extends IsansysPopupDialogFragment implements WebPageButtonRecyclerViewAdapter.ItemClickListener
{
    private final String TAG = this.getClass().getSimpleName();

    String desiredUrl = "";

    LinearLayout linearLayoutLoadingWebpage;
    LinearLayout linearLayoutErrorLoadingWebpage;
    LinearLayout linearLayoutWebpageBottomButtons;

    Button buttonPopupWebpageDismiss;

    TextView textViewProblemLoadingWebpageError;

    WebView webView;

    public PopupWebpage(MainActivityInterface main_activity_interface)
    {
        super(main_activity_interface);
    }

    public interface Callback
    {
        void PopupDismissed();
        void ScreenTouched();
    }

    private Callback callback;

    private boolean loadingFinished = true;
    private boolean redirect = false;
    private boolean errorOccurred = false;

    public void setArguments(Callback callback)
    {
        this.callback = callback;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Context context = getActivity();

        final Dialog dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pop_up_webpage);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        dialog.getWindow().getDecorView().setOnTouchListener((v, event) -> {
            callback.ScreenTouched();

            v.performClick();

            return false;
        });

        buttonPopupWebpageDismiss = dialog.findViewById(R.id.buttonPopupWebpageDismiss);
        buttonPopupWebpageDismiss.setOnClickListener(v -> dismissPopupIfVisible());

        Button buttonPopupWebpageDismissSmall = dialog.findViewById(R.id.buttonPopupWebpageDismissSmall);
        buttonPopupWebpageDismissSmall.setOnClickListener(v -> dismissPopupIfVisible());

        ImageButton imageButtonBack = dialog.findViewById(R.id.imageButtonBack);
        imageButtonBack.setOnClickListener(v -> navigateBack());

        ImageButton imageButtonReload = dialog.findViewById(R.id.imageButtonReload);
        imageButtonReload.setOnClickListener(v -> reload());

        ImageButton imageButtonForward = dialog.findViewById(R.id.imageButtonForward);
        imageButtonForward.setOnClickListener(v -> navigateForward());

        linearLayoutLoadingWebpage = dialog.findViewById(R.id.linearLayoutLoadingWebpage);
        linearLayoutLoadingWebpage.setVisibility(View.GONE);

        linearLayoutErrorLoadingWebpage = dialog.findViewById(R.id.linearLayoutErrorLoadingWebpage);
        linearLayoutErrorLoadingWebpage.setVisibility(View.GONE);

        linearLayoutWebpageBottomButtons = dialog.findViewById(R.id.linearLayoutWebpageBottomButtons);
        linearLayoutWebpageBottomButtons.setVisibility(View.GONE);

        textViewProblemLoadingWebpageError = dialog.findViewById(R.id.textViewProblemLoadingWebpageError);

        // Setup the WebView here but keep it hidden until the user clicks on a button to select the webpage
        webView = dialog.findViewById(R.id.webView);
        webView.setVisibility(View.GONE);

        webView.setInitialScale(1);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
            {
                Log.d(TAG, "shouldOverrideUrlLoading");

                if (!loadingFinished)
                {
                    redirect = true;
                }

                loadingFinished = false;
                webView.loadUrl(request.getUrl().toString());

                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);

                Log.d(TAG, "onPageStarted : URL = " + url);

                loadingFinished = false;
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);

                Log.d(TAG, "onPageFinished");

                if (!redirect)
                {
                    hideProgress();

                    if (!errorOccurred)
                    {
                        linearLayoutWebpageBottomButtons.setVisibility(View.VISIBLE);
                        buttonPopupWebpageDismiss.setVisibility(View.GONE);
                    }

                    // Reset for next time;
                    errorOccurred = false;
                    redirect = false;
                    loadingFinished = true;
                }
                else
                {
                    redirect = false;
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
            {
                super.onReceivedError(view, request, error);

                Log.e(TAG, "onReceivedError");

                errorOccurred = true;
                hideProgress();
                showError(error);
            }

            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse)
            {
                super.onReceivedHttpError(view, request, errorResponse);

                Log.e(TAG, "onReceivedHttpError");
            }

            @Override
            public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error)
            {
                super.onReceivedSslError(view, handler, error);

                Log.e(TAG, "onReceivedSslError");

                errorOccurred = true;
                hideProgress();
                showSslError(error);
            }

            @Override
            public void onLoadResource(WebView view, String url)
            {
                super.onLoadResource(view, url);

                if (loadingFinished == false)
                {
                    showProgress();
                }

                Log.d(TAG, "onLoadResource : url = " + url);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url)
            {
                super.onPageCommitVisible(view, url);

                Log.d(TAG, "onPageCommitVisible : url = " + url);
            }
        });

        WebSettings webSettings = webView.getSettings();
//        webSettings.setAppCacheEnabled(false);
        webSettings.setJavaScriptEnabled(true);
        //webSettings.setBuiltInZoomControls(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setUseWideViewPort(true);

        // TODO https://stackoverflow.com/questions/45940861/android-8-cleartext-http-traffic-not-permitted

        switchToWebpage(desiredUrl);

        dialog.show();

        return dialog;
    }

    private void showProgress()
    {
        linearLayoutLoadingWebpage.setVisibility(View.VISIBLE);

        webView.setVisibility(View.GONE);
    }

    private void hideProgress()
    {
        linearLayoutLoadingWebpage.setVisibility(View.GONE);

        Log.d(TAG, "hideProgress errorOccurred = " + errorOccurred);

        if (!errorOccurred)
        {
            webView.setVisibility(View.VISIBLE);
        }
    }

    private void showError(WebResourceError error)
    {
        String errorMessage = error.getErrorCode() + " : " + error.getDescription();
        Log.e(TAG, "showError : " + errorMessage);

        textViewProblemLoadingWebpageError.setText(errorMessage);

        linearLayoutLoadingWebpage.setVisibility(View.GONE);
        linearLayoutErrorLoadingWebpage.setVisibility(View.VISIBLE);
    }

    private void showSslError(SslError error)
    {
        Log.e(TAG, "showSslError : " + error.toString());

        textViewProblemLoadingWebpageError.setText(error.toString());

        linearLayoutLoadingWebpage.setVisibility(View.GONE);
        linearLayoutErrorLoadingWebpage.setVisibility(View.VISIBLE);
    }

    public void dismissPopupIfVisible()
    {
        Log.d(TAG, "dismissPopupIfVisible");

        if (getDialog() != null)
        {
            if (getDialog().isShowing())
            {
                callback.PopupDismissed();

                getDialog().dismiss();
            }
        }
    }

    public void navigateBack()
    {
        Log.d(TAG, "navigateBack");

        if(webView.canGoBack())
        {
            webView.goBack();
        }
        else
        {
            Log.e(TAG, "navigateBack : Cannot go back");
        }
    }

    public void reload()
    {
        Log.d(TAG, "reload");

        webView.reload();
    }

    public void navigateForward()
    {
        Log.d(TAG, "navigateForward");

        if(webView.canGoForward())
        {
            webView.goForward();
        }
        else
        {
            Log.e(TAG, "navigateForward : Cannot go forward");
        }
    }

    @Override
    public void onDismiss(@NotNull DialogInterface dialog)
    {
        Log.d(TAG, "onDismiss");

        super.onDismiss(dialog);
    }

    @Override
    public void onItemClick(WebPageButtonDescriptor buttonInformation)
    {
        switchToWebpage(buttonInformation.url);
    }


    public void switchToWebpage(String url)
    {
        Log.d(TAG, "switchToWebpage : url = " + url);

        // WebView apparently cant display PDF's, so use Google to display them
        if (url.length() > 3)
        {
            String extension = url.substring(url.length() - 3);
            if (extension.equals("pdf"))
            {
                url = "https://docs.google.com/gview?embedded=true&url=" + url;
            }

            Log.d(TAG, "switchToWebpage : display url = " + url);

            loadingFinished = false;

            // Load the URL. The WebView call backs will handle showing the busy icon and making the web view visible
            webView.loadUrl(url);
        }
    }

    public void setUrl(String url)
    {
        desiredUrl = url;
    }
}
