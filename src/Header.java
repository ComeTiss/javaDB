import java.util.ArrayList;

public class Header {

    private ArrayList<Type> types = new ArrayList<> ();
    private ArrayList<String> fields;
    private String primaryKey = null;

    /* ----------------------------------------------- */

    void setFromArray (ArrayList<String> fields) {
        this.fields = fields;
    }

    void setTypesFromArray (ArrayList<String> types) {
        for (String field : this.fields) {
            setType (field, types.get (index (field)));
        }
    }

    void set(String... fields) {
        /* Allow user to set table field names */
        if (CanSetFields(fields)) {
            this.fields = new ArrayList<> ();
            for (String field : fields) {
                this.fields.add (count(), field);
            }
        }
    }

    void update (String oldField, String newField) {
        /* Change a field name, except if
        - field to change doesn't exists
        - new field name is an existing field
        */
        if (! exists (oldField) || exists (newField)) {
            System.out.println ("Incorrect fields input");
            return;
        }
        int index = index (oldField);
        this.fields.remove (index);
        this.fields.add (index, newField);
    }

    void setKey (String field) {
        if (this.primaryKey == null && this.fields.contains (field)) {
            this.primaryKey = field;
        }
        else {
            System.out.println ("Couldn't assign '"+field+"' as PRIMARY KEY");
        }
    }

    void setType(String field, String type) {
        /*
            Allow to define field type only if:
            - field exist
            - type is allowed
            - this field type hasn't been defined yet
        */
        Type FieldType = new Type (type);

        if ( exists (field) && FieldType.isValid (type)  && ! TypeDefined (field)) {
            int FieldIndex = index (field);
            this.types.add (FieldIndex, FieldType);
        }
    }

    ArrayList<String> get () {
        return this.fields;
    }

    String getKey () {
        return this.primaryKey;
    }

    ArrayList <String> getAllTypes (){
        ArrayList<String> types = new ArrayList<> ();
        for (Type type : this.types) {
            types.add (type.get ());
        }
        return types;
    }

    Type getType (String field) {
        if ( exists (field) ) {
            int FieldIndex = index (field);
            return this.types.get (FieldIndex);
        }
        return null;
    }

    void delete (String field) {
        this.fields.remove (index(field));
    }

    int count () {
        try {
            return this.fields.size();
        }
        catch (Exception e) {
            return 0;
        }
    }

    int index(String field) {
        return this.fields.indexOf(field);
    }

    int indexKey () {
        return this.fields.indexOf (primaryKey);
    }

    void show() {
        separation();
        for (int i = 0; i < count(); i++) {
            System.out.printf("%-13s  ", this.fields.get(i));
        }
        System.out.print("\n");
        separation();
    }

    private void separation() {
        String message = new String ();
        for (int i = 0; i < count(); i++) {
            message += "--------------";
        }
        System.out.println(message);
    }

    /* ---------- Error Methods ------------ */
    Boolean MissingType () {
        /*
            check that all fields have a type
        */
        if (this.types.size () < count ()) {
            return true;
        }
        return false;
    }

    Boolean TypeDefined (String field) {
        int FieldIndex = index (field);
        try {
            if (this.types.get (FieldIndex) != null) {
                return true;
            }
        }
        catch (Exception e) {
            return false;
        }
        return false;
    }

    Boolean KeyDefined () {
        if (this.primaryKey != null) {
            return true;
        }
        return false;
    }

    Boolean exists(String field) {
        if (!this.fields.contains(field)) {
            return false;
        }
        return true;
    }

    private Boolean CanSetFields (String... fields) {
        /*
            Allow to set Fields only if:
            # fields not set before
            # new fields amount at least equal to current fields
         */
        if (this.fields != null && fields.length < count()) {
            return false;
        }
        return true;
    }

    /* -------------- TESTING -------------------- */

    public static void main(String[] args) {
        Header program = new Header();
        program.run();
    }

    private void run() {
        testCanSetFields();
        testSet();
        testIndex();
        testIndexKey();
        testExists();
        testSetKey();
        testKeyDefined();
        testUpdate();
        testType();
        System.out.println("All Test passed");
    }

    private void testCanSetFields() {
        Table test = new Table ("test");
        assert (test.getFields ().CanSetFields());
        assert (test.getFields ().CanSetFields ("field 1", "field 2"));
        test.getFields ().set ("field 1", "field 2");
        assert (!test.getFields ().CanSetFields ("new field"));
        assert (test.getFields ().CanSetFields ("new field", "another new field"));
    }

    private void testSet () {
        Table test = new Table("test");
        test.getFields ().set("id", "Name");
        assert(test.getFields ().count() == 2);
        assert(test.getFields ().index("id") == 0);
        assert(test.getFields ().index("Name") == 1);
        // change fields
        test.getFields ().set("City", "Country");
        assert(test.getFields ().count() == 2);
        assert(test.getFields ().index("City") == 0);
        assert(test.getFields ().index("Country") == 1);
    }

    private void testExists () {
        Table test = new Table("test");
        test.getFields ().set("id", "Name");
        assert(test.getFields ().exists("id"));
        assert(test.getFields ().exists("Name"));
        assert(!test.getFields ().exists("Error"));
    }

    private void testIndex () {
        Table test = new Table("test");
        test.getFields ().set("id", "Name");
        assert (test.getFields ().index("id") == 0);
        assert (test.getFields ().index("Name") == 1);
    }

    private void testIndexKey () {

        Header fields = new Header();
        fields.set ("Field 1", "Field 2");
        fields.setKey ("Field 2");
        assert (fields.indexKey () == 1);
    }

    private void testSetKey () {
        Table test = new Table ("test");
        test.getFields ().set("id", "Name");
        assert (test.getFields ().getKey () == null);

        // primary key must be a known field
        test.getFields ().setKey ("something");
        assert (test.getFields ().getKey () == null );
        test.getFields ().setKey ("id");
        assert (test.getFields ().getKey ().equals ("id"));

        // table already has a primary key
        test.getFields ().setKey ("Name");
        assert (test.getFields ().getKey ().equals ("id"));
    }

    private void testKeyDefined () {

        Header fields = new Header();
        fields.set ("Field 1", "Field 2");
        assert (! fields.KeyDefined ());
        fields.setKey ("Field 1");
        assert (fields.KeyDefined ());
    }

    private void testUpdate () {
        Table test = new Table ("test");
        test.getFields ().set("id", "Gender", "Name");
        test.getFields ().update ("Gender", "new");
        test.getFields ().get ().get (0).equals ("id");
        test.getFields ().get ().get (1).equals ("new");
        test.getFields ().get ().get (2).equals ("Name");
    }

    private void testType () {
        Header test = new Header();
        test.set ("Field 1", "Field 2");

        test.setType ("Field 1", "Unknown");
        assert (! test.TypeDefined ("Field 1"));

        test.setType ("Field 1", "Integer");
        assert (test.TypeDefined ("Field 1"));
        assert (test.getType ("Field 1").get ().equals ("Integer"));

        assert (test.MissingType ());

        test.setType ("Field 2", "String");
        assert (! MissingType ());
    }
}

