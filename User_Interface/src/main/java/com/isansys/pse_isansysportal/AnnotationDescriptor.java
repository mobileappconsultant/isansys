package com.isansys.pse_isansysportal;

class AnnotationDescriptor
{
    public final String name;
    private final int android_database_id;
    private final int server_database_id;

    public AnnotationDescriptor(String name, int android_database_id, int server_database_id)
    {
        this.name = name;
        this.android_database_id = android_database_id;
        this.server_database_id = server_database_id;
    }
}
