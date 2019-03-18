import java.io.File;
import java.util.ArrayList;

public class Database {
    /*
       We define a Database as a collection of Tables
   */
    private String name;
    private ArrayList<Table> db = new ArrayList<> ();

    Database (String name) {
        this.name = name;
    }


    /* ---------- Methods ---------- */
    int count () {
        return this.db.size ();
    }

    String getName () {
        return this.name;
    }

    void changeName (String name) {
        this.name = name;
    }

    ArrayList<Table> getAll () {
        return this.db;
    }

    Table getOne (String name) {
        if (TableExists (name)) {
            return this.db.get (index (name));
        }
        return null;
    }

    void add (Table ...newTables) {
        /*
           A database can only contain one copy of a Table,
           Once it contains it, it will ignore future adding of it
        */
        for (Table t: newTables) {
            if (! TableExists (t.getName ())) {
                this.db.add(t);
            }
        }
    }

    void delete (String name) {
        /* delete a table from the database */

        if (TableExists (name)) {
            this.db.remove (index (name));
        }
    }

    void save () {
        /*
           Will create text file for each table of the database
           And save their content into it
           All text files are saved within same folder
        */
        String dbFolder = this.name;
        if (! new File(dbFolder).mkdir()) {
            Display display = new Display ();
            display.ErrorCreatingDatabase (this.name);
        }
        for (Table t : this.db) {
            String file = dbFolder + "/" + t.getName() + ".txt";
            t.save (file);
        }
    }

    void show () {
        Display display = new Display ();
        display.ShowDatabase (this.name, this.db);
    }

    private int index (String name) {
        /*
           Return current index of a Table in Database
        */
        int pos=0;

        for (Table t : this.db) {
            if (t.getName ().equals (name)) {
                pos = db.indexOf (t);
            }
        }
        return pos;
    }

    private Boolean TableExists (String name) {
        /*
            check if database contains the given Table
        */
        for (Table t : this.db) {
            if (t.getName ().equals (name)) {
                return true;
            }
        }
        return false;
    }

    /* ----------- TESTING ----------- */

    public static void main(String[] args) {
        Database program = new Database("DB");
        program.run();
    }

    private void run() {
        testAdd();
        testIndex();
        testTableExists();
        testGetAll();
        testGetOne();
        testDelete();
        System.out.println ("All tests passed");
    }

    private void testAdd () {
        Database testDB = new Database ("DB");
        Table test = new Table ("test");
        Table test2 = new Table ("test2");

        assert (testDB.count () == 0);
        testDB.add (test, test, test);
        assert (testDB.count () == 1);
        testDB.add (test2);
        assert (testDB.count () == 2);
        assert (testDB.db.contains (test));
        assert (testDB.db.contains (test2));
    }

    private void testIndex () {
        Database testDB = new Database ("DB");
        Table test = new Table ("test");
        Table test2 = new Table ("test2");

        testDB.add (test, test2);
        assert (testDB.index ("test") == 0);
        assert (testDB.index ("test2") == 1);
    }

    private void testTableExists () {
        Database testDB = new Database ("DB");
        Table test = new Table ("test");
        Table test2 = new Table ("test2");
        testDB.add (test, test2);

        assert (testDB.TableExists ("test"));
        assert (testDB.TableExists ("test2"));
        assert (! testDB.TableExists ("unknown"));
    }

    private void testGetAll () {
        Database testDB = new Database ("DB");
        Table test = new Table ("test");
        Table test2 = new Table ("test2");
        testDB.add (test, test2);

        assert (testDB.getAll ().size () == 2);
        assert (testDB.getAll ().contains (test));
        assert (testDB.getAll ().contains (test2));
    }

    private void testGetOne () {
        Database testDB = new Database ("DB");
        Table test = new Table ("test");
        Table test2 = new Table ("test2");
        testDB.add (test, test2);

        assert (testDB.count () == 2);
        assert (testDB.getOne ("test") == test);
        assert (testDB.getOne ("test2") == test2);
        assert (testDB.count () == 2);
    }

    private void testDelete () {
        Database testDB = new Database ("DB");
        Table test = new Table ("test");
        Table test2 = new Table ("test2");
        testDB.add (test, test2);

        assert (testDB.count () == 2);
        testDB.delete ("test");
        assert (testDB.count () == 1);
        assert (! testDB.getAll ().contains (test));
        assert (testDB.getAll ().contains (test2));
    }
}
