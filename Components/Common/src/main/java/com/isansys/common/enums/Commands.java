package com.isansys.common.enums;

public enum Commands
{
    CMD_CREATE_NEW_SESSION,
    CMD_UPDATE_EXISTING_SESSION,
    CMD_END_EXISTING_SESSION,
    REMOVED__CMD_PAUSE_EXISTING_SESSION,                                                     // Not used anymore but acting as placeholder so upgrades go smoothly
    CMD_GET_PATIENT_THRESHOLD_SET,
    CMD_REPORT_PATIENT_THRESHOLD_SET,
    CMD_GET_HOSPITAL_PATIENT_ID,
    CMD_GET_GATEWAY_SESSION_NUMBERS,
    CMD_REPORT_GATEWAY_SESSION_NUMBERS,
    CMD_REPORT_HOSPITAL_PATIENT_ID,

    CMD_UPDATE_USER_INTERFACE_BLUETOOTH_STATUS,

    REMOVED__CMD_REPORT_BTLE_SCAN_PROGRESS,                                                  // Not used anymore but acting as placeholder so upgrades go smoothly
    CMD_REPORT_BLUETOOTH_SCAN_PROGRESS,
    CMD_CHECK_GATEWAY_UI_CONNECTION,                                                // UI sends to PG on startup to check PG responding
    CMD_SEND_NEW_SETUP_MODE_DATA_TO_UI,                                             // Send the UI the new Setup Mode data
    CMD_START_SETUP_MODE,
    CMD_STOP_SETUP_MODE,

    REMOVED__CMD_RSSI_VALUE,                                                                 // Not used anymore but acting as placeholder so upgrades go smoothly

    REMOVED__CMD_SEND_GATEWAY_VERSION_NUMBER,                                                // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_GATEWAY_VERSION_NUMBER,                                              // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_VALIDATE_QR_CODE,
    CMD_REPORT_QR_CODE_DETAILS,

    CMD_GET_GATEWAYS_ASSIGNED_BED_DETAILS,
    CMD_REPORT_GATEWAYS_ASSIGNED_BED_DETAILS,
    CMD_SET_GATEWAYS_ASSIGNED_BED_DETAILS,

    REMOVED__CMD_REPORT_LIFETOUCH_BATTERY_LEVEL,                                             // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_LIFETEMP_BATTERY_LEVEL,                                              // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_NONIN_WRIST_OX_BATTERY_LEVEL,                                        // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_LIFEOX_BATTERY_LEVEL,                                                // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_BLOOD_PRESSURE_BATTERY_LEVEL,                                        // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_GET_SERVER_ADDRESS,
    CMD_REPORT_SERVER_ADDRESS,
    CMD_SET_SERVER_ADDRESS,

    CMD_PING_SERVER,
    CMD_REPORT_SERVER_PING_RESULT,
    CMD_REPORT_SERVER_RESPONSE,

    REMOVED__CMD_SET_DESIRED_LIFETOUCH,                                                      // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_SET_DESIRED_LIFETEMP,                                                       // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_SET_DESIRED_NONIN_WRIST_OX,                                                 // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_SET_DESIRED_LIFEOX,                                                         // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_SET_DESIRED_BLOOD_PRESSURE,                                                 // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_CLEAR_DESIRED_LIFETOUCH,                                                    // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_CLEAR_DESIRED_LIFETEMP,                                                     // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_CLEAR_DESIRED_NONIN_WRIST_OX,                                               // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_CLEAR_DESIRED_LIFEOX,                                                       // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_CLEAR_DESIRED_BLOOD_PRESSURE,                                               // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_LIFETOUCH_DEVICE_INFO,                                                  // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_LIFETEMP_DEVICE_INFO,                                                   // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_NONIN_WRIST_OX_DEVICE_INFO,                                             // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_LIFEOX_DEVICE_INFO,                                                     // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_BLOOD_PRESSURE_DEVICE_INFO,                                             // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_LIFETOUCH_DEVICE_INFO,                                               // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_LIFETEMP_DEVICE_INFO,                                                // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_NONIN_WRIST_OX_DEVICE_INFO,                                          // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_LIFEOX_DEVICE_INFO,                                                  // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_BLOOD_PRESSURE_UA767_DEVICE_INFO,                                    // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_RETRY_CONNECTING_TO_LIFETOUCH,                                              // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_RETRY_CONNECTING_TO_LIFETEMP,                                               // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_RETRY_CONNECTING_TO_NONIN_WRIST_OX,                                         // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_RETRY_CONNECTING_TO_LIFEOX,                                                 // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_RETRY_CONNECTING_TO_BLOOD_PRESSURE,                                         // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_CONNECT_TO_DESIRED_BLUETOOTH_DEVICES,
    CMD_END_OF_DEVICE_CONNECTION,                                                   // Sent to the UI to let it know to show Start Monitoring button and to start the screen timeout

    REMOVED__CMD_DISCONNECT_FROM_DESIRED_LIFETOUCH,                                          // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_DISCONNECT_FROM_DESIRED_LIFETEMP,                                           // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_DISCONNECT_FROM_DESIRED_NONIN_WRIST_OX,                                     // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_DISCONNECT_FROM_DESIRED_LIFEOX,                                             // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_DISCONNECT_FROM_DESIRED_BLOOD_PRESSURE,                                     // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_LAST_LIFETOUCH_BATTERY_LEVEL,                                           // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_LAST_LIFETEMP_BATTERY_LEVEL,                                            // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_LAST_LIFEOX_BATTERY_LEVEL,                                              // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_ENABLE_SERVER_SYNCING,
    CMD_GET_SERVER_SYNC_ENABLE_STATUS,
    CMD_REPORT_SERVER_SYNC_ENABLE_STATUS,

    REMOVED__CMD_ENABLE_LIFETOUCH_TEST_MODE,                                                 // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_LIFETOUCH_TEST_MODE_ENABLE_STATUS,                                      // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_LIFETOUCH_TEST_MODE_ENABLE_STATUS,                                   // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_ENABLE_NIGHT_MODE,
    CMD_START_DEVICE_RAW_ACCELEROMETER_MODE,
    CMD_STOP_DEVICE_RAW_ACCELEROMETER_MODE,
    CMD_SEND_NEW_RAW_ACCELEROMETER_MODE_DATA_TO_UI,
    CMD_GET_WARDS_AND_BEDS_FROM_SERVER,
    CMD_REPORT_WARD_LIST,
    CMD_REPORT_BED_LIST,

    CMD_RESET_BLUETOOTH,

    CMD_REPORT_DEVICE_LEAD_OFF_DETECTION_STATUS,

    CMD_EMPTY_LOCAL_DATABASE,

    CMD_CHECK_DEVICE_STATUS,
    CMD_REPORT_CHECK_DEVICE_STATUS_RESULTS,
    CMD_SET_DUMMY_SERVER_DETAILS,
    CMD_RADIO_OFF,

    CMD_FROM_SERVER__START_DEVICE_SETUP_MODE,
    CMD_FROM_SERVER__STOP_DEVICE_SETUP_MODE,

    CMD_SET_REALTIME_SERVER_PORT,
    CMD_GET_REALTIME_SERVER_PORT,
    CMD_REPORT_REALTIME_SERVER_PORT,

    CMD_REPORT_DEVICE_SETUP_MODE_STARTED_VIA_SERVER,
    CMD_REPORT_DEVICE_SETUP_MODE_STOPPED_VIA_SERVER,

    CMD_ENABLE_REALTIME_LINK,
    CMD_GET_REALTIME_LINK_ENABLE_STATUS,
    CMD_REPORT_REALTIME_LINK_ENABLE_STATUS,

    CMD_REPORT_CONNECTED_TO_SERVER_STATUS,

    CMD_EXPORT_LOCAL_DATABASE_TO_ANDROID_ROOT,

    REMOVED__CMD_REPORT_NONIN_WRIST_OX_ATTACHED_STATUS,                                      // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_LIFEOX_ATTACHED_STATUS,                                              // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_ENABLE_HTTPS,
    CMD_GET_HTTPS_ENABLE_STATUS,
    CMD_REPORT_HTTPS_ENABLE_STATUS,

    CMD_REPORT_INVALID_SERVER_STATUS_CODE,

    CMD_GET_PATIENT_NAME_FROM_HOSPITAL_PATIENT_ID,
    CMD_REPORT_PATIENT_NAME_FROM_SERVER_LOOKUP_OF_HOSPITAL_PATIENT_ID,

    CMD_REPORT_PATIENT_NAME_LOOKUP_ENABLE_STATUS,
    CMD_ENABLE_PATIENT_NAME_LOOKUP,
    CMD_GET_PATIENT_NAME_LOOKUP_ENABLE_STATUS,

    CMD_FORCE_DEVICE_LEADS_OFF_STATE,

    REMOVED__CMD_FORCE_NONIN_WRIST_OX_FINGER_OFF_STATE,                                      // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_FORCE_LIFEOX_FINGER_OFF_STATE,                                              // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_ENABLE_DEVICE_PERIODIC_SETUP_MODE,
    CMD_REPORT_DEVICE_PERIODIC_SETUP_MODE_ENABLE_STATUS,
    CMD_GET_DEVICE_PERIODIC_SETUP_MODE_ENABLE_STATUS,

    CMD_DISPLAY_DEVICE_PERIODIC_MODE_DATA_IN_UI,
    REMOVED__CMD_DISPLAY_NONIN_WRIST_OX_PERIODIC_MODE_DATA_IN_UI,                            // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_DISPLAY_LIFEOX_PERIODIC_MODE_DATA_IN_UI,                                    // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_GET_DEVICE_LEADS_OFF_STATUS,

    REMOVED__CMD_REPORT_LIFETOUCH_CHANGE_SESSION_DISCONNECTED,                               // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_DO_NTP_TIME_SYNC,
    CMD_REPORT_NTP_CLOCK_OFFSET_IN_MS,

    CMD_UPDATE_UI_DEVICE_MEASUREMENT_PROGRESS_BAR,

    CMD_GET_DUMMY_DATA_MODE_DEVICE_LEADS_OFF_ENABLE_STATUS,
    CMD_REPORT_DUMMY_DATA_MODE_DEVICE_LEADS_OFF_ENABLE_STATUS,

    REMOVED__CMD_GET_DUMMY_DATA_MODE_NONIN_WRIST_OX_FINGER_OFF_ENABLE_STATUS,                // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_DUMMY_DATA_MODE_NONIN_WRIST_OX_FINGER_OFF_ENABLE_STATUS,             // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_DUMMY_DATA_MODE_LIFEOX_FINGER_OFF_ENABLE_STATUS,                        // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_DUMMY_DATA_MODE_LIFEOX_FINGER_OFF_ENABLE_STATUS,                     // Not used anymore but acting as placeholder so upgrades go smoothly

    REMOVED__CMD_DUMMY_MODE_SERVER_ENABLE_DEVICE_SETUP_MODE,                                 // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_DUMMY_MODE_FORCE_ENABLE_DEVICE_SETUP_MODE,                                  // Not used anymore but acting as placeholder so upgrades go smoothly
    CMD_EXIT_BUTTON_PRESSED,

    REMOVED__CMD_REPORT_LIFETOUCH_OPERATING_MODE,                                            // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_FROM_SERVER__EXPORT_LOCAL_DATABASE_TO_ANDROID_ROOT,
    CMD_FROM_SERVER__EMPTY_LOCAL_DATABASE,
    CMD_FROM_SERVER__REPORT_GATEWAY_STATUS,
    CMD_FROM_SERVER__ENABLE_PATIENT_NAME_LOOKUP,
    CMD_FROM_SERVER__ENABLE_PERIODIC_SETUP_MODE,
    CMD_FROM_SERVER__MANUAL_TIME_SYNC,
    CMD_FROM_SERVER__ENABLE_DUMMY_DATA_MODE,
    CMD_FROM_SERVER__QR_CODE_UNLOCK_USER,
    CMD_FROM_SERVER__QR_CODE_UNLOCK_ADMIN,
    REMOVED__CMD_FROM_SERVER__ENABLE_NEW_RESPIRATION_ALGORITHM,                              // Not used anymore but acting as placeholder so upgrades go smoothly
    CMD_FROM_SERVER__ENABLE_SIMPLE_HEART_RATE_ALGORITHM,
    CMD_FROM_SERVER__SET_JSON_ARRAY_SIZE,

    REMOVED__CMD_ENABLE_ANIMATION_MODE,                                                      // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_ANIMATION_MODE_ENABLE_STATUS,                                           // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_ANIMATION_MODE_ENABLE_STATUS,                                        // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_LIFETEMP_LEADS_OFF_STATUS,                                              // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_LIFETEMP_LEADS_OFF_STATUS,                                           // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_REPORT_WIFI_STATUS,

    CMD_FROM_SERVER__ENABLE_DATA_SYNCING_TO_SERVER,
    REMOVED__CMD_FROM_SERVER__DISABLE_DATA_SYNCING_TO_SERVER,                                // Not used anymore but acting as placeholder so upgrades go smoothly

    REMOVED__CMD_FORCE_LIFETEMP_LEADS_OFF_STATE,                                             // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_DUMMY_DATA_MODE_LIFETEMP_LEADS_OFF_ENABLE_STATUS,                       // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_DUMMY_DATA_MODE_LIFETEMP_LEADS_OFF_ENABLE_STATUS,                    // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_NONIN_WRIST_OX_LEADS_OFF_STATUS,                                        // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_LIFEOX_LEADS_OFF_STATUS,                                                // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_LIFETOUCH_START_SESSION_TIME_FOR_POINCARE_GRAPH,                        // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_LIFETOUCH_START_SESSION_TIME_FOR_POINCARE_GRAPH,                     // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_REPORT_DATABASE_EMPTIED,                                                    // Command sent by the Gateway to tell the UI that the database has been emptied

    CMD_ENABLE_CSV_OUTPUT,
    CMD_GET_CSV_ENABLE_STATUS,
    CMD_REPORT_CSV_ENABLE_STATUS,

    REMOVED__CMD_REMOVE_DEVICE_FROM_ADD_DEVICES_PAGE,                                        // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_DATABASE_STATUS,                                                        // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_REPORT_DATABASE_STATUS,

    CMD_SET_JSON_ARRAY_SIZE,
    CMD_GET_JSON_ARRAY_SIZE,
    CMD_REPORT_JSON_ARRAY_SIZE,

    CMD_USE_NEW_RESPIRATION_RATE,
    CMD_GET_RESPIRATION_RATE_ALGORITHM,
    CMD_REPORT_RESPIRATION_RATE_ALGORITHM,

    REMOVED__CMD_FORCE_LIFETOUCH_CONNECTION_EVENT_STATE_TO_DISCONNECTED,                     // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_FORCE_LIFETEMP_CONNECTION_EVENT_STATE_TO_DISCONNECTED,                      // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_FORCE_NONIN_WRIST_OX_CONNECTION_EVENT_STATE_TO_DISCONNECTED,                // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_FORCE_LIFEOX_CONNECTION_EVENT_STATE_TO_DISCONNECTED,                        // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_LIFETOUCH_FORCE_CONNECTION_EVENT_STATE,                                 // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_LIFETEMP_FORCE_CONNECTION_EVENT_STATE,                                  // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_NONIN_WRIST_OX_FORCE_CONNECTION_EVENT_STATE,                            // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_GET_WIFI_STATUS,

    REMOVED__CMD_REPORT_LIFETOUCH_BEAT_RECEIVED,                                             // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_SET_NUMBER_OF_DUMMY_DATA_MODE_MEASUREMENTS_PER_TICK,
    CMD_GET_NUMBER_OF_DUMMY_DATA_MODE_MEASUREMENTS_PER_TICK,
    CMD_REPORT_NUMBER_OF_DUMMY_DATA_MODE_MEASUREMENTS_PER_TICK,
    CMD_FROM_SERVER__SET_NUMBER_OF_DUMMY_DATA_MODE_MEASUREMENTS_PER_TICK,

    CMD_TELL_GATEWAY_THAT_UI_HAS_BOOTED,                                            // Command to tell the Gateway that the UI has (re)booted
    CMD_TELL_UI_THAT_GATEWAY_HAS_BOOTED,                                            // Command to tell the UI that the Gateway has (re)booted

    CMD_FROM_SERVER__RESET_GATEWAY_AND_UI_RUN_COUNTERS,

    REMOVED__CMD_REPORT_NONIN_WRIST_OX_OPERATING_MODE,                                       // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_START_NONIN_WRIST_OX_SETUP_MODE,                                            // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_STOP_NONIN_WRIST_OX_SETUP_MODE,                                             // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_START_NONIN_WRIST_OX_SETUP_MODE_FROM_WAMP,                                  // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_STOP_NONIN_WRIST_OX_SETUP_MODE_FROM_WAMP,                                   // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_NONIN_WRIST_OX_SETUP_MODE_STARTED_VIA_SERVER,                        // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_NONIN_WRIST_OX_SETUP_MODE_STOPPED_VIA_SERVER,                        // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_DUMMY_MODE_FORCE_ENABLE_NONIN_WRIST_OX_SETUP_MODE,                          // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_DUMMY_MODE_SERVER_ENABLE_NONIN_WRIST_OX_SETUP_MODE,                         // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_SEND_NEW_NONIN_WRIST_OX_SETUP_MODE_DATA_TO_UI,                              // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_SEND_NEW_LIFEOX_SETUP_MODE_DATA_TO_UI,                                      // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_LIFEOX_OPERATING_MODE,                                               // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_START_LIFEOX_SETUP_MODE,                                                    // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_STOP_LIFEOX_SETUP_MODE,                                                     // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_START_LIFEOX_SETUP_MODE_FROM_WAMP,                                          // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_STOP_LIFEOX_SETUP_MODE_FROM_WAMP,                                           // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_LIFEOX_SETUP_MODE_STARTED_VIA_WAMP,                                  // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_LIFEOX_SETUP_MODE_STOPPED_VIA_WAMP,                                  // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_DUMMY_MODE_FORCE_ENABLE_LIFEOX_SETUP_MODE,                                  // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_DUMMY_MODE_SERVER_ENABLE_LIFEOX_SETUP_MODE,                                 // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_REPORT_NUMBER_OF_LIFETOUCH_HEART_BEATS_PENDING,                             // Command to tell the UI how much data is cached on the Lifetouch

    REMOVED__CMD_FROM_WAMP_SAVE_SESSION_TO_DISK_FOR_SOFTWARE_UPDATE,                         // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_FROM_WAMP_RESTORE_SESSION_FROM_DISK_AFTER_SOFTWARE_UPDATE,                  // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_FROM_WAMP_DELETE_SAVED_SESSION_FROM_DISK_AFTER_SOFTWARE_UPDATE,             // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_SESSION_BEING_SAVED_TO_DISK,                                         // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_SESSION_BEING_RESTORED_FROM_DISK,                                    // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_REPORT_NEW_VITALS_DATA,                                                     // Send new vital sign to the UI

    CMD_SET_SETUP_MODE_TIME_IN_SECONDS,
    CMD_GET_SETUP_MODE_TIME_IN_SECONDS,
    CMD_REPORT_SETUP_MODE_TIME_IN_SECONDS,

    REMOVED__CMD_SET_NONIN_WRIST_OX_SETUP_MODE_TIME_IN_SECONDS,                              // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_NONIN_WRIST_OX_SETUP_MODE_TIME_IN_SECONDS,                              // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_NONIN_WRIST_OX_SETUP_MODE_TIME_IN_SECONDS,                           // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_SET_LIFEOX_SETUP_MODE_TIME_IN_SECONDS,                                      // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_LIFEOX_SETUP_MODE_TIME_IN_SECONDS,                                      // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_LIFEOX_SETUP_MODE_TIME_IN_SECONDS,                                   // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_FROM_SERVER__SET_SETUP_MODE_TIME_IN_SECONDS,
    REMOVED__CMD_FROM_SERVER__SET_NONIN_WRIST_OX_SETUP_MODE_TIME_IN_SECONDS,                 // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_GET_PATIENT_START_SESSION_TIME,
    CMD_REPORT_PATIENT_START_SESSION_TIME,

    CMD_SET_MAX_NUMBER_NONIN_WRIST_OX_INTERMEDIATE_MEASUREMENTS_INVALID_BEFORE_MINUTE_MARKED_INVALID,
    CMD_GET_MAX_NUMBER_NONIN_WRIST_OX_INTERMEDIATE_MEASUREMENTS_INVALID_BEFORE_MINUTE_MARKED_INVALID,
    CMD_REPORT_MAX_NUMBER_NONIN_WRIST_OX_INTERMEDIATE_MEASUREMENTS_INVALID_BEFORE_MINUTE_MARKED_INVALID,
    CMD_FROM_SERVER__MAX_NUMBER_NONIN_WRIST_OX_INTERMEDIATE_MEASUREMENTS_INVALID_BEFORE_MINUTE_MARKED_INVALID,

    CMD_ENABLE_WEBSERVICE_AUTHENTICATION,
    CMD_GET_WEBSERVICE_AUTHENTICATION_ENABLE_STATUS,
    CMD_REPORT_WEBSERVICE_AUTHENTICATION_ENABLE_STATUS,

    CMD_FROM_SERVER__ENABLE_WEBSERVICE_AUTHENTICATION,

    REMOVED__CMD_STORE_MANUALLY_ENTERED_VITAL_SIGN_HEART_RATE,                               // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_STORE_MANUALLY_ENTERED_VITAL_SIGN_RESPIRATION_RATE,                         // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_STORE_MANUALLY_ENTERED_VITAL_SIGN_TEMPERATURE,                              // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_STORE_MANUALLY_ENTERED_VITAL_SIGN_SPO2,                                     // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_STORE_MANUALLY_ENTERED_VITAL_SIGN_BLOOD_PRESSURE,                           // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_STORE_MANUALLY_ENTERED_VITAL_SIGN_CONSCIOUSNESS_LEVEL,                      // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_STORE_MANUALLY_ENTERED_VITAL_SIGN_SUPPLEMENTAL_OXYGEN_LEVEL,                // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_LIFEOX_DISPLAY_MODE_IN_CHANGE_SESSION_FRAGMENT,                         // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_LIFEOX_DISPLAY_MODE_IN_CHANGE_SESSION_FRAGMENT,                      // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_EARLY_WARNING_SCORE_DEVICE_INFO,                                        // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_EARLY_WARNING_SCORE_DEVICE_INFO,                                     // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_PULSE_TRANSIT_TIME_DEVICE_INFO,                                         // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_PULSE_TRANSIT_TIME_DEVICE_INFO,                                      // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_GET_DEFAULT_EARLY_WARNING_SCORE_TYPES_FROM_SERVER,
    CMD_REPORT_CACHED_DATA_UPDATED,

    REMOVED__CMD_ENABLE_EARLY_WARNING_SCORES,                                                // Not used anymore but acting as placeholder so upgrades go smoothly
    CMD_ENABLE_EARLY_WARNING_SCORES_DEVICE,
    REMOVED__CMD_GET_EARLY_WARNING_SCORES_ENABLE_STATUS,                                     // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_EARLY_WARNING_SCORES_ENABLE_STATUS,                                  // Not used anymore but acting as placeholder so upgrades go smoothly
    CMD_SET_SPOOF_EARLY_WARNING_SCORES,
    CMD_GET_SPOOF_EARLY_WARNING_SCORES,
    CMD_REPORT_SPOOF_EARLY_WARNING_SCORES,

    REMOVED_CMD_ENABLE_PULSE_TRANSIT_TIME,
    REMOVED_CMD_GET_PULSE_TRANSIT_TIME_ENABLE_STATUS,
    REMOVED_CMD_REPORT_PULSE_TRANSIT_TIME_ENABLE_STATUS,

    CMD_REPORT_PATIENT_ORIENTATION,

    CMD_EMPTY_LOCAL_DATABASE_INCLUDING_EWS_THRESHOLD_SETS,

    CMD_ENABLE_WEBSERVICE_ENCRYPTION,
    CMD_GET_WEBSERVICE_ENCRYPTION_ENABLE_STATUS,
    CMD_REPORT_WEBSERVICE_ENCRYPTION_ENABLE_STATUS,

    CMD_FROM_SERVER__ENABLE_WEBSERVICE_ENCRYPTION,

    CMD_SET_LOG_CAT_MESSAGES,

    CMD_REPORT_ALL_BLUETOOTH_DEVICES_NOT_CONNECTED,
    CMD_STOP_RUNNING_BLUETOOTH_SCAN,

    CMD_SET_SERVER_PORT,
    CMD_GET_SERVER_PORT,
    CMD_REPORT_SERVER_PORT,

    CMD_RECONNECT_WIFI,

    REMOVED__CMD_SET_WIFI_AUTO_RECONNECT_ENABLED_STATUS,                                     // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_WIFI_AUTO_RECONNECT_ENABLED_STATUS,                                     // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_WIFI_AUTO_RECONNECT_ENABLED_STATUS,                                  // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_SET_RUN_DEVICES_IN_TEST_MODE,
    CMD_GET_RUN_DEVICES_IN_TEST_MODE,
    CMD_REPORT_RUN_DEVICES_IN_TEST_MODE,

    CMD_SET_MANUAL_VITAL_SIGNS_ENABLED_STATUS,
    CMD_GET_MANUAL_VITAL_SIGNS_ENABLED_STATUS,
    CMD_REPORT_MANUAL_VITAL_SIGNS_ENABLED_STATUS,

    CMD_FROM_SERVER__REPORT_FEATURES_ENABLED,
    CMD_FROM_SERVER__QR_CODE_UNLOCK_FEATURE_ENABLE,

    CMD_FROM_SERVER__ENABLE_MANUAL_VITAL_SIGNS_ENTRY,
    REMOVED__CMD_FROM_SERVER__ENABLE_EARLY_WARNING_SCORES,                                   // Not used anymore but acting as placeholder so upgrades go smoothly
    CMD_FROM_SERVER__ENABLE_CSV_OUTPUT,

    CMD_SET_LONG_TERM_MEASUREMENT_TIMEOUT_IN_MINUTES,
    CMD_GET_LONG_TERM_MEASUREMENT_TIMEOUT_IN_MINUTES,
    CMD_REPORT_LONG_TERM_MEASUREMENT_TIMEOUT_IN_MINUTES,

    CMD_GET_DATABASE_STATUS_FOR_POPUP_SERVER_STATUS,

    CMD_REPORT_GATEWAY_PLUGGED_IN_STATUS_TIMER_SWITCH_SAFE,

    CMD_SET_UNPLUGGED_OVERLAY_ENABLED_STATUS,
    CMD_GET_UNPLUGGED_OVERLAY_ENABLED_STATUS,
    CMD_REPORT_UNPLUGGED_OVERLAY_ENABLED_STATUS,

    CMD_FROM_SERVER__ENABLE_NIGHT_MODE,
    CMD_FROM_SERVER__ENABLE_UI_NIGHT_MODE,

    CMD_FROM_SERVER__ENABLE_UNPLUGGED_OVERLAY,

    CMD_SET_DUMMY_DATA_MODE_BACKFILL_SESSION_WITH_DATA_ENABLED_STATUS,
    CMD_SET_DUMMY_DATA_MODE_BACKFILL_HOURS,

    CMD_VALIDATE_DATA_MATRIX_BARCODE,
    CMD_REPORT_DATA_MATRIX_VALIDATION_RESULT,

    CMD_REPORT_LIFETOUCH_BEATS_RECEIVED,

    CMD_FROM_SERVER__RESET_SERVER_SYNC_STATUS,

    CMD_GET_NTP_CLOCK_OFFSET_IN_MS,

    CMD_DELETE_EXPORTED_DATABASES,

    CMD_SET_MANUFACTURING_MODE_ENABLED_STATUS,
    CMD_GET_MANUFACTURING_MODE_ENABLED_STATUS,
    CMD_REPORT_MANUFACTURING_MODE_ENABLED_STATUS,

    REMOVED__CMD_SET_CONTINUOUS_SETUP_MODE_ENABLED_STATUS,                                   // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_CONTINUOUS_SETUP_MODE_ENABLED_STATUS,                                   // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_CONTINUOUS_SETUP_MODE_ENABLED_STATUS,                                // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_FROM_SERVER__REPORT_PATIENT_NAME_RECEIVED_FROM_HOSPITAL_PATIENT_ID_LOOKUP,

    REMOVED__CMD_SET_NONIN_INSERT_FINGER_TO_TURN_ON_MODE_ENABLED_STATUS,                     // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_NONIN_INSERT_FINGER_TO_TURN_ON_MODE_ENABLED_STATUS,                     // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_NONIN_INSERT_FINGER_TO_TURN_ON_MODE_ENABLED_STATUS,                  // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_FROM_SERVER__ENABLE_NONIN_TURN_ON_BY_INSERTING_FINGER,                      // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_SET_SIMPLE_HEART_RATE_ENABLED_STATUS,
    CMD_GET_SIMPLE_HEART_RATE_ENABLED_STATUS,
    CMD_REPORT_SIMPLE_HEART_RATE_ENABLED_STATUS,

    CMD_REPORT_USB_ACCESSORY_CONNECTION_STATUS,

    CMD_REPORT_GSM_STATUS,

    REMOVED__CMD_REPORT_LIFETEMP_CHANGE_SESSION_DISCONNECTED,                                // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_LIFEOX_CHANGE_SESSION_DISCONNECTED,                                  // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_DISABLE_BLUETOOTH_ADAPTER,
    CMD_ENABLE_BLUETOOTH_ADAPTER,

    CMD_DISABLE_WIFI,
    CMD_ENABLE_WIFI,

    CMD_SET_GSM_MODE_ONLY_ENABLED_STATUS,
    CMD_GET_GSM_MODE_ONLY_ENABLED_STATUS,
    CMD_REPORT_GSM_MODE_ONLY_ENABLED_STATUS,

    CMD_GET_SWEETBLUE_DIAGNOSTICS,
    CMD_REPORT_SWEETBLUE_DIAGNOSTICS,

    CMD_SET_USE_BACK_CAMERA_ENABLED_STATUS,
    CMD_GET_USE_BACK_CAMERA_ENABLED_STATUS,
    CMD_REPORT_USE_BACK_CAMERA_ENABLED_STATUS,

    CMD_SET_PATIENT_ORIENTATION_ENABLED_STATUS,
    CMD_GET_PATIENT_ORIENTATION_ENABLED_STATUS,
    CMD_REPORT_PATIENT_ORIENTATION_ENABLED_STATUS,

    CMD_SET_SHOW_NUMBERS_ON_BATTERY_INDICATOR_ENABLED_STATUS,
    CMD_GET_SHOW_NUMBERS_ON_BATTERY_INDICATOR_ENABLED_STATUS,
    CMD_REPORT_SHOW_NUMBERS_ON_BATTERY_INDICATOR_ENABLED_STATUS,

    CMD_SET_SHOW_MAC_ADDRESS_ENABLED_STATUS,
    CMD_GET_SHOW_MAC_ADDRESS_ENABLED_STATUS,
    CMD_REPORT_SHOW_MAC_ADDRESS_ENABLED_STATUS,

    CMD_SET_LIFETEMP_MEASUREMENT_INTERVAL_IN_SECONDS,
    CMD_GET_LIFETEMP_MEASUREMENT_INTERVAL_IN_SECONDS,
    CMD_REPORT_LIFETEMP_MEASUREMENT_INTERVAL_IN_SECONDS,

    CMD_RESTART_INSTALLATION_WIZARD,

    CMD_SET_DEVELOPER_POPUP_ENABLED_STATUS,
    CMD_GET_DEVELOPER_POPUP_ENABLED_STATUS,
    CMD_REPORT_DEVELOPER_POPUP_ENABLED_STATUS,

    CMD_SET_PATIENT_ORIENTATION_MEASUREMENT_INTERVAL_IN_SECONDS,
    CMD_GET_PATIENT_ORIENTATION_MEASUREMENT_INTERVAL_IN_SECONDS,
    CMD_REPORT_PATIENT_ORIENTATION_MEASUREMENT_INTERVAL_IN_SECONDS,

    CMD_SET_SHOW_IP_ADDRESS_ON_WIFI_POPUP_ENABLED_STATUS,
    CMD_GET_SHOW_IP_ADDRESS_ON_WIFI_POPUP_ENABLED_STATUS,
    CMD_REPORT_SHOW_IP_ADDRESS_ON_WIFI_POPUP_ENABLED_STATUS,

    CMD_SET_PERCENTAGE_OF_POOR_SIGNAL_HEART_BEATS_BEFORE_MINUTE_MARKED_INVALID,
    CMD_GET_PERCENTAGE_OF_POOR_SIGNAL_HEART_BEATS_BEFORE_MINUTE_MARKED_INVALID,
    CMD_REPORT_PERCENTAGE_OF_POOR_SIGNAL_HEART_BEATS_BEFORE_MINUTE_MARKED_INVALID,

    CMD_REPORT_GATEWAY_CHARGE_STATUS,

    CMD_REPORT_NUMBER_OF_LIFETEMP_MEASUREMENTS_PENDING,                             // Command to tell the UI how much data is cached on the Lifetemp

    CMD_REPORT_LIFETOUCH_NO_BEATS_DETECTED_TIMER_STATUS,

    CMD_SET_USA_MODE_ENABLED_STATUS,
    CMD_GET_USA_MODE_ENABLED_STATUS,
    CMD_REPORT_USA_MODE_ENABLED_STATUS,

    REMOVED__CMD_GET_LIFETOUCH_OPERATING_MODE,                                               // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_FROM_SERVER__START_DEVICE_RAW_ACCELEROMETER_MODE,
    CMD_FROM_SERVER__STOP_DEVICE_RAW_ACCELEROMETER_MODE,

    CMD_REPORT_DEVICE_RAW_ACCELEROMETER_MODE_STARTED_VIA_SERVER,
    CMD_REPORT_DEVICE_RAW_ACCELEROMETER_MODE_STOPPED_VIA_SERVER,

    CMD_SET_SHOW_LIFETOUCH_ACTIVITY_LEVEL_ENABLED_STATUS,
    CMD_GET_SHOW_LIFETOUCH_ACTIVITY_LEVEL_ENABLED_STATUS,
    CMD_REPORT_SHOW_LIFETOUCH_ACTIVITY_LEVEL_ENABLED_STATUS,

    REMOVED__CMD_DOWNLOAD_TEST_GATEWAY_APK_FROM_BUILD_SERVER,
    REMOVED__CMD_DOWNLOAD_TEST_LOGGER_APK_FROM_BUILD_SERVER,
    REMOVED__CMD_DOWNLOAD_TEST_USER_INTERFACE_APK_FROM_BUILD_SERVER,

    CMD_CLEAR_DESIRED_DEVICE,

    CMD_GET_DEVICE_INFO,

    CMD_RETRY_CONNECTING_TO_DEVICE,
    CMD_DISCONNECT_FROM_DESIRED_DEVICE,

    CMD_REPORT_DEVICE_BATTERY_LEVEL,
    REMOVED__CMD_GET_LAST_DEVICE_BATTERY_LEVEL,                                              // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_SET_AUTO_RESUME_ENABLED_STATUS,
    CMD_GET_AUTO_RESUME_ENABLED_STATUS,
    CMD_REPORT_AUTO_RESUME_ENABLED_STATUS,

    CMD_REPORT_BLE_DEVICE_CHANGE_SESSION_DISCONNECTED,

    REMOVED__CMD_SET_SWEETBLUE_BETWEEN_TASKS_DELAY_ENABLED_STATUS,                           // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_SWEETBLUE_BETWEEN_TASKS_DELAY_ENABLED_STATUS,                           // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_SWEETBLUE_BETWEEN_TASKS_DELAY_ENABLED_STATUS,                        // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_SET_SWEETBLUE_BETWEEN_TASKS_DELAY_TIME,                                     // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_GET_SWEETBLUE_BETWEEN_TASKS_DELAY_TIME,                                     // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_SWEETBLUE_BETWEEN_TASKS_DELAY_TIME,                                  // Not used anymore but acting as placeholder so upgrades go smoothly
    REMOVED__CMD_REPORT_BLOOD_PRESSURE_BLUE_BP05_DEVICE_INFO,                                // Not used anymore but acting as placeholder so upgrades go smoothly

    REMOVED__CMD_SET_SIMPLIFIED_BLUE_BP05_PLAN,
    REMOVED__CMD_GET_SIMPLIFIED_BLUE_BP05_PLAN,
    REMOVED__CMD_REPORT_SIMPLIFIED_BLUE_BP05_PLAN,
    REMOVED__CMD_START_BLUE_BP05_DATA_UPLOAD_FROM_DEVICE,
    REMOVED__CANCEL_BLUE_BP05_DATA_UPLOAD_FROM_DEVICE,
    REMOVED__REPORT_BLUE_BP05_DATA_UPLOAD_PROGRESS,
    REMOVED__CMD_START_BLUE_BP05_PLAN_DOWNLOAD_TO_DEVICE,
    REMOVED__CMD_CANCEL_BLUE_BP05_PLAN_DOWNLOAD_TO_DEVICE,
    REMOVED__CMD_REPORT_BLUE_BP05_PLAN_DOWNLOAD_PROGRESS,
    REMOVED__CMD_GET_BLUE_BP05_PLAN_RELEASED_STATUS,
    REMOVED__CMD_REPORT_BLUE_BP05_PLAN_RELEASED_STATUS,

    CMD_REPORT_DFU_PROGRESS,

    CMD_FORCE_CHECK_FOR_LATEST_FIRMWARE_IMAGES_FROM_SERVER,
    CMD_REPORT_CHECK_FOR_LATEST_FIRMWARE_IMAGES_FROM_SERVER_COMPLETE,

    CMD_VALIDATE_INSTALLATION_QR_CODE,
    CMD_REPORT_INSTALLATION_QR_CODE_DETAILS,

    CMD_GET_GATEWAY_CONFIG_FROM_SERVER,

    CMD_GET_INSTALLATION_COMPLETE,
    CMD_REPORT_INSTALLATION_COMPLETE,
    CMD_SET_INSTALLATION_COMPLETE,

    REMOVED__CMD_GET_DEVICE_SETUP_MODE_HISTORY,                                              // Not used anymore but acting as placeholder so upgrades go smoothly
    CMD_REPORT_DEVICE_SETUP_MODE_HISTORY,

    CMD_RESET_DATABASE_FAILED_TO_SEND_STATUS,

    CMD_GET_LOCATION_ENABLED,
    CMD_REPORT_LOCATION_ENABLED,

    CMD_STORE_VITAL_SIGN,

    CMD_SET_AUTO_UPLOAD_LOG_FILES_TO_SERVER_ENABLED_STATUS,
    CMD_GET_AUTO_UPLOAD_LOG_FILES_TO_SERVER_ENABLED_STATUS,
    CMD_REPORT_AUTO_UPLOAD_LOG_FILES_TO_SERVER_ENABLED_STATUS,

    CMD_SET_DEVICE_PERIODIC_MODE_PERIOD_TIME_IN_SECONDS,
    CMD_GET_DEVICE_PERIODIC_MODE_PERIOD_TIME_IN_SECONDS,
    CMD_REPORT_DEVICE_PERIODIC_MODE_PERIOD_TIME_IN_SECONDS,

    CMD_SET_DEVICE_PERIODIC_MODE_ACTIVE_TIME_IN_SECONDS,
    CMD_GET_DEVICE_PERIODIC_MODE_ACTIVE_TIME_IN_SECONDS,
    CMD_REPORT_DEVICE_PERIODIC_MODE_ACTIVE_TIME_IN_SECONDS,

    CMD_SET_WIFI_LOGGING_ENABLED_STATUS,
    CMD_GET_WIFI_LOGGING_ENABLED_STATUS,
    CMD_REPORT_WIFI_LOGGING_ENABLED_STATUS,

    CMD_SET_GSM_LOGGING_ENABLED_STATUS,
    CMD_GET_GSM_LOGGING_ENABLED_STATUS,
    CMD_REPORT_GSM_LOGGING_ENABLED_STATUS,

    CMD_SET_DATABASE_LOGGING_ENABLED_STATUS,
    CMD_GET_DATABASE_LOGGING_ENABLED_STATUS,
    CMD_REPORT_DATABASE_LOGGING_ENABLED_STATUS,

    CMD_SET_SERVER_LOGGING_ENABLED_STATUS,
    CMD_GET_SERVER_LOGGING_ENABLED_STATUS,
    CMD_REPORT_SERVER_LOGGING_ENABLED_STATUS,

    CMD_SET_BATTERY_LOGGING_ENABLED_STATUS,
    CMD_GET_BATTERY_LOGGING_ENABLED_STATUS,
    CMD_REPORT_BATTERY_LOGGING_ENABLED_STATUS,

    CMD_SET_DFU_BOOTLOADER_ENABLED_STATUS,
    CMD_GET_DFU_BOOTLOADER_ENABLED_STATUS,
    CMD_REPORT_DFU_BOOTLOADER_ENABLED_STATUS,

    CMD_REPORT_MANUAL_VITAL_SIGN_DEVICE_INFO,

    CMD_SET_AUTO_ENABLE_EWS__ENABLED_STATUS,
    CMD_GET_AUTO_ENABLE_EWS__ENABLED_STATUS,
    CMD_REPORT_AUTO_ENABLE_EWS__ENABLED_STATUS,

    REMOVED__CMD_GET_NONIN_WRIST_OX_OPERATING_MODE,                                          // Not used anymore but acting as placeholder so upgrades go smoothly

    CMD_REPORT_EWS_DOWNLOAD_SUCCESS,
    CMD_REPORT_SERVER_DATA_SYNC_STARTING_DUE_TO_MISSING_THRESHOLDS,

    CMD_TEST_ONLY_DELETE_EWS_THRESHOLDS,

    CMD_GET_REALTIME_SERVER_TYPE,
    CMD_REPORT_REALTIME_SERVER_TYPE,

    CMD_SPOOF_DEVICE_CONNECTION_STATE,
    CMD_TEST_ONLY_STOP_FAST_UI_UPDATES,

    CMD_REPORT_DEVICE_INFO,
    CMD_SET_DESIRED_DEVICE,

    CMD_REPORT_DEVICE_OPERATING_MODE,
    CMD_GET_DEVICE_OPERATING_MODE,

    CMD_GET_SERVER_CONFIGURABLE_TEXT_FROM_SERVER,

    CMD_SET_PREDEFINED_ANNOTATION_ENABLED_STATUS,
    CMD_GET_PREDEFINED_ANNOTATION_ENABLED_STATUS,
    CMD_REPORT_PREDEFINED_ANNOTATION_ENABLED_STATUS,
    
    CMD_REPORT_NFC_TAG,

    CMD_CRASH_PATIENT_GATEWAY_ON_DEMAND,

    CMD_GET_LAST_NETWORK_STATUS,

    CMD_START_NONIN_PLAYBACK_SIMULATION_FROM_FILE,

    CMD_REFRESH_DEVICE_CONNECTION_STATE,

    CMD_REPORT_NONIN_PLAYBACK_IS_ONGOING,
    CMD_REPORT_NONIN_PLAYBACK_MIGHT_OCCUR,

    CMD_GET_REALTIME_SERVER_CONNECTED_STATUS,

    CMD_SET_DISPLAY_TIMEOUT_IN_SECONDS,
    CMD_GET_DISPLAY_TIMEOUT_IN_SECONDS,
    CMD_REPORT_DISPLAY_TIMEOUT_IN_SECONDS,

    CMD_STORE_AUDIT_TRAIL_EVENT,

    CMD_FROM_SERVER__SET_DISPLAY_TIMEOUT_IN_SECONDS,

    CMD_SET_DISPLAY_TIMEOUT_APPLIES_TO_PATIENT_VITALS_DISPLAY,
    CMD_GET_DISPLAY_TIMEOUT_APPLIES_TO_PATIENT_VITALS_DISPLAY,
    CMD_REPORT_DISPLAY_TIMEOUT_APPLIES_TO_PATIENT_VITALS_DISPLAY,

    CMD_FROM_SERVER__SET_DISPLAY_TIMEOUT_APPLIES_TO_PATIENT_VITALS_DISPLAY,

    CMD_REPORT_SOFTWARE_UPDATE_AVAILABLE,
    CMD_ENTER_SOFTWARE_UPDATE_MODE,
    CMD_REPORT_IN_SOFTWARE_UPDATE_MODE,
    CMD_SOFTWARE_UPDATE_COMPLETE,
    CMD_GET_SOFTWARE_UPDATE_STATE,
    CMD_TEST_ONLY_SPOOF_SOFTWARE_UPDATE_INSTALLED,

    CMD_START_SERVER_DATA_DOWNLOAD,
    CMD_REPORT_SERVER_DATA_SYNC_STARTING_DUE_TO_SOFTWARE_UPDATE,
    CMD_FORCE_SERVER_DATA_DOWNLOAD,
    CMD_SERVER_SYNC_DATA_IN_TEST_MODE,

    CMD_FROM_SERVER__REQUEST_DATABASE_SYNC_STATUS,
    CMD_FROM_SERVER__REQUEST_ALL_DEVICE_INFO_OBJECTS,

    CMD_FROM_SERVER__DELETE_EXPORTED_DATABASES,
    CMD_FROM_SERVER__ENABLE_GSM_MODE,
    CMD_FROM_SERVER__SET_DEVELOPER_POPUP_ENABLED_STATUS,
    CMD_FROM_SERVER__SET_USA_MODE_ENABLED_STATUS,

    CMD_SET_SPO2_SPOT_MEASUREMENTS_ENABLED_STATUS,
    CMD_GET_SPO2_SPOT_MEASUREMENTS_ENABLED_STATUS,
    CMD_REPORT_SPO2_SPOT_MEASUREMENTS_ENABLED_STATUS,

    CMD_FROM_SERVER__SET_LONG_TERM_MEASUREMENT_TIMEOUT_IN_SECONDS,

    CMD_FROM_SERVER__SET_PERIODIC_MODE_ACTIVE_TIME_IN_SECONDS,
    CMD_FROM_SERVER__SET_PERIODIC_MODE_PERIOD_TIME_IN_SECONDS,

    CMD_FROM_SERVER__SET_PERCENTAGE_OF_POOR_SIGNAL_HEART_BEATS_BEFORE_MINUTE_MARKED_INVALID,

    CMD_FROM_SERVER__SPO2_SPOT_MEASUREMENTS_ENABLED_STATUS,

    CMD_FROM_SERVER__SHOW_SERVER_SYNCING_POPUP,
    CMD_SHOW_SERVER_SYNCING_POPUP,

    CMD_FROM_SERVER__ENABLE_PRE_DEFINED_ANNOTATIONS,
    CMD_FROM_SERVER__ENABLE_AUTO_ADD_EWS,
    CMD_FROM_SERVER__ENABLE_DFU,

    REMOVED_CMD_FROM_SERVER__ENABLE_PULSE_TRANSIT_TIME,
    CMD_FROM_SERVER__ENABLE_SHOW_NUMBERS_ON_BATTERY_INDICATOR,
    CMD_FROM_SERVER__ENABLE_SHOW_IP_ADDRESS_ON_WIFI_POPUP,
    CMD_FROM_SERVER__ENABLE_RUN_DEVICES_IN_TEST_MODE,
    CMD_FROM_SERVER__ENABLE_SHOW_MAC_ADDRESS,
    CMD_FROM_SERVER__ENABLE_LIFETOUCH_ACTIVITY_LEVEL,
    CMD_FROM_SERVER__ENABLE_SESSION_AUTO_RESUME,
    CMD_FROM_SERVER__ENABLE_AUTO_UPLOAD_LOG_FILES_TO_SERVER,
    CMD_FROM_SERVER__ENABLE_MANUFACTURING_MODE,
    CMD_FROM_SERVER__ENABLE_USE_BACK_CAMERA,
    CMD_FROM_SERVER__ENABLE_PATIENT_ORIENTATION,
    CMD_FROM_SERVER__ENABLE_WIFI_LOGGING,
    CMD_FROM_SERVER__ENABLE_GSM_LOGGING,
    CMD_FROM_SERVER__ENABLE_DATABASE_LOGGING,
    CMD_FROM_SERVER__ENABLE_SERVER_LOGGING,
    CMD_FROM_SERVER__ENABLE_BATTERY_LOGGING,
    CMD_FROM_SERVER__SET_LIFETEMP_MEASUREMENT_INTERVAL_IN_SECONDS,
    CMD_FROM_SERVER__SET_PATIENT_ORIENTATION_MEASUREMENT_INTERVAL_IN_SECONDS,

    CMD_FORWARD_VIDEO_CALL_REQUEST_TO_USER_INTERFACE,
    CMD_FORWARD_VIDEO_CALL_JOIN_TO_USER_INTERFACE,
    CMD_FROM_SERVER__REQUEST_VIDEO_CALL,
    CMD_FROM_SERVER__JOIN_VIDEO_CALL,
    CMD_FROM_SERVER__SERVER_DECLINED_VIDEO_CALL,
    CMD_FROM_SERVER__UPDATE_VIDEO_CALL_CONNECTION_ID,
    CMD_FORWARD_VIDEO_CALL_DECLINED_TO_USER_INTERFACE,
    CMD_FORWARD_VIDEO_CALL_BROWSER_CONNECTION_ID_TO_USER_INTERFACE,
    CMD_REPORT_GATEWAY_VIDEO_CALL_STATUS,

    CMD_REPORT_BLUETOOTH_OFF_ERROR,

    CMD_SET_VIDEO_CALLS_ENABLED_STATUS,
    CMD_GET_VIDEO_CALLS_ENABLED_STATUS,
    CMD_REPORT_VIDEO_CALLS_ENABLED_STATUS,

    CMD_REQUEST_PATIENT_SPECIFIC_VIDEO_CALL_CONTACTS_FROM_SERVER,
    CMD_REPORT_PATIENT_SPECIFIC_VIDEO_CALL_CONTACTS_RECEIVED_FROM_SERVER,

    CMD_FROM_SERVER__REPORT_PATIENT_SPECIFIC_VIDEO_CALL_CONTACTS_UPDATE,

    CMD_PATIENT_ON_GATEWAY_REQUESTING_VIDEO_CALL,
    CMD_PATIENT_ON_GATEWAY_CANCELLED_VIDEO_CALL_REQUEST,

    CMD_DUMMY_QR_CODE_UNLOCK_USER,
    CMD_DUMMY_QR_CODE_UNLOCK_ADMIN,

    CMD_FROM_SERVER__ENABLE_VIDEO_CALLS,

    CMD_SET_REALTIME_SERVER_TYPE,

    CMD_SET_DISPLAY_TEMPERATURE_IN_FAHRENHEIT_ENABLED_STATUS,
    CMD_GET_DISPLAY_TEMPERATURE_IN_FAHRENHEIT_ENABLED_STATUS,
    CMD_REPORT_DISPLAY_TEMPERATURE_IN_FAHRENHEIT_ENABLED_STATUS,

    CMD_SET_DISPLAY_WEIGHT_IN_LBS_ENABLED_STATUS,
    CMD_GET_DISPLAY_WEIGHT_IN_LBS_ENABLED_STATUS,
    CMD_REPORT_DISPLAY_WEIGHT_IN_LBS_ENABLED_STATUS,

    CMD_FROM_SERVER__EXIT_GATEWAY,
    CMD_TELL_USER_INTERFACE_TO_EXIT,

    CMD_IMPORT_DATABASE_FROM_ANDROID_ROOT,

    CMD_GET_FREE_DISK_SPACE,
    CMD_REPORT_FREE_DISK_SPACE,

    CMD_SET_LT3_KHZ_SETUP_MODE_ENABLED_STATUS,
    CMD_GET_LT3_KHZ_SETUP_MODE_ENABLED_STATUS,
    CMD_REPORT_LT3_KHZ_SETUP_MODE_ENABLED_STATUS,

    CMD_RECALCULATE_THRESHOLDS_AFTER_SERVER_CONFIG_RECEIVED,

    CMD_SET_VIEW_WEBPAGES_ENABLED_STATUS,
    CMD_GET_VIEW_WEBPAGES_ENABLED_STATUS,
    CMD_REPORT_VIEW_WEBPAGES_ENABLED_STATUS,

    CMD_GET_VIEWABLE_WEB_PAGES_FROM_SERVER,

    CMD_REMOVE_VITAL_SIGN_TYPE_FROM_EWS,
}
