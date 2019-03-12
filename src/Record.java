import java.util.ArrayList;

public class Record {

    // Definitions
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
        this.values.set (index, value);
    }

    Object select(int index) {
        return this.values.get (index);
    }

    int width() {
        return this.values.size ();
    }

    void show() {
        for (int i = 0; i < width (); i++) {
            System.out.printf ("%-13s  ", select(i));
        }
        System.out.print ("\n");
    }

}