using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Diagnostics;
using System.IO;
using TcpIpLibrary;

namespace TestServer
{
    public partial class Form1 : Form
    {
        TcpServer server;
        string client1ConnectionId;
        Process processBareTail;

        TextWriter LogFile;
        string log_file_path;
        string log_filename;

        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            int port = 23456;
            server = new TcpServer("0.0.0.0", port);
            server.OnConnected += server_Connected;
            server.OnDataReceived += server_DataReceived;
            server.OnDisconnected += server_Disconnected;

            buttonStartStopServer.BackColor = Color.Red; 
                    
            processBareTail = new Process();
            if (LogFile != null)
            {
                LogFile.Close();
            }

            log_file_path = System.IO.Path.GetDirectoryName(System.Windows.Forms.Application.ExecutablePath);
            log_filename = "Log.txt";
            LogFile = new StreamWriter(log_file_path + "\\" + log_filename);
        }


        void WriteTimeAndMessageToFile(string message)
        {
            DateTime CurrentTime = DateTime.Now;

            string milliseconds_string = CurrentTime.Millisecond.ToString();
            if (milliseconds_string.Length == 1)
            {
                milliseconds_string += "00";
            }
            if (milliseconds_string.Length == 2)
            {
                milliseconds_string += "0";
            }

            LogFile.Write(CurrentTime.ToString() + "." + milliseconds_string + " : " + message);
            LogFile.Flush();
        }
        
        
        void server_Disconnected(object sender, ServerConnectionEventArgs e)
        {
            writeToLog("Connection closed: " + e.ConnectionId + "\r\n");
        }

        void server_DataReceived(object sender, ServerDataReceivedEventArgs e)
        {
            String data = "";
            foreach (byte value in e.Data)
            {
                data = data + Convert.ToChar(value);
            }
            
            //writeToLog("Data received on " + e.ConnectionId + " : " + data);
            writeToLog(data);
        }

        void server_Connected(object sender, ServerConnectionEventArgs e)
        {
            client1ConnectionId = e.ConnectionId;
            writeToLog("Connection accepted: " + e.ConnectionId + "\r\n");
        }

        void server_ConnectionRequest(object sender, ServerConnectionRequestEventArgs e)
        {
            writeToLog("Connection request from " + e.RemoteHost + "\r\n");
            
            e.Accept = true;
        }

        string convertByteToHexString(byte Value)
        {
            return String.Format("{0:X2}", Value) + " ";
        }

        string ConvertShortToHexString(ushort Value)
        {
            return String.Format("{0:X4}", Value) + " ";
        }
         

        void writeToLog(string message)
        {
            Debug.WriteLine(message);
            //WriteTimeAndMessageToFile(message + "\r\n");
            LogFile.Write(message);
            LogFile.Flush();
        }

        void sendCommand(byte[] raw_message)
        {
            if (client1ConnectionId != null)
            {
                server.Send(client1ConnectionId, raw_message);
            }
            else
            {
                writeToLog("Socket connection has not been established.\r\n");
            }
        }

        string processIncomingPayload(int command, byte[] payload_array)
        {
            return "";
        }

        private void buttonStartStopServer_Click(object sender, EventArgs e)
        {
            try
            {
                if (buttonStartStopServer.Text == "Start Server")
                {
                    server.Start();
                    buttonStartStopServer.BackColor = Color.Green;
                    buttonStartStopServer.Text = "Stop Server";
                    writeToLog("Starting server.\r\n");
                    writeToLog(string.Format("Server started on {0}:{1}", server.LocalHost, server.LocalPort));
                }
                else
                {
                    server.Stop();
                    buttonStartStopServer.BackColor = Color.Red; 
                    buttonStartStopServer.Text = "Start Server";
                    writeToLog("Stopping server.\r\n");
                }
            }
            catch (Exception ee)
            {
                MessageBox.Show(ee.ToString());
            }
        }

        private void checkBoxViewLog_CheckedChanged(object sender, EventArgs e)
        {
            if (checkBoxViewLog.Checked == true)
            {
                try
                {
                    processBareTail.StartInfo.FileName = System.IO.Path.GetDirectoryName(System.Windows.Forms.Application.ExecutablePath) + "\\baretail.exe";
                    processBareTail.StartInfo.Arguments = log_filename;
                    processBareTail.StartInfo.WorkingDirectory = log_file_path;
                    processBareTail.Start();
                }
                catch (Exception)
                {
                }
            }
            else
            {
                try
                {
                    processBareTail.Kill();
                }
                catch (InvalidOperationException)
                {
                    // Baretail has already been closed
                }
            }
        }

    }
}
