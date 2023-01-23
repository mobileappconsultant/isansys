import datetime
from datetime import timedelta
import sys
import os
#TODAYS DATE date = datetime.date.today()

# to do :  make times v v flexible, i.e. enter number of hours

numberOfReadingsAdded = 0

class NoninTime:
  def __init__ (self, hour, minute, second, day, month, year):
	self.month = month
	self.year = year
	self.hour = hour
	self.minute = minute
	self.second = second
	self.day = day
  
#class Study:
  #def __init__ (self, writeTime, startTime, endTime):
#	self.writeTime = writeTime
	#self.startTime = startTime
	#self.endTime = endTime

class Study:
   def __init__ (startHour, startMin, endHour, endMin, spoValue):
     self.startHour = startHour
     self.startMin = startMin
     self.endHour = endHour
     self.endMin = endMin
     self.spoValue = spoValue

class NoninReading:
  def __init__ (self, spo2, pulse):
    self.spo2 = spo2
    self.pulse = pulse
    self.checksum = hex(spo2 + pulse)

# Always returns 2 digits for values 0-255 and without the "0x" of the Python hex fn.
def hexXX(a):
  return '{:02X}'.format(0 if a < 0 else 255 if a > 255 else a)
	
def checksum2bytes(a,b):
	return hexXX(a+b)

# returns hex values as string with associated checksum
def hexify(a,b):   
    return hexXX(a) + " " + hexXX(b) + " " + checksum2bytes(a,b) + " "

def makeStudy(spo2, startTimeAsPython, endTimeAsPython):
  #print ('STUDY ' + str(startTimeAsPython) + " --> " + str(endTimeAsPython) + "  " + str(spo2))
  
  thisStudy = "FE FD FB 01 02 03 "
  
  # Write time - not used by Isansys
  thisStudy += hexify(endTimeAsPython.month, endTimeAsPython.day) + hexify(endTimeAsPython.year, endTimeAsPython.minute) + hexify(endTimeAsPython.second, endTimeAsPython.hour)
  # End time of study
  thisStudy += hexify(endTimeAsPython.month, endTimeAsPython.day) + hexify(endTimeAsPython.year, endTimeAsPython.minute) + hexify(endTimeAsPython.second, endTimeAsPython.hour)
  #Start time of study
  thisStudy += hexify(startTimeAsPython.month, startTimeAsPython.day) + hexify(startTimeAsPython.year, startTimeAsPython.minute) + hexify(startTimeAsPython.second, startTimeAsPython.hour)

  difference = endTimeAsPython - startTimeAsPython
  difference_in_seconds = difference.total_seconds()
  #number_of_readings_required = int(difference_in_seconds / 4)  # use int to round down
  
  number_of_readings_required = int(difference_in_seconds)  # going for 1 reading per second
  
  print ('STUDY ' + str(startTimeAsPython) + " --> " + str(endTimeAsPython) + "  " + str(spo2) + "  " + str(number_of_readings_required) + " readings " )
  secondsAdded = 0
 
  spo2FakeValue = 1
  if (desiredSpo2 == 102):
    spo2FakeValue = 80
	
  spo2ControlledIncrease = 0
  spo2FakeValuesIncreasing = True

  for x in range(0, number_of_readings_required):
    	
    if (desiredSpo2 < 101):
	  thisStudy = thisStudy + "58 " + hexXX(desiredSpo2) + " A4 "
    else:
      if (desiredSpo2 == 101):
        thisStudy += "58 " + hexXX(spo2FakeValue) + " A4 "
	
        if (spo2FakeValuesIncreasing == True):
          spo2FakeValue = spo2FakeValue + 1
          if (spo2FakeValue == 100):
	        spo2FakeValuesIncreasing = False
        else:
          spo2FakeValue = spo2FakeValue - 1
          if (spo2FakeValue == 1):
	        spo2FakeValuesIncreasing = True
	  
      if (desiredSpo2 == 102):
         thisStudy += "58 " + hexXX(spo2FakeValue) + " A4 "
         spo2ControlledIncrease = spo2ControlledIncrease + 1
         if (spo2ControlledIncrease == 3):
           if (spo2FakeValue < 100):
             spo2ControlledIncrease  = 0
             spo2FakeValue = spo2FakeValue + 1
	  
      
    secondsAdded += 4
		
  return thisStudy

defaultUserSpecifiesEverything = True
editStartDate = False
twentyFourHours = False
xAmountOfHours = False

if len(sys.argv) > 1:
  if str(sys.argv[1]) == "editstartday":
	editStartDate = True
  if str(sys.argv[1]) == "24hrs":
    twentyFourHours = True
  if str(sys.argv[1]) == "xhrs":
    xAmountOfHours = True

if (twentyFourHours or xAmountOfHours):
  defaultUserSpecifiesEverything = False

fileOut = open("playbackFile.txt", "w+")
print ('Parameters:  (use without parameters to specify each study)')
print ('  24hrs  -  24 hrs ')
print ('  editstartday - specify start dates before today')
print ('  xhrs - specify hours, study quantity & gap between')


#if twentyFourHours == True:
#  numberOfStudies = 12
#else:
#  numberOfStudies = input()

allStudies = []

playback = "06 "

now = datetime.datetime.today()    
startYear = now.year - 2000
startSecond = now.second

# "Default" use , user selects
if defaultUserSpecifiesEverything == True:
  print('Number of studies (start with the most recent first)')
  numberOfStudies = input()
  for x in range (0, numberOfStudies):
    print('-------- STUDY ' + str(x) + '--------')    
    if editStartDate == True:
      print " >>> edit start day <<<"
      print "Date "
      startDay = input()
      print "Month "
      startMonth = input()
    else:
      startMonth = now.month
      startDay = now.day
	
    print('Start hour')
    startHour = input()
    print('Start minute')
    startMinute = input()
    print('No. of hours')
    hoursOfPlayback = input()
    print('SpO2 value [ 101 = 0-100 up then 100-0 down .... ]  [ 102 = 80 x 3, 81 x 3 ... 100 only ]')
    desiredSpo2 = input()
  
    
    startTimeAsPython = datetime.datetime(startYear, startMonth, startDay, startHour, startMinute, startSecond)
    endTimeAsPython = startTimeAsPython + timedelta(hours=hoursOfPlayback)
  
    playback += makeStudy(desiredSpo2, startTimeAsPython, endTimeAsPython )
  
if twentyFourHours == True:
  print('No. of studies')
  numberOfStudies = input()
  hoursPerStudy = int(24 / numberOfStudies)
  print('SpO2 increase between studies')
  spo2IncreaseBetweenStudies = input()
  
  desiredSpo2 = 52
  endTimeOfPlayback = datetime.datetime(startYear, now.month, now.day, now.hour, now.minute, startSecond)
  endTimeOfPlayback-=timedelta(minutes=5)
  
  startTimes = []
  for x in range(1, numberOfStudies+1):
    studyStartTime = endTimeOfPlayback - (timedelta(hours=hoursPerStudy) * x)
    studyStartTime += timedelta(minutes=3)
    startTimes.append(studyStartTime)    
  
  for x in range (0, numberOfStudies):
    startTimeAsPython = startTimes[x]
    endTimeAsPython = startTimeAsPython + timedelta(hours = hoursPerStudy)
    endTimeAsPython -= timedelta(minutes=3)	
	
    playback += makeStudy(desiredSpo2, startTimeAsPython, endTimeAsPython)   
	
    desiredSpo2 += spo2IncreaseBetweenStudies
    if (desiredSpo2 > 100):
      desiredSpo2 = 100
	  
    
if xAmountOfHours == True:
  print('No. of hours')
  xHoursAmount = input()
  print('No. of studies')
  numberOfStudies = input()
  hoursPerStudy = int(xHoursAmount / numberOfStudies)
  print('SpO2 increase between studies')
  spo2IncreaseBetweenStudies = input()
  print('Gap between studies in minutes')
  gapBetweenStudies = input()
  desiredSpo2 = 52
  endTimeOfPlayback = datetime.datetime(startYear, now.month, now.day, now.hour, now.minute, startSecond)
  endTimeOfPlayback-=timedelta(minutes=5)
  
  
  startTimeOfPlayback = endTimeOfPlayback - timedelta(hours=xHoursAmount)
  allGapsInMinutes = gapBetweenStudies * (numberOfStudies - 1)
  startTimeOfPlayback -= timedelta(minutes=allGapsInMinutes)
  
  startTimes = []
  for x in range(1, numberOfStudies+1):
    studyStartTime = endTimeOfPlayback - (timedelta(hours=hoursPerStudy) * x)
    studyStartTime += timedelta(minutes=3)
    startTimes.append(studyStartTime)    
  
  for x in range (0, numberOfStudies):
    startTimeAsPython = startTimes[x]
    endTimeAsPython = startTimeAsPython + timedelta(hours = hoursPerStudy)
    endTimeAsPython -= timedelta(minutes=3)	
	
    playback += makeStudy(desiredSpo2, startTimeAsPython, endTimeAsPython)   
	
    desiredSpo2 += spo2IncreaseBetweenStudies
    if (desiredSpo2 > 100):
      desiredSpo2 = 100


#Closing header including current time of Nonin (not required)
playback += "FE FD FB 04 02 06 00 00 00 00 00 00 00 00 00 "

#Checksum
checksum = 0
playback += "EF 11 22 "

# ByteCount
bytecount = len(playback) / 3
bytecountLsb = bytecount % 256
bytecountMsb = int (bytecount / 256)
playback += "EE " + hexXX(bytecountMsb) + " " + hexXX(bytecountLsb) + " "

playback += "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"


fileOut.write(playback)
fileOut.close()

print "-----------------------------"

os.system('explorer .')
