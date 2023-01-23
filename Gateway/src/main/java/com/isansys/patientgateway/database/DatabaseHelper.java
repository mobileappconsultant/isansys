package com.isansys.patientgateway.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.isansys.patientgateway.PatientGatewayService;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "gateway.db";

    private static final int DATABASE_VERSION = 120;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context, String external_db)
    {
        super(context, external_db, null, DATABASE_VERSION);
    }

    private final TableLifetouchHeartRate tableLifetouchHeartRate = new TableLifetouchHeartRate();
    private final TableLifetouchRespirationRate tableLifetouchRespirationRate = new TableLifetouchRespirationRate();
    private final TableLifetouchHeartBeat tableLifetouchHeartBeat = new TableLifetouchHeartBeat();
    private final TableLifetouchSetupModeRawSample tableLifetouchSetupModeRawSample = new TableLifetouchSetupModeRawSample();
    private final TableLifetouchBattery tableLifetouchBattery = new TableLifetouchBattery();
    private final TableLifetouchPatientOrientation tableLifetouchPatientOrientation = new TableLifetouchPatientOrientation();
    private final TableLifetouchRawAccelerometerModeSample tableLifetouchRawAccelerometerModeSample = new TableLifetouchRawAccelerometerModeSample();

    private final TableLifetempMeasurement tableLifetempMeasurement = new TableLifetempMeasurement();
    private final TableLifetempBattery tableLifetempBattery = new TableLifetempBattery();

    private final TableOximeterMeasurement tableOximeterMeasurement = new TableOximeterMeasurement();
    private final TableOximeterIntermediateMeasurement tableOximeterIntermediateMeasurement = new TableOximeterIntermediateMeasurement();
    private final TableOximeterSetupModeRawSample tableOximeterSetupModeRawSample = new TableOximeterSetupModeRawSample();
    private final TableOximeterBattery tableOximeterBattery = new TableOximeterBattery();

    private final TableBloodPressureMeasurement tableBloodPressureMeasurement = new TableBloodPressureMeasurement();
    private final TableBloodPressureBattery tableBloodPressureBattery = new TableBloodPressureBattery();

    private final TableWeightScaleWeight tableWeightScaleWeight = new TableWeightScaleWeight();
    private final TableWeightScaleBattery tableWeightScaleBattery = new TableWeightScaleBattery();

    private final TableManuallyEnteredHeartRate tableManuallyEnteredHeartRate = new TableManuallyEnteredHeartRate();
    private final TableManuallyEnteredRespirationRate tableManuallyEnteredRespirationRate = new TableManuallyEnteredRespirationRate();
    private final TableManuallyEnteredTemperature tableManuallyEnteredTemperature = new TableManuallyEnteredTemperature();
    private final TableManuallyEnteredSpO2 tableManuallyEnteredSpO2 = new TableManuallyEnteredSpO2();
    private final TableManuallyEnteredBloodPressure tableManuallyEnteredBloodPressure = new TableManuallyEnteredBloodPressure();
    private final TableManuallyEnteredWeight tableManuallyEnteredWeight = new TableManuallyEnteredWeight();
    private final TableManuallyEnteredConsciousnessLevel tableManuallyEnteredConsciousnessLevel = new TableManuallyEnteredConsciousnessLevel();
    private final TableManuallyEnteredSupplementalOxygenLevel tableManuallyEnteredSupplementalOxygenLevel = new TableManuallyEnteredSupplementalOxygenLevel();
    private final TableManuallyEnteredCapillaryRefillTime tableManuallyEnteredCapillaryRefillTime = new TableManuallyEnteredCapillaryRefillTime();
    private final TableManuallyEnteredRespirationDistress tableManuallyEnteredRespirationDistress = new TableManuallyEnteredRespirationDistress();
    private final TableManuallyEnteredFamilyOrNurseConcern tableManuallyEnteredFamilyOrNurseConcern = new TableManuallyEnteredFamilyOrNurseConcern();
    private final TableManuallyEnteredUrineOutput tableManuallyEnteredUrineOutput = new TableManuallyEnteredUrineOutput();
    private final TableAuditTrail tableAuditTrail = new TableAuditTrail();

    private final TableWards tableWards = new TableWards();
    private final TableBeds tableBeds = new TableBeds();

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database)
    {
        tableLifetouchHeartRate.onCreateTable(database);
        tableLifetouchHeartBeat.onCreateTable(database);
        tableLifetouchSetupModeRawSample.onCreateTable(database);
        tableLifetouchRespirationRate.onCreateTable(database);
        tableLifetouchBattery.onCreateTable(database);
        tableLifetouchPatientOrientation.onCreateTable(database);
        tableLifetouchRawAccelerometerModeSample.onCreateTable(database);

        tableLifetempMeasurement.onCreateTable(database);
        tableLifetempBattery.onCreateTable(database);

        tableOximeterMeasurement.onCreateTable(database);
        tableOximeterIntermediateMeasurement.onCreateTable(database);
        tableOximeterSetupModeRawSample.onCreateTable(database);
        tableOximeterBattery.onCreateTable(database);
        
        tableBloodPressureMeasurement.onCreateTable(database);
        tableBloodPressureBattery.onCreateTable(database);

        tableWeightScaleWeight.onCreateTable(database);
        tableWeightScaleBattery.onCreateTable(database);

        TablePatientDetails.onCreateTable(database);
        TablePatientSession.onCreateTable(database);
        TableDeviceSession.onCreateTable(database);
        TableDeviceInfo.onCreateTable(database);

        TableConnectionEvent.onCreateTable(database);

        TableDiagnosticsGatewayStartupTimes.onCreateTable(database);
        TableDiagnosticsUiStartupTimes.onCreateTable(database);

        tableManuallyEnteredHeartRate.onCreateTable(database);
        tableManuallyEnteredRespirationRate.onCreateTable(database);
        tableManuallyEnteredTemperature.onCreateTable(database);
        tableManuallyEnteredSpO2.onCreateTable(database);
        tableManuallyEnteredBloodPressure.onCreateTable(database);
        tableManuallyEnteredWeight.onCreateTable(database);
        tableManuallyEnteredConsciousnessLevel.onCreateTable(database);
        tableManuallyEnteredSupplementalOxygenLevel.onCreateTable(database);
        TableManuallyEnteredAnnotation.onCreateTable(database);
        tableManuallyEnteredCapillaryRefillTime.onCreateTable(database);
        tableManuallyEnteredRespirationDistress.onCreateTable(database);
        tableManuallyEnteredFamilyOrNurseConcern.onCreateTable(database);
        tableManuallyEnteredUrineOutput.onCreateTable(database);

        TablePatientSessionsFullySynced.onCreateTable(database);
        
        TableThresholdSet.onCreateTable(database);
        TableThresholdSetAgeBlockDetail.onCreateTable(database);
        TableThresholdSetLevel.onCreateTable(database);
        TableThresholdSetColour.onCreateTable(database);

        TableEarlyWarningScore.onCreateTable(database);

        TableSetupModeLog.onCreateTable(database);

        TableServerConfigurableText.onCreateTable(database);

        tableWards.onCreateTable(database);
        tableBeds.onCreateTable(database);

        tableAuditTrail.onCreateTable(database);

        TableViewableWebPageDetails.onCreateTable(database);
    }

    // Method is called during an upgrade of the database. e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        PatientGatewayService.exportDBStatic();

        tableLifetouchHeartRate.onUpgrade(database, oldVersion, newVersion);
        tableLifetouchHeartBeat.onUpgrade(database, oldVersion, newVersion);
        tableLifetouchSetupModeRawSample.onUpgrade(database, oldVersion, newVersion);
        tableLifetouchRespirationRate.onUpgrade(database, oldVersion, newVersion);
        tableLifetouchBattery.onUpgrade(database, oldVersion, newVersion);
        tableLifetouchPatientOrientation.onUpgrade(database, oldVersion, newVersion);
        tableLifetouchRawAccelerometerModeSample.onUpgrade(database, oldVersion, newVersion);

        tableLifetempMeasurement.onUpgrade(database, oldVersion, newVersion);
        tableLifetempBattery.onUpgrade(database, oldVersion, newVersion);

        tableOximeterMeasurement.onUpgrade(database, oldVersion, newVersion);
        tableOximeterIntermediateMeasurement.onUpgrade(database, oldVersion, newVersion);
        tableOximeterSetupModeRawSample.onUpgrade(database, oldVersion, newVersion);
        tableOximeterBattery.onUpgrade(database, oldVersion, newVersion);

        tableBloodPressureMeasurement.onUpgrade(database, oldVersion, newVersion);
        tableBloodPressureBattery.onUpgrade(database, oldVersion, newVersion);

        tableWeightScaleWeight.onUpgrade(database, oldVersion, newVersion);
        tableWeightScaleBattery.onUpgrade(database, oldVersion, newVersion);
        
        TablePatientDetails.onUpgrade(database, oldVersion, newVersion);
        TablePatientSession.onUpgrade(database, oldVersion, newVersion);
        TableDeviceSession.onUpgrade(database, oldVersion, newVersion);
        TableDeviceInfo.onUpgrade(database, oldVersion, newVersion);

        TableConnectionEvent.onUpgrade(database, oldVersion, newVersion);

        TableDiagnosticsGatewayStartupTimes.onUpgrade(database, oldVersion, newVersion);
        TableDiagnosticsUiStartupTimes.onUpgrade(database, oldVersion, newVersion);

        tableManuallyEnteredHeartRate.onUpgrade(database, oldVersion, newVersion);
        tableManuallyEnteredRespirationRate.onUpgrade(database, oldVersion, newVersion);
        tableManuallyEnteredTemperature.onUpgrade(database, oldVersion, newVersion);
        tableManuallyEnteredSpO2.onUpgrade(database, oldVersion, newVersion);
        tableManuallyEnteredBloodPressure.onUpgrade(database, oldVersion, newVersion);
        tableManuallyEnteredWeight.onUpgrade(database, oldVersion, newVersion);
        tableManuallyEnteredConsciousnessLevel.onUpgrade(database, oldVersion, newVersion);
        tableManuallyEnteredSupplementalOxygenLevel.onUpgrade(database, oldVersion, newVersion);
        TableManuallyEnteredAnnotation.onUpgrade(database, oldVersion, newVersion);
        tableManuallyEnteredCapillaryRefillTime.onUpgrade(database, oldVersion, newVersion);
        tableManuallyEnteredRespirationDistress.onUpgrade(database, oldVersion, newVersion);
        tableManuallyEnteredFamilyOrNurseConcern.onUpgrade(database, oldVersion, newVersion);
        tableManuallyEnteredUrineOutput.onUpgrade(database, oldVersion, newVersion);

        TablePatientSessionsFullySynced.onUpgrade(database, oldVersion, newVersion);
        
        TableThresholdSet.onUpgrade(database, oldVersion, newVersion);
        TableThresholdSetAgeBlockDetail.onUpgrade(database, oldVersion, newVersion);
        TableThresholdSetLevel.onUpgrade(database, oldVersion, newVersion);
        TableThresholdSetColour.onUpgrade(database, oldVersion, newVersion);
        
        TableEarlyWarningScore.onUpgrade(database, oldVersion, newVersion);

        TableFirmwareImage.onUpgrade(database, oldVersion, newVersion);

        TableSetupModeLog.onUpgrade(database, oldVersion, newVersion);

        TableServerConfigurableText.onUpgrade(database, oldVersion, newVersion);

        tableWards.onUpgrade(database, oldVersion, newVersion);
        tableBeds.onUpgrade(database, oldVersion, newVersion);
		
		tableAuditTrail.onUpgrade(database, oldVersion, newVersion);

        TableViewableWebPageDetails.onUpgrade(database, oldVersion, newVersion);
    }
}
