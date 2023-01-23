package com.isansys.common.enums;

/** All Possible device types in the system.
 *
 *  This MUST be kept up-to-date with the DeviceType enum in the Server code
 *
 **/
public enum DeviceType
{
    DEVICE_TYPE__INVALID,                                                               // 0
    DEVICE_TYPE__LIFETOUCH,                                                             // 1
    REMOVED__DEVICE_TYPE__LIFETEMP,                                                     // 2
    DEVICE_TYPE__NONIN_WRIST_OX,                                                        // 3
    DEVICE_TYPE__AND_UA767,                                                             // 4
    REMOVED__DEVICE_TYPE__LIFEOX,                                                       // 5
    DEVICE_TYPE__EARLY_WARNING_SCORE,                                                   // 6    Not a real Device Type, but the code is setup to expect this
    REMOVED__DEVICE_TYPE__PULSE_TRANSIT_TIME,                                           // 7    Not a real Device Type, but the code is setup to expect this
    DEVICE_TYPE__MANUALLY_ENTERED_HEART_RATE,                                           // 8
    DEVICE_TYPE__MANUALLY_ENTERED_RESPIRATION_RATE,                                     // 9
    DEVICE_TYPE__MANUALLY_ENTERED_TEMPERATURE,                                          // 10
    DEVICE_TYPE__MANUALLY_ENTERED_SPO2,                                                 // 11
    DEVICE_TYPE__MANUALLY_ENTERED_BLOOD_PRESSURE,                                       // 12
    REMOVED__DEVICE_TYPE__ABPM_05,                                                      // 13
    DEVICE_TYPE__MANUALLY_ENTERED_ANNOTATION,                                           // 14
    DEVICE_TYPE__MANUALLY_ENTERED_SUPPLEMENTAL_OXYGEN,                                  // 15
    DEVICE_TYPE__MANUALLY_ENTERED_CONSCIOUSNESS_LEVEL,                                  // 16
    DEVICE_TYPE__MANUALLY_ENTERED_CAPILLARY_REFILL_TIME,                                // 17
    DEVICE_TYPE__MANUALLY_ENTERED_RESPIRATION_DISTRESS,                                 // 18
    DEVICE_TYPE__MANUALLY_ENTERED_FAMILY_OR_NURSE_CONCERN,                              // 19
    DEVICE_TYPE__NONIN_WRIST_OX_BTLE,                                                   // 20
    DEVICE_TYPE__LIFETOUCH_BLUE_V2,                                                     // 21
    DEVICE_TYPE__AND_ABPM_TM2441,                                                       // 22
    DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__PATIENT_GATEWAY,                           // 23
    REMOVED__DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__LOGGER,                           // 24
    DEVICE_TYPE__GATEWAY_TABLET_INFORMATION__USER_INTERFACE,                            // 25
    DEVICE_TYPE__AND_UA651,                                                             // 26
    DEVICE_TYPE__LIFETEMP_V2,                                                           // 27
    DEVICE_TYPE__FORA_IR20,                                                             // 28
    DEVICE_TYPE__INSTAPATCH,                                                            // 29
    DEVICE_TYPE__LIFETOUCH_THREE,                                                       // 30
    DEVICE_TYPE__NONIN_3230,                                                            // 31
    DEVICE_TYPE__MASIMO_RADIUS_PPG,                                                     // 32
    DEVICE_TYPE__AND_UC352BLE,                                                          // 33
    DEVICE_TYPE__MANUALLY_ENTERED_WEIGHT,                                               // 34
    DEVICE_TYPE__MEDLINKET,                                                             // 35
    DEVICE_TYPE__AND_UA1200BLE,                                                         // 36
    DEVICE_TYPE__AND_UA656BLE,                                                          // 37
    DEVICE_TYPE__MANUALLY_ENTERED_URINE_OUTPUT,                                         // 38
}