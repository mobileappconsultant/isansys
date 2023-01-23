function checkForAlarmConditions(java_interface, example_parameter_a, example_parameter_b)
{
    var heart_rate = java_interface.getHeartRate();
    var respiration_rate = java_interface.getRespirationRate();
    return heart_rate + example_parameter_a + heart_rate;
}