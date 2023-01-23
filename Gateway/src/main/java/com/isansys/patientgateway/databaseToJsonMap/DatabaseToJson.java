package com.isansys.patientgateway.databaseToJsonMap;

import com.isansys.common.enums.HttpOperationType;
import com.isansys.patientgateway.database.TableBloodPressureBattery;
import com.isansys.patientgateway.database.TableBloodPressureMeasurement;
import com.isansys.patientgateway.database.TableLifetempBattery;
import com.isansys.patientgateway.database.TableLifetempMeasurement;
import com.isansys.patientgateway.database.TableLifetouchBattery;
import com.isansys.patientgateway.database.TableLifetouchHeartBeat;
import com.isansys.patientgateway.database.TableLifetouchHeartRate;
import com.isansys.patientgateway.database.TableLifetouchRawAccelerometerModeSample;
import com.isansys.patientgateway.database.TableLifetouchRespirationRate;
import com.isansys.patientgateway.database.TableLifetouchSetupModeRawSample;
import com.isansys.patientgateway.database.TableManuallyEnteredAnnotation;
import com.isansys.patientgateway.database.TableManuallyEnteredBloodPressure;
import com.isansys.patientgateway.database.TableManuallyEnteredCapillaryRefillTime;
import com.isansys.patientgateway.database.TableManuallyEnteredConsciousnessLevel;
import com.isansys.patientgateway.database.TableManuallyEnteredFamilyOrNurseConcern;
import com.isansys.patientgateway.database.TableManuallyEnteredHeartRate;
import com.isansys.patientgateway.database.TableManuallyEnteredRespirationDistress;
import com.isansys.patientgateway.database.TableManuallyEnteredRespirationRate;
import com.isansys.patientgateway.database.TableManuallyEnteredSpO2;
import com.isansys.patientgateway.database.TableManuallyEnteredSupplementalOxygenLevel;
import com.isansys.patientgateway.database.TableManuallyEnteredTemperature;
import com.isansys.patientgateway.database.TableManuallyEnteredUrineOutput;
import com.isansys.patientgateway.database.TableManuallyEnteredWeight;
import com.isansys.patientgateway.database.TableOximeterBattery;
import com.isansys.patientgateway.database.TableOximeterIntermediateMeasurement;
import com.isansys.patientgateway.database.TableOximeterMeasurement;
import com.isansys.patientgateway.database.TableOximeterSetupModeRawSample;
import com.isansys.patientgateway.database.TableWeightScaleBattery;
import com.isansys.patientgateway.database.TableWeightScaleWeight;

import java.util.ArrayList;
import java.util.List;


public class DatabaseToJson
{
    public final String database_name;
    public final String json_name;

    public DatabaseToJson(String database_name, String json_name)
    {
        this.database_name = database_name;
        this.json_name = json_name;
    }


    public static List<DatabaseToJson> getMeasurementSpecificInfo(HttpOperationType operation_type)
    {
        List<DatabaseToJson> measurement_specific_database_table_and_json_names = new ArrayList<>();

        switch(operation_type)
        {
            // Non-measurement upload points - handled separately
            case INVALID:
            case PATIENT_DETAILS:
            case DEVICE_INFO:
            case START_PATIENT_SESSION:
            case START_DEVICE_SESSION:
            case END_DEVICE_SESSION:
            case END_PATIENT_SESSION:
            case AUDITABLE_EVENTS:
            case PATIENT_SESSION_FULLY_SYNCED:
            case SETUP_MODE_LOG:
            case EARLY_WARNING_SCORES:
            case CONNECTION_EVENT:
                break;



            case LIFETOUCH_HEART_RATE:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableLifetouchHeartRate.COLUMN_HEART_RATE, "HeartRate"));
            }
            break;

            case LIFETOUCH_HEART_BEAT:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableLifetouchHeartBeat.COLUMN_AMPLITUDE, "Amplitude"));
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableLifetouchHeartBeat.COLUMN_ACTIVITY_LEVEL, "ActivityLevel"));
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableLifetouchHeartBeat.COLUMN_RR_INTERVAL, "RrInterval"));

            }
            break;

            case LIFETOUCH_RESPIRATION_RATE:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableLifetouchRespirationRate.COLUMN_RESPIRATION_RATE, "RespirationRate"));

            }
            break;

            case LIFETOUCH_SETUP_MODE_SAMPLE:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableLifetouchSetupModeRawSample.COLUMN_SAMPLE_VALUE, "SetupMode"));
            }
            break;

            case LIFETOUCH_BATTERY:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableLifetouchBattery.COLUMN_BATTERY_PERCENTAGE, "Value"));
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableLifetouchBattery.COLUMN_BATTERY_MILLIVOLTS, "ValueInMillivolts"));
            }
            break;

            case LIFETOUCH_PATIENT_ORIENTATION:
            {
                // handled separately
            }
            break;

            case LIFETOUCH_RAW_ACCELEROMETER_MODE_SAMPLE:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableLifetouchRawAccelerometerModeSample.COLUMN_X_SAMPLE_VALUE, "XAxisSample"));
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableLifetouchRawAccelerometerModeSample.COLUMN_Y_SAMPLE_VALUE, "YAxisSample"));
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableLifetouchRawAccelerometerModeSample.COLUMN_Z_SAMPLE_VALUE, "ZAxisSample"));

            }
            break;




            case LIFETEMP_TEMPERATURE:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableLifetempMeasurement.COLUMN_TEMPERATURE, "Temperature"));
            }
            break;

            case LIFETEMP_BATTERY:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableLifetempBattery.COLUMN_BATTERY_PERCENTAGE, "Value"));
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableLifetempBattery.COLUMN_BATTERY_MILLIVOLTS, "ValueInMillivolts"));
            }
            break;





            case PULSE_OX_MEASUREMENT:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableOximeterMeasurement.COLUMN_PULSE, "Pulse"));
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableOximeterMeasurement.COLUMN_SPO2, "SpO2"));
            }
            break;

            case PULSE_OX_INTERMEDIATE_MEASUREMENT:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableOximeterIntermediateMeasurement.COLUMN_PULSE, "Pulse"));
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableOximeterIntermediateMeasurement.COLUMN_SPO2, "SpO2"));
            }
            break;

            case PULSE_OX_SETUP_MODE_SAMPLE:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableOximeterSetupModeRawSample.COLUMN_SAMPLE_VALUE, "SetupMode"));
            }
            break;

            case PULSE_OX_BATTERY:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableOximeterBattery.COLUMN_BATTERY_PERCENTAGE, "Value"));
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableOximeterBattery.COLUMN_BATTERY_MILLIVOLTS, "ValueInMillivolts"));
            }
            break;




            case BLOOD_PRESSURE_MEASUREMENT:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableBloodPressureMeasurement.COLUMN_PULSE, "Pulse"));
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableBloodPressureMeasurement.COLUMN_SYSTOLIC, "Systolic"));
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableBloodPressureMeasurement.COLUMN_DIASTOLIC, "Diastolic"));
            }
            break;

            case BLOOD_PRESSURE_BATTERY:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableBloodPressureBattery.COLUMN_BATTERY, "Value"));
            }
            break;




            case WEIGHT_SCALE_MEASUREMENT:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableWeightScaleWeight.COLUMN_WEIGHT, "Weight"));
            }
            break;

            case WEIGHT_SCALE_BATTERY:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableWeightScaleBattery.COLUMN_BATTERY_PERCENTAGE, "Value"));
            }
            break;




            case MANUALLY_ENTERED_HEART_RATES:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableManuallyEnteredHeartRate.COLUMN_HEART_RATE, "HeartRate"));
            }
            break;

            case MANUALLY_ENTERED_RESPIRATION_RATES:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableManuallyEnteredRespirationRate.COLUMN_RESPIRATION_RATE, "RespirationRate"));
            }
            break;

            case MANUALLY_ENTERED_TEMPERATURES:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableManuallyEnteredTemperature.COLUMN_TEMPERATURE, "Temperature"));
            }
            break;

            case MANUALLY_ENTERED_SPO2_MEASUREMENTS:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableManuallyEnteredSpO2.COLUMN_SPO2, "SpO2"));
            }
            break;

            case MANUALLY_ENTERED_BLOOD_PRESSURE_MEASUREMENTS:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableManuallyEnteredBloodPressure.COLUMN_SYSTOLIC, "Systolic"));
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableManuallyEnteredBloodPressure.COLUMN_DIASTOLIC, "Diastolic"));
            }
            break;

            case MANUALLY_ENTERED_WEIGHT_MEASUREMENTS:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableManuallyEnteredWeight.COLUMN_WEIGHT, "Weight"));
            }
            break;

            case MANUALLY_ENTERED_CONSCIOUSNESS_LEVELS:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableManuallyEnteredConsciousnessLevel.COLUMN_CONSCIOUSNESS_LEVEL, "Value"));
            }
            break;

            case MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN_LEVELS:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableManuallyEnteredSupplementalOxygenLevel.COLUMN_SUPPLEMENTAL_OXYGEN_LEVEL, "Value"));
            }
            break;

            case MANUALLY_ENTERED_ANNOTATIONS:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableManuallyEnteredAnnotation.COLUMN_ANNOTATION_TEXT, "AnnotationText"));
            }
            break;

            case MANUALLY_ENTERED_CAPILLARY_REFILL_TIME:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableManuallyEnteredCapillaryRefillTime.COLUMN_CAPILLARY_REFILL_TIME, "CapillaryRefillTime"));
            }
            break;

            case MANUALLY_ENTERED_RESPIRATION_DISTRESS:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableManuallyEnteredRespirationDistress.COLUMN_RESPIRATION_DISTRESS, "RespirationDistress"));
            }
            break;

            case MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableManuallyEnteredFamilyOrNurseConcern.COLUMN_CONCERN, "FamilyOrNurseConcern"));
            }
            break;

            case MANUALLY_ENTERED_URINE_OUTPUT:
            {
                measurement_specific_database_table_and_json_names.add(new DatabaseToJson(TableManuallyEnteredUrineOutput.COLUMN_URINE_OUTPUT, "UrineOutput"));
            }
            break;
        }

        return measurement_specific_database_table_and_json_names;
    }
}
