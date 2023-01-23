import sys
import os.path

arguments = sys.argv[1:]
count = len(arguments)
if count == 0:
    print "Usage: python AndroidLogFile.py inputfile.txt outputfile.txt"
    sys.exit (1)

input_filename = arguments[0]
output_filename = arguments[1]

print input_filename
print output_filename

if (not os.path.isfile(input_filename)):
    print"Cant find the input file"
    sys.exit (1)

# Open the input file
input_file = open(input_filename, 'r')

# Open the output file
output_file = open(output_filename, "w")


for line in input_file:
    words = line.split();                                                       # Use white space to split up the lines

    number_of_words = len(words)

    filename = words[2]                                                         # E.g. "I/BluetoothLeService(16907):". The number in the brackets is different on each run. Suspect its a process ID

    start = filename.find( '(' )                                                # Remove the text between the brackets
    end = filename.find( ')' )
    if start != -1 and end != -1:
        filename = filename[:start] + filename[end+1:]

    #print filename,
    #for x in xrange(3, number_of_words):
    #    print words[x],
    #print

    output_file.write(filename)
    output_file.write(" ")
    for x in xrange(3, number_of_words):
        output_file.write(words[x])
        output_file.write(" ")
    output_file.write("\n")

# Close opened files
input_file.close()
output_file.close()