using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using System.Text.RegularExpressions;
using System.Diagnostics;

namespace AndroidManifestSvnBuildNumber
{
    class RunResults
    {
        public int ExitCode;
        public Exception RunException;
        public StringBuilder Output;
        public StringBuilder Error;
    }


    class Program
    {
        public static RunResults RunExecutable(string executablePath, string arguments, string workingDirectory)
        {
            RunResults runResults = new RunResults
            {
                Output = new StringBuilder(),
                Error = new StringBuilder(),
                RunException = null
            };
            try
            {
                //if (File.Exists(executablePath))
                //{
                    using (Process proc = new Process())
                    {
                        proc.StartInfo.FileName = executablePath;
                        proc.StartInfo.Arguments = arguments;
                        proc.StartInfo.WorkingDirectory = workingDirectory;
                        proc.StartInfo.UseShellExecute = false;
                        proc.StartInfo.RedirectStandardOutput = true;
                        proc.StartInfo.RedirectStandardError = true;
                        proc.OutputDataReceived +=
                            (o, e) => runResults.Output.Append(e.Data).Append(Environment.NewLine);
                        proc.ErrorDataReceived +=
                            (o, e) => runResults.Error.Append(e.Data).Append(Environment.NewLine);
                        proc.Start();
                        proc.BeginOutputReadLine();
                        proc.BeginErrorReadLine();
                        proc.WaitForExit();
                        runResults.ExitCode = proc.ExitCode;
                    }
                //}
                //else
                //{
                //    throw new ArgumentException("Invalid executable path.", "exePath");
                //}
            }
            catch (Exception e)
            {
                runResults.RunException = e;
                Console.WriteLine("Problem (RunExecutable) : " + e.ToString());
            }
            return runResults;
        }


        static int Main(string[] args)
        {
            int svn_revision = 0;
            try
            {
                Console.WriteLine("Running AndroidManifestSvnBuildNumber");

                string exe = "svn.exe";

                string arguments = "log -l 1 -q";


                RunResults runResults = Program.RunExecutable(exe, arguments, "");

                if (runResults.ExitCode == 0)
                {
                    string lines = runResults.Output.ToString();

                    Console.WriteLine("svn log finished");

                    Console.WriteLine("Result found:");
                    Console.WriteLine(lines);

                    string[] split_lines = lines.Split('\n');

                    string rev = split_lines[1].Split('|')[0];

                    rev = rev.Replace(" ", "");

                    rev = rev.Replace("r", "");

                    Console.WriteLine("Revision found: " + rev);

                    svn_revision = int.Parse(rev);
                }
                else
                {
                    Console.WriteLine("Svn log failed with error:");
                    Console.WriteLine(runResults.Error.ToString());
                }
            }
            catch (Exception ee)
            {
                Console.WriteLine("Problem (main) : " + ee.ToString());
            }

            return svn_revision;
        }
    }
}
