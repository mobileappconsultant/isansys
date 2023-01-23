package com.isansys.patientgateway.tabletBattery;

import java.util.TreeMap;

/**
 * Class to add TreeMap for Tablet voltage to percentage
 * Created by Sushant on 20/05/2016.
 */
class BatteryVoltageToPercentageTable
{
    public final TreeMap<Integer, Integer> voltage_to_percentage_map = new TreeMap<>();

    public BatteryVoltageToPercentageTable()
    {
        populateVoltageToPercentageMap();
    }


    /**
     * Formula to calculate this list is y = -0.0000251614x4 + 0.0057754381x3 - 0.3917288660x2 + 13.4595831442x + 3,542.5416137794
     * Where X-axis is battery estimated_percentage and Y-axis is battery voltage
     * TabletBatteryInterpreter uses this tablet to estimate correct percentage.
     * Tablet below is obtained from the voltage-percentage graph constructed in Excel file.
     * Excel file is added to the root directory of SVN. File name is "Battery_voltage_percentage_map.xlsx"
     */
    private void populateVoltageToPercentageMap()
    {
        voltage_to_percentage_map.put(3543,0);
        voltage_to_percentage_map.put(3556,1);
        voltage_to_percentage_map.put(3568,2);
        voltage_to_percentage_map.put(3580,3);
        voltage_to_percentage_map.put(3590,4);
        voltage_to_percentage_map.put(3601,5);
        voltage_to_percentage_map.put(3610,6);
        voltage_to_percentage_map.put(3619,7);
        voltage_to_percentage_map.put(3628,8);
        voltage_to_percentage_map.put(3636,9);
        voltage_to_percentage_map.put(3643,10);
        voltage_to_percentage_map.put(3651,11);
        voltage_to_percentage_map.put(3657,12);
        voltage_to_percentage_map.put(3663,13);
        voltage_to_percentage_map.put(3669,14);
        voltage_to_percentage_map.put(3675,15);
        voltage_to_percentage_map.put(3680,16);
        voltage_to_percentage_map.put(3684,17);
        voltage_to_percentage_map.put(3689,18);
        voltage_to_percentage_map.put(3693,19);
        voltage_to_percentage_map.put(3697,20);
        voltage_to_percentage_map.put(3701,21);
        voltage_to_percentage_map.put(3705,22);
        voltage_to_percentage_map.put(3708,23);
        voltage_to_percentage_map.put(3711,24);
        voltage_to_percentage_map.put(3715,25);
        voltage_to_percentage_map.put(3718,26);
        voltage_to_percentage_map.put(3721,27);
        voltage_to_percentage_map.put(3724,28);
        voltage_to_percentage_map.put(3726,29);
        voltage_to_percentage_map.put(3729,30);
        voltage_to_percentage_map.put(3732,31);
        voltage_to_percentage_map.put(3735,32);
        voltage_to_percentage_map.put(3738,33);
        voltage_to_percentage_map.put(3741,34);
        voltage_to_percentage_map.put(3744,35);
        voltage_to_percentage_map.put(3747,36);
        voltage_to_percentage_map.put(3750,37);
        voltage_to_percentage_map.put(3753,38);
        voltage_to_percentage_map.put(3756,39);
        voltage_to_percentage_map.put(3759,40);
        voltage_to_percentage_map.put(3763,41);
        voltage_to_percentage_map.put(3766,42);
        voltage_to_percentage_map.put(3770,43);
        voltage_to_percentage_map.put(3774,44);
        voltage_to_percentage_map.put(3778,45);
        voltage_to_percentage_map.put(3782,46);
        voltage_to_percentage_map.put(3787,47);
        voltage_to_percentage_map.put(3791,48);
        voltage_to_percentage_map.put(3796,49);
        voltage_to_percentage_map.put(3801,50);
        voltage_to_percentage_map.put(3806,51);
        voltage_to_percentage_map.put(3811,52);
        voltage_to_percentage_map.put(3817,53);
        voltage_to_percentage_map.put(3823,54);
        voltage_to_percentage_map.put(3828,55);
        voltage_to_percentage_map.put(3835,56);
        voltage_to_percentage_map.put(3841,57);
        voltage_to_percentage_map.put(3848,58);
        voltage_to_percentage_map.put(3854,59);
        voltage_to_percentage_map.put(3861,60);
        voltage_to_percentage_map.put(3868,61);
        voltage_to_percentage_map.put(3876,62);
        voltage_to_percentage_map.put(3883,63);
        voltage_to_percentage_map.put(3891,64);
        voltage_to_percentage_map.put(3899,65);
        voltage_to_percentage_map.put(3907,66);
        voltage_to_percentage_map.put(3916,67);
        voltage_to_percentage_map.put(3924,68);
        voltage_to_percentage_map.put(3933,69);
        voltage_to_percentage_map.put(3942,70);
        voltage_to_percentage_map.put(3951,71);
        voltage_to_percentage_map.put(3960,72);
        voltage_to_percentage_map.put(3970,73);
        voltage_to_percentage_map.put(3979,74);
        voltage_to_percentage_map.put(3989,75);
        voltage_to_percentage_map.put(3999,76);
        voltage_to_percentage_map.put(4009,77);
        voltage_to_percentage_map.put(4019,78);
        voltage_to_percentage_map.put(4029,79);
        voltage_to_percentage_map.put(4039,80);
        voltage_to_percentage_map.put(4049,81);
        voltage_to_percentage_map.put(4059,82);
        voltage_to_percentage_map.put(4069,83);
        voltage_to_percentage_map.put(4080,84);
        voltage_to_percentage_map.put(4090,85);
        voltage_to_percentage_map.put(4100,86);
        voltage_to_percentage_map.put(4110,87);
        voltage_to_percentage_map.put(4120,88);
        voltage_to_percentage_map.put(4130,89);
        voltage_to_percentage_map.put(4140,90);
        voltage_to_percentage_map.put(4150,91);
        voltage_to_percentage_map.put(4160,92);
        voltage_to_percentage_map.put(4170,93);
        voltage_to_percentage_map.put(4179,94);
        voltage_to_percentage_map.put(4188,95);
        voltage_to_percentage_map.put(4197,96);
        voltage_to_percentage_map.put(4206,97);
        voltage_to_percentage_map.put(4214,98);
        voltage_to_percentage_map.put(4223,99);
        voltage_to_percentage_map.put(4231,100);
    }
}
