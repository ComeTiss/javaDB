
import java.util.ArrayList;
import java.util.Arrays;


public class Table {

    /*
        A table contains a collection of Records and a Header
        In order to write to the Table, the header must be fully defined, as well as the primary key

        Common operations:
        - create/update/delete/alter
        - select/filters
    */

    private String name;
    private ArrayList<Record> rows = new ArrayList<>();
    private Header fields = new Header();

    Table (String name) {
        this.name = name;
    }

    /* ------------ Random Methods ----------- */

    int count() {
        return this.rows.size();
    }

    void show() {

        Display display = new Display ();
        display.ShowTable (name, this.rows, this.fields);
    }

    String getName() {
        return this.name;
    }

    Header getFields() {
        return this.fields;
    }

    ArrayList<Record> getRows () {
        return this.rows;
    }

    void changeName (String name) {
        this.name = name;
    }

    int RowIndex (Object KeyValue) {

        int indexKey = this.fields.indexKey ();
        int row = 0;

        for (int i=0; i<count (); i++) {
            if (this.rows.get (i).select (indexKey).equals (KeyValue)) {
                row = i;
            }
        }
        return row;
    }

    /* ------------ Record Methods ----------- */

    void alter(String field, String type) {
        /*
           add a new column to table
        */
        if (this.fields.exists (field)) {
            return;
        }

        ArrayList<String> newFields = this.fields.get (); // copy current fields
        newFields.add (field);

        String Key = this.fields.getKey (); // save current key
        this.fields.setFromArray (newFields); //swap
        this.fields.setType (field, type);
        this.fields.setKey (Key);

        //extend current records size
        for (int i=0; i<count (); i++) {
            this.rows.get (i).extend ("");
        }
    }

    void create(Object KeyValue) {
        /* Add new row to the table */
        if (! CanCreate (KeyValue)) {
            return;
        }
        Record r = new Record(this.fields.count());
        int index = this.fields.indexKey ();
        r.update(index, KeyValue);
        this.rows.add(r);
    }

    void update(Object KeyValue, String field, Object value) {
        /* update a cell in the table */

        if (!this.fields.exists(field) || keyConstrain (field, value) ||
            ! KeyValueExists (KeyValue) || ! DataTypeValid (field, value)){
            return;
        }
        int index = this.fields.index(field);
        this.rows.get(RowIndex (KeyValue)).update(index, value);
    }

    void deleteRecord(Object KeyValue) {
        /* delete a row from the table */

        if (KeyValueExists (KeyValue)) {
            this.rows.remove(RowIndex (KeyValue));
        }
    }

    void deleteColumn (String field) {
        /*
           delete column content and its header from the table
           -> except if header is a primary key
        */

        if (field.equals (this.fields.getKey ())) {
            Display display = new Display ();
            display.ErrorKeyConstrain ();
            return;
        }
        if (this.fields.exists (field)) {
            int fieldIndex = this.fields.index (field);
            for (int i=0; i<count (); i++) {
                this.rows.get(i).delete (fieldIndex);
            }
            this.fields.delete (field);
        }
    }

    /* ---------- QUERY METHODS ----------- */


    Object selectOne (String field, Object KeyValue) {
        if (! this.fields.exists (field) || ! KeyValueExists (KeyValue)) {
            return null;
        }
        int index = this.fields.index (field);
        return selectRow (KeyValue).get (index);
    }

    ArrayList<Object> selectRow (Object KeyValue) {
        String Key = this.fields.getKey ();
        Table t = filterByValue (Key, KeyValue); // filtered table with 1 record only

        if (t.count () != 1) {
            Display display = new Display ();
            display.ErrorKeyValue ();
            return null;
        }
        return t.getRecord (0).get ();
    }

    ArrayList<Object> selectColumn (String field) {

        if (! this.fields.exists (field)) {
            return null;
        }

        ArrayList<Object> entries = new ArrayList<> ();
        for (int i=0; i<count (); i++) {
            entries.add (getEntry (field, i));
        }
        return entries;
    }

    /* ----------- FILTER METHODS ----------- */

    Table filterByValue (String field, Object value) {
        /*
          Given a field name and a value
          returns all rows within table containing it
        */

        Table filteredData = new Table ("filter");
        filteredData.fields = this.fields;

        if (filteredData.fields.exists (field)) {
            for (String Field : this.fields.get ()) {
                String type = this.fields.getType (Field).get ();
                filteredData.getFields ().setType (Field, type);
            }

            int fieldIndex = this.fields.index (field);
            for (Record row : this.rows) {
                if (row.exists (fieldIndex, value)) {
                    filteredData.getRows ().add (row);
                }
            }
        }
        return filteredData;
    }

    Table filterByField (String ...fields) {
        /*
          Returns a new table with only the values for the fields given as argument
          All the fields given must exist within the current table
        */
        Table filteredData = new Table ("filter");
        ArrayList<String> newFields = new ArrayList<> ();

        // Initialize fields
        newFields.addAll (Arrays.asList (fields));
        filteredData.getFields ().setFromArray (newFields);

        // initialize types
        if (this.fields.existAll (fields)) {
            for (String Field : newFields) {
                String type = this.fields.getType (Field).get ();
                filteredData.getFields ().setType (Field, type);
            }

            // Initialize rows
            for (int i=0; i<count (); i++) {
                Record newRow = new Record (newFields.size ());
                filteredData.getRows ().add (newRow);
            }

            // populate new table with values
            for (String field : newFields) {
                int fieldIndex = filteredData.getFields ().index (field);
                for (int i=0; i<filteredData.count (); i++) {
                    Object value = getEntry (field, i);
                    filteredData.getRows ().get (i).update (fieldIndex, value);
                }
            }
        }
        return filteredData;
    }

    /* ---------- Error Methods ------------ */

    private Boolean DataTypeValid (String field, Object value) {
        /*  Returns true, if the value type matches the field type */

        if ( ! this.fields.exists (field) ) {
            return false;
        }
        String type = this.fields.getType (field).get ();

        if (type.equals ("String") && value instanceof String) {
            return true;
        }
        if (type.equals ("Integer") && value instanceof Integer) {
            return true;
        }
        if (type.equals ("Boolean") && value instanceof Boolean) {
            return true;
        }
        return false;
    }

    private Boolean CanCreate (Object KeyValue) {

        /* all the checks must be passed so a new record can be created */

        if (this.fields.MissingType ()) {
            return false;
        }
        if (! DataTypeValid (this.fields.getKey (), KeyValue)) {
            return false;
        }
        if (! this.fields.KeyDefined () || KeyValueExists (KeyValue)) {
            return false;
        }
        return true;
    }

    private Boolean InBound(int row) {
        if (row < 0 || row >= count()) {
            return false;
        }
        return true;
    }

    private Boolean KeyValueExists (Object KeyValue) {
        /*
            If Table has a primary key:
            Check all rows for a KeyValue,
            return true if this value exists
        */

        if ( this.fields.KeyDefined () ) {

            int index = this.fields.indexKey ();
            for (Record r : this.rows) {

                if (r.select (index).equals (KeyValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean keyConstrain (String field, Object value) {
        /*
            Returns false if field not a primary key,
            otherwise check that the value isn't used already
        */
        if ( field.equals(this.fields.getKey()) &&
                KeyValueExists (value)) {
            return true;
        }
        return false;
    }

    /* ------------ Write/save Table from File ------------ */

    void load (String filename) {
        /* Read content from given file
           & write into table
         */
        Data output = new Data ();
        output.read (filename);

        // fill table with output data
        if (output.isValid ()) {
            this.fields.setFromArray (output.getFields ());
            this.rows = output.getRows ();
            this.fields.setTypesFromArray (output.getTypes ());

            if (this.fields.exists (output.getKey ())) {
                this.fields.setKey (output.getKey ());
            }
        }
        else {
            Display display = new Display ();
            display.ErrorFileDataFormat ();
        }
    }

    void save (String filename) {
        /*
            Write table content into a text file
        */
        Data output = new Data ();
        output.write (this.fields, this.rows, filename);
    }

    /* ----------- TESTING ------------ */

    /* The following 2 methods are used internally to make other functions shorter */
    private Record getRecord(int index) {
        if (!InBound (index)) {
            return null;
        }
        return this.rows.get(index);
    }

    private Object getEntry(String field, int rowIndex) {
        if (!fields.exists(field)) {
            return null;
        }
        return getRecord(rowIndex).select(fields.index(field));
    }

    public static void main(String[] args) {
        Table program = new Table("test");
        program.run();
    }

    private void run() {
        testAlter();
        testKeyConstrain();
        testKeyValueExists();
        testCreateRecord();
        testUpdateRecord();
        testDeleteRecord();
        testInBound();
        testDataTypeValid();
        testDeleteColumn();
        testFilterByValue();
        testFilterByField();
        testSelectColumn();
        System.out.println("All Test passed");
    }
    private void testAlter () {
        Table test = new Table("test");
        test.fields.set("Field 1", "Field 2");
        assert (test.fields.count() == 2);
        test.alter("Field 3", "String");
        assert (test.fields.count() == 3);
    }

    private void testKeyConstrain () {
        Table test = createTestTable ();
        assert (test.keyConstrain ("Field 1", 1));
        assert (! test.keyConstrain ("Field 3", "value 3"));
        assert (! test.keyConstrain ("Field 2", "value 2"));
        assert (! test.keyConstrain ("Field 2", "value 4"));
    }

    private void testKeyValueExists () {
        Table test = createTestTable ();
        assert (test.KeyValueExists (1));
        assert (test.KeyValueExists (2));
        assert (! test.KeyValueExists (3));
        assert (! test.KeyValueExists ("unknown"));
        assert (! test.KeyValueExists ("value 3"));
        assert (! test.KeyValueExists ("value 4"));
        assert (! test.KeyValueExists ("value 5"));
    }

    private void testCreateRecord () {
        Table test = new Table("test");
        test.getFields ().set("id", "Name");
        test.getFields ().setType ("id", "String");
        test.getFields ().setType ("Name", "String");

        test.getFields ().setKey ("id");
        test.create("1");
        assert (test.count() == 1);
        assert (test.getEntry("id", 0).equals("1"));

        // can't create record because of key constrain
        test.create( "1");
        assert (test.count() == 1);
        assert (test.keyConstrain ("id", "1"));
    }
    private void testUpdateRecord () {
        Table test = new Table("test");
        test.getFields ().set("id", "Name");
        test.getFields ().setType ("id", "String");
        test.getFields ().setType ("Name", "String");
        test.getFields ().setKey ("id");
        test.create("1");
        test.update ("1", "Name", "John");

        // check updated correctly
        assert (test.count() == 1);
        assert (test.getEntry("id", 0).equals("1"));
        assert (test.getEntry("Name", 0).equals("John"));
        test.update("1", "id", "0");
        assert (test.getEntry("id", 0).equals("0"));

        // update on a row that doesn't exist
        test.update("2", "Name", "Peter");
        assert (test.count() == 1);

        // update fails because of Key constrain
        test.create("1");
        test.update("1", "id", "0");
        assert (test.getEntry ("id", 1).equals ("1"));

        // update successful with different value
        test.update("1", "id", "2");
        assert (test.getEntry ("id", 1).equals ("2"));
    }

    private void testDeleteRecord () {
        Table test = new Table("test");
        test.getFields ().set("id", "Name");
        test.getFields ().setType ("id", "String");
        test.getFields ().setType ("Name", "String");
        test.getFields ().setKey ("id");
        test.create( "1");
        assert (test.count() == 1);
        test.deleteRecord("1");
        assert (test.count() == 0);
    }

    private void testInBound () {
        Table test = new Table("test");
        test.getFields ().set("id", "Name");
        test.getFields ().setType ("id", "String");
        test.getFields ().setType ("Name", "String");
        test.getFields ().setKey ("id");
        assert (test.count() == 0);
        assert (!test.InBound (0));
        test.create("1");
        assert (test.InBound (0));
        assert (!test.InBound (1));
    }

    private void testDataTypeValid () {
        Table test = createTestTable ();
        assert (test.DataTypeValid ("Field 1", 3));
        assert (! test.DataTypeValid ("Field 1", true));
        assert (! test.DataTypeValid ("Field 1", "3"));

        assert (! test.DataTypeValid ("Field 2", 3));
        assert (! test.DataTypeValid ("Field 2", true));
        assert (test.DataTypeValid ("Field 2", "value 2"));
    }


    private void testDeleteColumn () {
        Table test = createTestTable ();
        assert (test.getRecord (0).width () == 3);

        // Trying to delete Primary key column
        test.deleteColumn ("Field 1");
        assert (test.getFields ().count () == 3);

        test.deleteColumn ("Field 2");
        assert (test.getFields ().count () == 2);
        assert (test.getFields ().exists ("Field 1"));
        assert (! test.getFields ().exists ("Field 2"));
        assert (test.getFields ().exists ("Field 3"));
    }

    private void testFilterByValue () {
        Table test = createTestTable ();
        assert(test.filterByValue ("Field 1", "unknown").count () == 0);
        assert(test.filterByValue ("Field 1", 1).count () == 1);
        assert(test.filterByValue ("Field 2", "value 2").count () == 1);
        assert(test.filterByValue ("Field 3", "value 3").count () == 1);
        assert(test.filterByValue ("Field 3", "value 5").count () == 1);
    }

    private void testFilterByField () {
        Table test = createTestTable ();
        assert(test.getRecord (0).width () == 3);
        assert(test.filterByField ("Field 1", "Field 2").getRecord (0).width () == 2);
        assert(test.filterByField ("Field 1").getRecord (0).width () == 1);
        assert(test.filterByField ("Field 1", "Field 2", "Field 3").getRecord(0).width () == 3);
    }

    private void testSelectColumn() {
        Table test = createTestTable ();
        assert (test.selectColumn ("Field 1").size () == 2);
        assert (test.selectColumn ("Field 1").get (0).equals (1));
        assert (test.selectColumn ("Field 1").get (1).equals (2));
    }

    private Table createTestTable () {
        Table test = new Table ("test");
        test.getFields ().set ("Field 1", "Field 2", "Field 3");
        test.getFields ().setKey ("Field 1");

        test.getFields ().setType ("Field 1", "Integer");
        test.getFields ().setType ("Field 2", "String");
        test.getFields ().setType ("Field 3", "String");

        test.create(1);
        test.update(1, "Field 2", "value 2");
        test.update(1, "Field 3", "value 3");
        test.create(2);
        test.update(2, "Field 2", "value 4");
        test.update(2, "Field 3", "value 5");
        return test;
    }
}
