using System;
using System.IO;
using System.Xml.Serialization;
using BasicXMLSerializer;   // Import the namespace where MyConsoleText is defined

namespace BasicXMLDeserializer
{
    class Program
    {
        static void Main(string[] args)
        {
            if (args.Length == 0)
            {
                Console.WriteLine("Please provide the XML file path as an argument.");
                return;
            }

            // Open the XML file (first argument)
            var fileStream = new FileStream(args[0], FileMode.Open, FileAccess.Read);
            var streamReader = new StreamReader(fileStream);

            // Prepare deserializer for MyConsoleText type
            XmlSerializer serializer = new XmlSerializer(typeof(MyConsoleText));

            // Deserialize back into an object
            MyConsoleText obj = (MyConsoleText)serializer.Deserialize(streamReader);

            Console.WriteLine("Deserialization complete. Object restored.");
            Console.WriteLine("Value inside object: " + obj.text);

            streamReader.Close();
        }
    }
}
