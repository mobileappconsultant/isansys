package com.isansys.pse_isansysportal;

public class FragmentGraphManuallyEnteredBlocks extends FragmentGraphManuallyEntered
{
    @Override
    public void onResume()
    {
        show_as_block = true;

        super.onResume();

        super.setupForNoYAxisVitalSign();
    }
}
