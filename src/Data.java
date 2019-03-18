
import java.io.*;
import java.util.ArrayList;

public class Data {
    /*
        This class handles all the operations regarding files I/O
        # read / write Table into given file
    */

    private ArrayList<Record> rows = new ArrayList<> ();
    private ArrayList<String> fields = new ArrayList<> ();
    private ArrayList<String> types = new ArrayList<> ();
    private String primaryKey = null;

    ArrayList<Record> getRows () {
        return this.rows;
    }

    ArrayList<String> getFields () {
        return this.fields;
    }

    ArrayList<String> getTypes() {
        return this.types;
    }

    String getKey () {
        return this.primaryKey;
    }


    void read(String filename) {
        /*
          Given a filename, returns its content
          # data returned line by line
          # each word separated by a ','
          # get rid of spaces within content between two ','
        */
        try {
            FileReader filereader = new FileReader (new File (removeSpaces(filename)));
            BufferedReader bufferedReader = new BufferedReader (filereader);
            String line;
            int count = 0;

            while ((line = bufferedReader.readLine()) != null) {
                Object[] objects = line.split (",");

                if (! BlankLine (objects)) {
                    if (count > 2) {
                        this.rows.add (new Record (objects.length));
                    }
                    saveObjects (objects, count);
                    count++;
                }
            }
            bufferedReader.close ();
        }
        catch (FileNotFoundException e) {
            Display display = new Display ();
            display.ErrorOpeningFile (filename);
        }
        catch (IOException e) {
            Display display = new Display ();
            display.ErrorReadingFile (filename);
        }
    }

    private void saveObjects (Object[] objects, int counter) {
        for (int i = 0; i < objects.length; i++) {
            if (counter == 0) {
                this.fields.add (removeSpaces (objects[i].toString ()));
            }
            else if (counter == 1) {
                this.types.add (removeSpaces (objects[i].toString ()));
            }
            else if (counter == 2) {
                /* If multiple values are present on line,
                   the last will be taken as the Table key
                */
                this.primaryKey = removeSpaces (objects[i].toString ());
            }
            else {
                if (CorrectType (this.types.get (i), objects[i].toString ())) {
                    /* Store value with the correct type */
                    Object value = ReformatObj (this.types.get (i), removeSpaces(objects[i].toString ()));
                    this.rows.get (counter - 3).update (i, value);
                }
            }
        }
    }


    void write (Header fields, ArrayList<Record> rows, String filename) {
        /* write Table data into a file
           # convert a list of string to a string (for both rows and fields)
           # write this new string line into given file
        */
        try {
            PrintWriter writer = new PrintWriter (new File (removeSpaces(filename)), "UTF-8");

            String newFields = convert (convertToObj (fields.get ()));
            if (newFields != null) {
                writer.println (newFields);
            }
            String newTypes = convert (convertToObj (fields.getAllTypes ()));
            if (newTypes != null) {
                writer.println (newTypes);
            }
            if (fields.KeyDefined ()) {
                writer.println (fields.getKey ());
            }
            else {
                writer.println ("UndefinedKey");
            }

            for (int i=0; i<rows.size(); i++) {
                String newRow = convert (rows.get(i).get());
                if (newRow != null) {
                    writer.println (newRow);
                }
            }
            writer.close ();
        }
        catch (IOException e) {
            Display display = new Display ();
            display.ErrorWritingFile (filename);
        }
    }


    private Boolean BlankLine (Object[] line) {
        /* check that new line read from a file isn't a blank line
        */
        if (line.length == 1 && line[0].equals ("")) {
            return true;
        }
        return false;
    }

    private Boolean findComma (Object word) {
        /* returns true if the parsed word contains a comma
        */
        String StrWord = word.toString ();
        for (int i=0; i<StrWord.length (); i++) {
            if (StrWord.charAt(i) == ',') {
                Display display = new Display ();
                display.ErrorCommaSeparator ();
                return true;
            }
        }
        return false;
    }

    private String removeSpaces (String word) {
        return word.replaceAll("\\s+", "");
    }

    private Object ReformatObj (String type, Object value) {
        /* All Objects are read from a file as a String
           # here they are reformatted to match their type
         */
        if (type.equals ("Integer")) return Integer.parseInt (value.toString ());
        if (type.equals ("Boolean")) return Boolean.parseBoolean (value.toString ());
        else return value.toString ();
    }

    private ArrayList<Object> convertToObj (ArrayList<String> strings) {
        ArrayList<Object> objects = new ArrayList<> ();
        objects.addAll (strings);
        return objects;
    }

    private String convert (ArrayList<Object> List) {
        /* converts the Record list of values to a single String
           Each value separated with a ','
           If any value contains a ',' already - conversion wont be possible
        */
        String line = new String ();

        for (int i=0; i < List.size (); i++) {
            if (! findComma (List.get (i))) {
                line += List.get (i) ;
            }
            if (i < List.size () - 1) {
                line += ", ";
            }
        }
        return line;
    }

    Boolean isValid () {
        /*
          check that amount of types and each row width
          respect the table size defined by the field array size
        */
        int fieldWidth = this.fields.size ();

        if (types.size () > fieldWidth) {
            return false;
        }
        for (Record row : this.rows) {
            if (row.width () > fieldWidth) {
                return false;
            }
        }
        return true;
    }


    private String findType (String input) {
        /* check that each character of a given value
           respects a certain format, so it respect the input type
         */
        String value = removeSpaces (input);
        if (value.equals ("true") || value.equals ("false")) return "BooleanOrString";
        if (value.matches ("[0-9]+")) return "Integer";
        if (value.matches ("[a-zA-Z0-9]+")) return "String";
        return "Unknown";
    }

    Boolean CorrectType (String type, String value) {
        /*  Check that a value type read from a file
            matches its field type
        */
        if (type.equals ("String")) {
            if (findType (value).equals ("BooleanOrString") ||
                    findType (value).equals ("String")) {
                return true;
            }
        }
        if (type.equals ("Integer") && findType (value).equals ("Integer")) {
            return true;
        }
        if (type.equals ("Boolean") && findType (value).equals ("BooleanOrString")) {
            return true;
        }
        return false;

    }


    /* ------------- TESTING -------------- */

    public static void main(String[] args) {
        Data program = new Data ();
        program.run();
    }

    private void run() {
        /* Tested read & write methods by visual check */
        testIsValid();
        testFindType ();
        testCorrectType();
        testReformatObject();
        testFindComma();
        testConvert();
        testRemoveSpace();
        System.out.println ("All test passed");
    }

    private void testIsValid () {
        Data test = new Data ();
        test.fields.add("Field 1");
        test.fields.add("Field 2");
        test.rows.add (new Record (4));
        assert (! test.isValid ());

        test.rows.remove (0);
        test.rows.add (new Record(2));
        assert (test.isValid ());
    }

    private void testFindType () {
        assert (findType ("hello").equals ("String"));
        assert (findType ("1234").equals ("Integer"));
        assert (findType ("false").equals ("BooleanOrString"));
        assert (findType ("true").equals ("BooleanOrString"));
        assert (findType ("1234,123").equals ("Unknown"));
        assert (findType ("8?/EQZfe").equals ("Unknown"));
    }

    private void testCorrectType () {
        assert (CorrectType ("String", "Hello"));
        assert (CorrectType ("Integer", "12345"));
        assert (CorrectType ("Boolean", "false"));
        assert (CorrectType ("Boolean", "true"));
        assert (CorrectType ("String", "true"));
        assert (CorrectType ("String", "false"));
    }

    private void testReformatObject () {
        assert (ReformatObj ("String", "Hello") instanceof String);
        assert (ReformatObj ("Integer", "123") instanceof Integer);
        assert (ReformatObj ("Boolean", "true") instanceof Boolean);
        assert (ReformatObj ("Boolean", "false") instanceof Boolean);
    }

    private void testFindComma () {

        Data testData = new Data ();
        assert (! testData.findComma ("word"));
        assert (! testData.findComma ("123"));
        assert (testData.findComma ("wo,rd"));
        assert (testData.findComma ("12,34"));

    }

    private void testConvert() {
        Data testData = new Data ();
        ArrayList<Object> test = new ArrayList<> ();
        test.add ("one");
        test.add ("two");
        assert(testData.convert (test).equals ("one, two"));
    }

    private void testRemoveSpace () {
        Data testData = new Data ();
        assert (testData.removeSpaces ("hello").equals ("hello"));
        assert (testData.removeSpaces ("h e l l o").equals ("hello"));
        assert (testData.removeSpaces ("  hello").equals ("hello"));
        assert (testData.removeSpaces ("hello  ").equals ("hello"));
        assert (testData.removeSpaces ("  hel  lo  ").equals ("hello"));
    }
}
