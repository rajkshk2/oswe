using System;
using System.Diagnostics;
using System.IO;
using System.Xml;
using System.Xml.Serialization;

namespace MultiXMLDeserializer
{
    class Program
    {
        static void Main(string[] args)
        {
            if (args.Length == 0)
            {
                Console.WriteLine("Usage: MultiXMLDeserializer.exe <xmlfile>");
                return;
            }

            // Read the XML file provided by the user
            string xml = File.ReadAllText(args[0]);
            CustomDeserializer(xml);
        }

        static void CustomDeserializer(string myXMLString)
        {
            XmlDocument xmlDocument = new XmlDocument();
            xmlDocument.LoadXml(myXMLString);

            foreach (XmlElement xmlItem in xmlDocument.SelectNodes("customRootNode/item"))
            {
                string typeName = xmlItem.GetAttribute("objectType");

                // 🚨 Dangerous: trusting user-supplied type names
                var xser = new XmlSerializer(Type.GetType(typeName));

                var reader = new XmlTextReader(new StringReader(xmlItem.InnerXml));
                xser.Deserialize(reader);
            }
        }
    }

    // 🚨 Dangerous class that executes commands
    public class ExecCMD
    {
        private string _cmd;
        public string cmd
        {
            get { return _cmd; }
            set
            {
                _cmd = value;
                ExecCommand();
            }
        }

        private void ExecCommand()
        {
            Process myProcess = new Process();
            myProcess.StartInfo.FileName = _cmd;
            myProcess.Start();
            myProcess.Dispose();
        }
    }
}
