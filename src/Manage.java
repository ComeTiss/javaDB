
public class Manage {

    /* This collection of objects aims to provide a framework to create and manage databases
       The aim of this class is to create/interact with database/tables.

       Examples of stuff you could do ...
       - You an create a table or a database
       - a table can be added to a database (See Table class for more details)
       - a table can be filled with values (manually or loading from a file)
       - a table can be saved
       - a database can be saved

        A user interface wasn't developed, however you can find some useful examples below:
     */


    public static void main (String[] args) {
        Manage program = new Manage();
        program.run();
    }

    private void run() {

        /* Example 1: create a Table, fill manually and save it into a file */
        Table table1 = new Table("table 1");
        table1.getFields().set("id", "Name", "Kind", "Owner", "Member");
        table1.getFields ().setType ("id", "Integer");
        table1.getFields ().setType ("Name", "String");
        table1.getFields ().setType ("Kind", "String");
        table1.getFields ().setType ("Owner", "String");
        table1.getFields ().setType ("Member", "Boolean");
        table1.getFields ().setKey ("id");
        table1.create(1);
        table1.update(1, "Name", "Bobby");
        table1.update(1, "Kind", "dog");
        table1.update(1, "Owner", "io18245");
        table1.update(1, "Member", true);
        table1.create(2);
        table1.update(2, "Name", "Tom");
        table1.update(2, "Kind", "dog");
        table1.update(2, "Owner", "fs34567");
        table1.update(2, "Member", true);
        table1.create(3);
        table1.update(3, "Name", "Garfield");
        table1.update(3, "Kind", "cat");
        table1.update(3, "Owner", "bn56123");
        table1.update(3, "Member", false);
        table1.save ("data.txt");
        table1.show ();

        /* Example 2: Create a table and load data into it from file */
        Table table2 = new Table ("table 2");
        table2.load ("data.txt"); // file created in Example 1
        table2.create (4);
        table2.update(4, "Name", "Nelson");
        table2.update(4, "Kind", "fish");
        table2.update(4, "Owner", "to12768");
        table2.update(4, "Member", true);
        table2.show ();

        /* Example 3: Manipulate data from an existing table and save it into a new table */
        Table table3 = table2.filterByValue ("Kind", "dog").filterByField ("Name", "Owner");
        table3.getFields ().setKey ("Name");
        table3.alter ("Price", "String");
        table3.update("Bobby","Price", "300$");
        table3.update("Tom","Price", "150$");
        table3.show ();

        /* Example 4: create a Database, populate it, and save it */
        Database db = new Database ("mydb");
        db.add (table1, table2, table3);
        db.save ();
        db.show ();

        /* Example 5: make queries on a table */
        System.out.println ("\n-------- Example 5: Queries -------");
        System.out.println (
        "\nQuery a Column: " + table1.selectColumn ("Owner") +
        "\nQuery a Value : " + table1.selectOne ("Kind", 2) +
        "\nQuery a Record: " + table1.selectRow (1));
    }
}
