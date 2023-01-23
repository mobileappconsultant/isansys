package com.isansys.pse_isansysportal;

import java.util.ArrayList;

public class ServerConfigurableTextStrings
{
    private final ArrayList<AnnotationDescriptor> list_annotation_conditions = new ArrayList<>();
    private final ArrayList<AnnotationDescriptor> list_annotation_actions = new ArrayList<>();
    private final ArrayList<AnnotationDescriptor> list_annotation_outcomes = new ArrayList<>();

    public boolean isEmpty()
    {
        return list_annotation_conditions.isEmpty() && list_annotation_actions.isEmpty() && list_annotation_outcomes.isEmpty();
    }

    public void clear()
    {
        list_annotation_conditions.clear();
        list_annotation_actions.clear();
        list_annotation_outcomes.clear();
    }

    public void addAnnotationCondition(AnnotationDescriptor annotation_descriptor)
    {
        list_annotation_conditions.add(annotation_descriptor);
    }

    public void addAnnotationAction(AnnotationDescriptor annotation_descriptor)
    {
        list_annotation_actions.add(annotation_descriptor);
    }

    public void addAnnotationOutcome(AnnotationDescriptor annotation_descriptor)
    {
        list_annotation_outcomes.add(annotation_descriptor);
    }

    public ArrayList<AnnotationDescriptor> getAnnotationConditions()
    {
        return list_annotation_conditions;
    }

    public ArrayList<AnnotationDescriptor> getAnnotationActions()
    {
        return list_annotation_actions;
    }

    public ArrayList<AnnotationDescriptor> getAnnotationOutcomes()
    {
        return list_annotation_outcomes;
    }
}
