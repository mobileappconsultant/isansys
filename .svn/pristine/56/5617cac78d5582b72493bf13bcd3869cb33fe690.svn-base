#include "variant.h"
#include <stdio.h>
#include <adk.h>


uint8_t nfc_value[16];
int nfc_byte_number = 0;

/**
 * USB definitions (Needs USBHost library (only for Arduino Due))
 */
 
// Accessory descriptor. It's how Arduino identifies itself to Android.
char applicationName[] = "Patient Gateway"; // the app on your phone
char accessoryName[] = "Arduino Due"; // your Arduino board
char companyName[] = "isansys";
 
// Make up anything you want for these
char versionNumber[] = "0.1";
char serialNumber[] = "1";
char url[] = "";
 
USBHost Usb;
ADK adk(&Usb, companyName, applicationName, accessoryName, versionNumber, url, serialNumber);

#define RCVSIZE 128

byte counter = 0;
byte bytes_to_send = 0;

// USB variables
uint8_t buf[RCVSIZE];
uint32_t nbread = 0;
char pong[] = "pong";

uint8_t tx_buf[RCVSIZE];
  
/**
 * NFC definitions
 */

const int IRQPin = 9;  // Sends wake-up pulse
const int PWR = 7;    // Power for NFC chip

byte rx_buffer[40];    // Receive buffer

// NFC Reader Commands
#define    IDN                    (0x01)
#define    PROTOCOL_SELECT        (0x02)
#define    SEND_RECV              (0x04)
#define    IDLE                   (0x07)
#define    RD_REG                 (0x08)
#define    WR_REG                 (0x09)
#define    BAUDRATE               (0x0A)
#define    ECHO                   (0x55)


enum NfcStateE {
  SYNCING,
  SYNCING_RESPONSE,
  IDN_RESPONSE,
  SET_PROTOCOL_RESPONSE,
  CHECK_FOR_TAG_RESPONSE,
  READ_BLOCK_RESPONSE,
  NFC_DATA_RECEIVED,
  DONE,
};


NfcStateE nfc_state = SYNCING;
NfcStateE nfc_state_at_last_ping = DONE;

// NFC variables
byte received_byte_counter;
byte length_of_data;
boolean tag_present;
byte block_number_being_received;

/**
 * NRF reader functions taken from other sketch...
 */


void transmitToNfc(byte value)
{
//  Serial.print("TX -> 0x");
//  Serial.println(value, HEX);

  Serial1.write((byte)value);
}


void emptyIncomingSerialBuffer()
{
  Serial.println("Emptying incoming buffered data from NFC");

  while (Serial1.available())
  {
    byte value = Serial1.read();
    Serial.print("Dropped byte 0x");
    Serial.println(value, HEX);
  }
}


void sendSync()
{
  Serial.println("Sending ECHO");

  transmitToNfc(ECHO);

  received_byte_counter = 0;
  nfc_state = SYNCING_RESPONSE;
}


void sendIdn()
{
  Serial.println("Sending IDN");
  
  transmitToNfc(IDN);         // NFC Reader Command
  transmitToNfc(0x00);        // Length of data to follow
  
  received_byte_counter = 0;
  nfc_state = IDN_RESPONSE;
}


void checkForTag()
{
  Serial.println("Sending Check For Tag");

  transmitToNfc(SEND_RECV);   // NFC Reader Command
  transmitToNfc(0x03);        // Length of data that follows
  transmitToNfc(0x26);        // Request Flags byte
  transmitToNfc(0x01);        // Inventory Command for ISO/IEC 15693
  transmitToNfc(0x00);        // Mask length for inventory command

  received_byte_counter = 0;
  nfc_state = CHECK_FOR_TAG_RESPONSE;
}


void setProtocol()
{
  Serial.println("Sending SET PROTOCOL");
  
  transmitToNfc(PROTOCOL_SELECT);  // Reader Command
  transmitToNfc(0x02);          // Length of data to follow
  transmitToNfc(0x01);          // Protocol : ISO/IEC 15693
  transmitToNfc(0x0D);          // Wait for SOF, 10% modulation, append CRC
  
  received_byte_counter = 0;
  nfc_state = SET_PROTOCOL_RESPONSE;
}


void readBlock(byte block_number)
{
  Serial.println("Sending Read Block");

  transmitToNfc(SEND_RECV);     // Reader Command
  transmitToNfc(0x03);          // Length of data that follows
  transmitToNfc(0x02);          // NFC Chip Command : Request Flags
  transmitToNfc(0x20);          // Read Block Command
  transmitToNfc(block_number);  // Block Number

  block_number_being_received = block_number;

  received_byte_counter = 0;
  nfc_state = READ_BLOCK_RESPONSE;
}


/** In theory will set the chip into idle mode so it wakes up when it detects a tag
 *  but it's not currently working.
 */
void sendIdleCommand()
{
    Serial.println("Sending IDLE command");
    
    transmitToNfc(IDLE);      // Send Receive CR95HF command
    transmitToNfc(0x0E);      // Length of data that follows
    
    transmitToNfc(0x02);      // Wake Up Source : Tag detection
    transmitToNfc(0x21);      // Enter Control Hi : 0x2100 = Tag Detection
    transmitToNfc(0x00);      // Enter Control Lo : 
    transmitToNfc(0x79);      // Wake Up Control Hi : 0x7901 = Tag Detection
    transmitToNfc(0x01);      // Wake Up Control Lo : 
    transmitToNfc(0x18);      // Leave Control Hi : 0x1800 = Tag Detection
    transmitToNfc(0x00);      // Leave Control Lo : 
    transmitToNfc(0x20);      // Wake Up Period : This byte is the coefficient used to adjust the time allowed between two tag detections. Also used to specify the duration before Timeout. (Typical value: 0x20)
                            //   Duration before Timeout = 256 * tL * (WU period + 2) * (MaxSleep + 1)
    transmitToNfc(0x60);      // Osc Start : 0x60 default
    transmitToNfc(0x60);      // DAC Start : 0x60 default
    transmitToNfc(0x64);      // DAC Data : 0x64 default
    transmitToNfc(0x74);      // DAC Data : 0x74 default
    transmitToNfc(0x3F);      // Swing Count : 0x37 default
    transmitToNfc(0x08);      // Max Sleep : This byte defines the maximum number of tag detection trials or the coefficient to adjust the maximum inactivity duration before Timeout.
}


void setup() 
{
    // Serial port for NFC chip
    Serial1.begin(57600);
    
    // Arduino Debug serial port
    Serial.begin(115200);
    while (!Serial)
    {
        ; // wait for serial port to connect. Needed for native USB port only
    }

    Serial.println("\r\nArduino Booted!");

    pinMode(IRQPin, OUTPUT);
    digitalWrite(IRQPin, HIGH);
  
    pinMode(PWR, OUTPUT);
    digitalWrite(PWR, LOW);
    delay(10);
    digitalWrite(PWR, HIGH);
    delay(20);
  
    // The CR95HF requires a wakeup pulse on its IRQ_IN pin before it will select UART or SPI mode.
    digitalWrite(IRQPin, LOW);
    delay(10);
    digitalWrite(IRQPin, HIGH);
    delay(20);

    cpu_irq_enable();
   
    delay(200);
        
    Serial.println("Finished Start");
}




/**
 * Main loop - handles all processing in Arduino
 */

void loop() 
{
  // Sync with NFC chip if necessary
  if (nfc_state == SYNCING)
  {
    Serial.println("Calling sendSync");
    sendSync();
  }


  // Now handle USB communications...
  Usb.Task();

  if (adk.isReady()) 
  {
    /* Read data from ADK and print to UART */
    adk.read(&nbread, RCVSIZE, buf);
    if (nbread > 0)
    {
      // Not sure why this is here but appears to be neccessary to do a write followed by a small delay here so that the data goes though later...          
      adk.write(4, (uint8_t *)pong);

      // Seems that the serial prints allow a small delay so that the adk.write below goes through???
      Serial.print("Number read = ");
      Serial.print(nbread);
      Serial.print(" : ");
      
      String s = "";
      for (uint32_t i = 0; i < nbread; ++i) 
      {
        s += (char)buf[i];
      }

      Serial.println(s);

      if(nfc_state == NFC_DATA_RECEIVED)
      {
        
        adk.write(16, nfc_value);

        nfc_state = DONE;
      }
      else if(nfc_state == DONE)
      {
          checkForTag();
      }
      else
      {
          // we've got a ping but we're not in an expected stae...
          if((nfc_state == READ_BLOCK_RESPONSE)
              && (nfc_state_at_last_ping == READ_BLOCK_RESPONSE))
          {
            // assume we've locked up...
            Serial.println("ALERT! STATE MACHINE LOCKED UP!");

            nfc_byte_number = 0;
            nfc_state = DONE;
          }
      }

      nfc_state_at_last_ping = nfc_state;
      
      Serial.println("Tx'ed String");
    }
  }



  // Now handle NFC data
  //  // If anything comes from NFC chip
  if (Serial1.available())
  {
    char value = Serial1.read();

    rx_buffer[received_byte_counter] = (byte)value;
    received_byte_counter++;


//    Serial.print("RX <- 0x");
//    Serial.print(value, HEX);
//    Serial.print(" : ");
//    Serial.println(value);


    // Response from NFC state machine
    switch (nfc_state)
    {
      case SYNCING_RESPONSE:
        {
          if (value == 0x55)
          {
            Serial.println("Synced");

            delay(250);
            emptyIncomingSerialBuffer();

            sendIdn();
          }
          else
          {
            Serial.println("Not Synced");
            //delay(500);
            delay(250);
            nfc_state = SYNCING;
          }
        }
        break;

      case IDN_RESPONSE:
        {
          switch (received_byte_counter)
          {
            case 1:                // Result Code
              {
                Serial.print("IDN_RESPONSE Result Code = ");
                Serial.println(value, HEX);
              }
              break;

            case 2:                // Length of data
              {
                length_of_data = value;

                Serial.print("IDN_RESPONSE Length of data = ");
                Serial.println(length_of_data, HEX);
              }
              break;
          }

          default:
          {
            if (received_byte_counter == (2 + length_of_data))
            {
              Serial.println("IDN Fully Received");

              setProtocol();
            }
          }
        }
        break;

      case SET_PROTOCOL_RESPONSE:
        {
          switch (received_byte_counter)
          {
            case 1:                // Result Code
              {
                Serial.print("SET_PROTOCOL_RESPONSE Result Code = ");
                Serial.println(value, HEX);
              }
              break;

            case 2:                // Length of data
              {
                length_of_data = value;

                Serial.print("SET_PROTOCOL_RESPONSE Length of data = ");
                Serial.println(length_of_data, HEX);

                Serial.println("SET_PROTOCOL_RESPONSE done");

                nfc_state = DONE;
              }
              break;
          }
        }
        break;

      case CHECK_FOR_TAG_RESPONSE:
        {
          switch (received_byte_counter)
          {
            case 1:                // Result Code
              {
                if (value == 0x80)
                {
                  tag_present = true;
                }
                else
                {
                  tag_present = false;
                }
              }
              break;

            case 2:                // Length of data
              {
                length_of_data = value;

                if (tag_present == false)
                {
//                  Serial.println("Tag NOT Found");

                  nfc_state = DONE;
                }
                else
                {
                  Serial.println("Tag Found");
                }
              }
              break;

            default:
              {
                if (received_byte_counter == (2 + length_of_data))
                {
                  Serial.println("Tag ID fully received");

                  readBlock(00);
                }
              }
          }
        }
        break;

        case READ_BLOCK_RESPONSE:
        {
          switch (received_byte_counter)
          {
            case 1:
              {
                // Result Code
                //Serial.print("READ_BLOCK_RESPONSE Result Code = ");
                //Serial.println(value, HEX);

                //Serial.print("block_number_being_received = ");
                //Serial.println(block_number_being_received);
              }
              break;

            case 2:
              {
                // Length of data
                length_of_data = value;

                //Serial.print("READ_BLOCK_RESPONSE Length of data = ");
                //Serial.println(length_of_data, HEX);
              }
              break;

            default:
              {
                if (received_byte_counter == (2 + length_of_data))
                {
                  Serial.print("                          Block Data = ");
                  
                  for (byte i=0; i<4; i++)
                  {
                    Serial.print(rx_buffer[i+3], HEX);
                    Serial.print(" ");
                    
                    nfc_value[nfc_byte_number++] = rx_buffer[i+3];
                  }
                  Serial.println("");

                  if (block_number_being_received != 3)
                  {
                    block_number_being_received++;
                    
                    readBlock(block_number_being_received);
                  }
                  else
                  {
                    nfc_byte_number = 0;
                    nfc_state = NFC_DATA_RECEIVED;
                  }
                }
              }
          }
        }
        break;
    }
  }
}

