import java.util.ArrayList;

public class Record {

    /*
        A Record, or a 'row' contains a set of values; one for each column of the table
    */


    private ArrayList<Object> values = new ArrayList<> ();

    // Constructor
    Record(int size) {
        for (int i = 0; i < size; i++) {
            this.values.add ("");
        }
    }

    /* --------- Methods ---------- */
    ArrayList<Object> get () {
        return this.values;
    }

    void delete (int index) {
        this.values.remove (index);
    }

    void extend (Object value) {
        this.values.add(value);
    }

    void update(int index, Object value) {
        if (value instanceof String) {
            String CleanValue = ((String) value).replaceAll ("\\s", "");
            this.values.set (index, CleanValue);
        }
        else {
            this.values.set (index, value);
        }
    }

    Object select(int index) {
        return this.values.get (index);
    }

    int width() {
        return this.values.size ();
    }

    Boolean exists (int index, Object value) {
        /*
            Returns true, if the record contains the value at the given field index
        */
        if (value instanceof String) {
            /* for string, remove spaces to compare */
            value = value.toString ().replaceAll ("\\s", "");
        }
        if (select (index).equals (value)) {
            return true;
        }
        return false;
    }
}