
import java.io.*;
import java.util.ArrayList;

public class Data {
    /*
        This class handles all the operations regarding files I/O
        # read / write Table into given file
    */

    private ArrayList<Record> data = new ArrayList<> ();
    private ArrayList<String> fields = new ArrayList<> ();
    private ArrayList<String> types = new ArrayList<> ();

    ArrayList<Record> getData () {
        return this.data;
    }

    ArrayList<String> getFields () {
        return this.fields;
    }

    ArrayList<String> getTypes() {
        return this.types;
    }


    void read(String filename) {
        /*
          Given a filename, returns its content
          # data returned line by line
          # each word separated by a ','
          # get rid of spaces within content between two ','
        */
        String line;

        try {
            FileReader filereader = new FileReader (new File (removeSpaces(filename)));
            BufferedReader bufferedReader = new BufferedReader (filereader);
            int count = 0;

            while ((line = bufferedReader.readLine()) != null) {
                Object[] words = line.split (",");

                if (count > 0) {
                    Record row = new Record (words.length);
                    this.data.add (row);
                }

                // read a line word by word
                for (int i = 0; i < words.length; i++) {

                    if (count == 0) {
                        this.fields.add (words[i].toString ());
                    }
                    else if (count == 1) {
                        this.types.add (removeSpaces (words[i].toString ()));
                    }
                    else {
                        this.data.get (count-2).update(i, words[i]);
                    }
                }
                count++;
            }
            bufferedReader.close ();
        }
        catch (FileNotFoundException e) {
            System.out.println ("Unable open file given: "+ filename);
        }
        catch (IOException e) {
            System.out.println ("Unable to read file content");
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

            for (int i=0; i<rows.size(); i++) {
                String newRow = convert (rows.get(i).get());
                if (newRow != null) {
                    writer.println (newRow);
                }
            }
            writer.close ();
        }
        catch (IOException e) {
            System.out.println ("Unable to write to file ");
        }
    }

    private Boolean findComma (Object word) {

        String StrWord = word.toString ();

        for (int i=0; i<StrWord.length (); i++) {
            if (StrWord.charAt(i) == ',') {
                System.out.println ("Not allowed to use ',' within a value");
                return true;
            }
        }
        return false;
    }

    private String removeSpaces (String word) {
        return word.replaceAll("\\s+", "");
    }

    ArrayList<Object> convertToObj (ArrayList<String> strings) {
        ArrayList<Object> objects = new ArrayList<> ();
        objects.addAll (strings);
        return objects;
    }

    String convert (ArrayList<Object> List) {
        /*
           converts the Record list of values to a single String
           Each value separated with a ','
           If any value contains a ',' already - conversion wont be possible
        */

        String line = new String ();

        for (int i=0; i < List.size (); i++) {
            if (findComma (List.get (i))) {
                return null;
            }
            line += List.get (i) ;

            if (i < List.size () - 1) {
                line += ", ";
            }
        }
        return line;
    }

    Boolean isValid () {
        /*
          check that all rows respect the table size
          defined by the field array size
        */
        int fieldWidth = this.fields.size ();
        for (Record row : this.data) {
            if (row.width () > fieldWidth) {
                return false;
            }
        }
        return true;
    }


    /* ------------- TESTING -------------- */

    public static void main(String[] args) {
        Data program = new Data ();
        program.run();
    }

    private void run() {
        /* Tested read & write methods by visual check */
        testIsValid();
        testFindComma();
        testConvert();
        testRemoveSpace();
        System.out.println ("All test passed");
    }

    private void testIsValid () {
        Data test = new Data ();
        test.fields.add("Field 1");
        test.fields.add("Field 2");
        test.data.add (new Record (4));
        assert (! test.isValid ());

        test.data.remove (0);
        test.data.add (new Record(2));
        assert (test.isValid ());
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
