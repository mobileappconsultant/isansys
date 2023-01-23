package com.isansys.pse_isansysportal;

import android.os.CountDownTimer;

class PopupWifiStatusCountDownTimer extends CountDownTimer
{
    private MainActivityInterface mainActivityInterface;

    public PopupWifiStatusCountDownTimer(long millisInFuture, long countDownInterval, MainActivityInterface mainActivityInterface)
    {
        super(millisInFuture, countDownInterval);

        this.mainActivityInterface = mainActivityInterface;
    }

    @Override
    public void onTick(long l)
    {
        // Timer tick every second
    }

    @Override
    public void onFinish()
    {
        // Dismiss the wifi status Popup view
        mainActivityInterface.dismissWifiPopupIfVisible();
    }
}
