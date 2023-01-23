using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;

// Comparing System Commands in Android Gateway to Android UI to ensure that the lists of commands are identical
// Intended to run from project root (same place as where AndroidManifestSvnBuildNumber.exe is run)
// and called from project root build.gradle checkSystemCommandsInSync()
// Assumptions: no more than 1 comment per line, no block comments, no comments before CMD_s

namespace SystemCommandsSynchronisedChecker
{
    class Program
    {

        public static int Problems { get; set; } = 0;


        // Try to obtain just the commands between "public enum Commands {"  and "}"
        public static List<String> getCommands(String[] allLines, String filename)
        {
            List<String> returnedList = new List<String>();
            bool found = false;
            int i = 0;           

            // First, find the start of the System Commands ("public enum Commands") (
            while (!found)
            {
                if (allLines[i].Contains("public enum Commands"))
                {
                    found = true;
                }
                i++;
            }

            bool finishedAddingCommands = false;

            while (!finishedAddingCommands)
            {
                String command = "";
                int start = allLines[i].IndexOf("CMD");

                if (start > -1 && allLines[i].IndexOf("//")  > -1 && start > allLines[i].IndexOf("//") )
                {
                    Console.WriteLine(" ** ERROR **  Unexpected commented out commands detected in " + filename + " " + allLines[i]);
                    Problems++;
                }

                bool endOfCommandFound = false;
                int j = start;
                while (!endOfCommandFound)
                {                   
                    if (allLines[i].IndexOf(',') == j || allLines[i].IndexOf('}') == j)
                    {
                        endOfCommandFound = true;
                    }
                    else
                    {
                        command += allLines[i][j];
                        j++;

                        // In case the last line does not have a comma at the end...
                        if (allLines[i].IndexOf(',') == -1 && command.Length + start == allLines[i].Length)
                        {                          
                            endOfCommandFound = true;
                        }
                    }
                }

                if (Regex.Matches(allLines[i], "CMD").Count > 1)
                {
                    Console.WriteLine("** ERROR **  2 CMD_ commands on one line in " + filename + "\n" + allLines[i]);
                    Problems++;
                }

                if (allLines[i].Contains("/*"))
                {
                    Console.WriteLine("** ERROR **  Block comments found in " + filename + "\n" + allLines[i]);
                    Problems++;
                }

                if (command.Length > 0)
                {
                    returnedList.Add(command);
                }

                if (allLines[i].Contains("CMD") && allLines[i].Contains("}"))
                {
                    Console.WriteLine(" ** WARNING **  Closing bracket on same line as last command in " + filename);
                    Console.WriteLine(allLines[i]);
                    finishedAddingCommands = true;
                }
                else
                {
                    i++;

                    // } on its own line
                    if (allLines[i].Contains("}") && !allLines[i].Contains("CMD"))
                    {
                        finishedAddingCommands = true;
                    }
                }
            }

            return returnedList;
        }

        public static int CompareSystemCommands()
        {
            const String uiFilename = "User_Interface\\src\\main\\java\\com\\isansys\\pse_isansysportal\\SystemCommands.java";
            const String gatewayFilename = "Gateway\\src\\main\\java\\com\\isansys\\patientgateway\\SystemCommands.java";
           
            String errorMessage = "Problems found in SystemCommands...\n";
            int commandsFound = 0;           

            try
            {
                String[] uiAllContents = System.IO.File.ReadAllLines(uiFilename);
                String[] gatewayAllContents = System.IO.File.ReadAllLines(gatewayFilename);

                List<String> uiCommands = getCommands(uiAllContents, uiFilename);
                List<String> gatewayCommands = getCommands(gatewayAllContents, gatewayFilename);

                // If sizes match, check that commands appear in the same order
                if (uiCommands.Count == gatewayCommands.Count)
                {
                    commandsFound = uiCommands.Count;

                    for (int i = 0; i < uiCommands.Count; i++)
                    {
                        if (!uiCommands[i].Equals(gatewayCommands[i]))
                        {
                            errorMessage += "Mismatch: (UI) " + uiCommands[i] + " <-> " + gatewayCommands[i] + " (Gateway)\n";
                            Problems++;
                        }
                    }
                }
                // Report that sizes are different
                else
                {
                    errorMessage += "UI and Gateway System Commands are different sizes\n";
                    errorMessage += "UI commands:" + uiCommands.Count + " Gateway commands:" + gatewayCommands.Count + "\n";
                    Problems++;
                }

                if (Problems > 0)
                {
                    // https://stackoverflow.com/questions/12795882/quickest-way-to-compare-two-list
                    List<String> uiNotGateway = uiCommands.Except(gatewayCommands).ToList();
                    List<String> gatewayNotUi = gatewayCommands.Except(uiCommands).ToList();

                    if (uiNotGateway.Any())
                    {
                        errorMessage += "UI System Commands not in Gateway System Commands:\n";
                        foreach (String c in uiNotGateway)
                        {
                            errorMessage += "   " + c + "\n";
                        }
                    };

                    if (gatewayNotUi.Any())
                    {
                        errorMessage += "Gateway System Commands not in UI System Commands:\n";
                        foreach (String c in gatewayNotUi)
                        {
                            errorMessage += "   " + c + "\n";
                        }
                    };

                    Console.WriteLine(errorMessage);

                    // Write differences
                    String uiOut = "User_Interface\\src\\main\\java\\com\\isansys\\pse_isansysportal\\SystemCommands.java\n\n";
                    String gatewayOut = "Gateway\\src\\main\\java\\com\\isansys\\patientgateway\\SystemCommands.java\n\n";
                    foreach (String s in uiCommands) { uiOut += s + "\n"; }
                    foreach (String s in gatewayCommands) { gatewayOut += s + "\n"; }
                    System.IO.File.WriteAllText("_uiSystemCommands.out.txt", uiOut);
                    System.IO.File.WriteAllText("_gatewaySystemCommands.out.txt", gatewayOut);

                    Console.WriteLine("To view differences, compare:\n_uiSystemCommands.out.txt\n_gatewaySystemCommands.out.txt");
                } 
                else
                {
                    Console.WriteLine("SystemCommandsSynchronisedChecker detected no problems, " + commandsFound + " commands.");
                }
            }
            catch (Exception ex)
            {                
                Console.WriteLine("Error!\n" + ex.ToString());
                return 99;
            }

            return Problems;
        }

        static int Main(string[] args)
        {
            return Program.CompareSystemCommands();
        }
    }
}
