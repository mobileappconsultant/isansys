package com.isansys.pse_isansysportal;

public class ManualVitalSignBeingEntered
{
    private final int vital_sign_id;
    private final String human_readable_name;

    private int button_id;
    private String value;

    private final int ews_score;

    public ManualVitalSignBeingEntered(int vital_sign_id, String human_readable_name, String value, int ews_score)
    {
        this.vital_sign_id = vital_sign_id;
        this.human_readable_name = human_readable_name;
        this.ews_score = ews_score;

        this.value = value;
    }


    public ManualVitalSignBeingEntered(int vital_sign_id, String human_readable_name, int button_id, int ews_score)
    {
        this.vital_sign_id = vital_sign_id;
        this.human_readable_name = human_readable_name;
        this.ews_score = ews_score;

        this.button_id = button_id;
    }


    public int getVitalSignId()
    {
        return this.vital_sign_id;
    }

    public String getValue()
    {
        return this.value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public int getButtonId()
    {
        return this.button_id;
    }

    public String getHumanReadableName()
    {
        return this.human_readable_name;
    }

    public int getEwsScore()
    {
        return this.ews_score;
    }
}
