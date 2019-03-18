import java.util.ArrayList;

public class Display {

    /*
        This class handles the input/output messages to the terminal, including:
        - displaying database or table content (record, header, overall table)
        - displaying error messages
    */

    void ShowDatabase (String name, ArrayList<Table> tables) {
        String msg = "\n###########\n";
        msg += name.toUpperCase ()+"\n\n";
        for (Table t : tables) {
            msg += "> " + t.getName () + "\n";
        }
        msg += "--------------\n";
        System.out.println (msg);
    }

    void ShowTable (String name, ArrayList<Record> rows, Header fields) {
        System.out.println ("\n###########");
        System.out.println (name.toUpperCase ()+"\n");
        ShowHeader (fields);
        for (int i = 0; i < rows.size() ; i++) {
            ShowRecord (rows.get(i));
        }
        System.out.print("\n");
    }

    void ShowRecord (Record row) {
       for (int i = 0; i < row.width (); i++) {
           System.out.printf ("%-13s  ", row.select(i));
       }
       System.out.print ("\n");
    }

    void ShowHeader (Header fields) {
        /*
            Display all fields on the screen, with a top/bottom separation line
        */
        separation(fields.count ());
        for (int i = 0; i < fields.count (); i++) {
            System.out.printf("%-13s  ", fields.get ().get (i));
        }
        System.out.print("\n");
        separation(fields.count ());
    }

    private void separation(int count) {

        String message = new String ();
        for (int i = 0; i < count; i++) {
            message += "--------------";
        }
        System.out.println(message);
    }

    /* ----------------------- Error messages ---------------------------- */
    void ErrorCreatingDatabase (String name) {
        System.out.println ("Folder exists already / Couldn't create folder: "+ name +"\n");

    }

    void ErrorFieldInput () {
        System.out.println ("Incorrect fields input");
    }

    // ----- Primary Key Error messages ----- |

    void ErrorKeyValue () {
        System.out.println ("Invalid results obtained with the given key value");
    }

    void ErrorKeyConstrain () {
        System.out.println ("Can't delete because of PRIMARY KEY constrain");
    }

    void ErrorKeyDefinition (String field) {
        System.out.println ("Couldn't assign '"+field+"' as PRIMARY KEY");
    }

    // ----- File handling Error messages ----- |

    void ErrorFileDataFormat () {
        System.out.println ("\nInvalid data format, check table size.");
    }

    void ErrorOpeningFile (String filename) {
        System.out.println ("Unable open file given: "+ filename);
    }

    void ErrorReadingFile (String filename) {
        System.out.println ("Unable to read file content from: " + filename);
    }

    void ErrorWritingFile (String filename) {
        System.out.println ("Unable to write to file: " + filename);
    }

    void ErrorCommaSeparator () {
        System.out.println ("Not allowed to use ',' within a value");
    }
}
