package com.isansys.pse_isansysportal;

import com.isansys.pse_isansysportal.enums.AnnotationEntryType;

public class AnnotationBeingEntered
{
    private AnnotationEntryType annotation_entry_type = AnnotationEntryType.NOT_SET_YET;

    private long timestamp_in_ms;

    private String keyboard_annotation = "";

    private String conditions = "";
    private String actions = "";
    private String outcomes = "";


    public void setAnnotationEntryType(AnnotationEntryType annotation_entry_type)
    {
        this.annotation_entry_type = annotation_entry_type;
    }


    public AnnotationEntryType getAnnotationEntryType()
    {
        return annotation_entry_type;
    }


    public void setConditions(String conditions)
    {
        this.conditions = conditions;
    }


    public String getConditions()
    {
        return conditions;
    }


    public void setActions(String actions)
    {
        this.actions = actions;
    }


    public String getActions()
    {
        return actions;
    }


    public void setOutcomes(String outcomes)
    {
        this.outcomes = outcomes;
    }


    public String getOutcomes()
    {
        return outcomes;
    }


    public void setKeyboardAnnotation(String keyboard_annotation)
    {
        this.keyboard_annotation = keyboard_annotation;
    }


    public String getAnnotation()
    {
        switch (getAnnotationEntryType())
        {
            case CUSTOM_VIA_ONSCREEN_KEYBOARD:
            {
                return keyboard_annotation;
            }

            case PREDEFINED_FROM_SERVER:
            {
                String separator = " : ";
                return conditions.concat(separator).concat(actions).concat(separator).concat(outcomes);
            }
        }

        return "";
    }


    public void setTimestamp(long timestamp_in_ms)
    {
        this.timestamp_in_ms = timestamp_in_ms;
    }


    public long getTimestamp()
    {
        return timestamp_in_ms;
    }
}
