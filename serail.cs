using System;
using System.IO;
using System.Xml.Serialization;

namespace BasicXMLSerializer
{
    class Program
    {
        static void Main(string[] args)
        {
            // Check if input was provided
            if (args.Length == 0)
            {
                Console.WriteLine("Please provide some text when running the program.");
                return;
            }

            // Create object and take input
            MyConsoleText myText = new MyConsoleText();
            myText.text = args[0];
            MySerializer(myText);
        }

        static void MySerializer(MyConsoleText txt)
        {
            var ser = new XmlSerializer(typeof(MyConsoleText));
            using (TextWriter writer = new StreamWriter("C:\\Users\\Public\\basicXML.txt"))
            {
                ser.Serialize(writer, txt);
            }
        }
    }

    public class MyConsoleText
    {
        private string _text;

        public string text
        {
            get { return _text; }
            set 
            { 
                _text = value; 
                Console.WriteLine("My first console text class says: " + _text);
            }
        }
    }
}
