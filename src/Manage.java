
public class Manage {

    public static void main (String[] args) {
        Manage program = new Manage();
        program.run();
    }

    private void run() {
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

        table1.save ("src/data.txt");

        Table table2 = new Table ("table 2");
        table2.write ("src/data.txt");
        table2.show ();

        Table t = table1.filterByValue ("Kind", "dog").filterByField ("Name", "Owner");
        t.getFields ().setKey ("Name");
        t.alter ("Price", "String");
        t.update("Bobby","Price", "300$");
        t.update("Tom","Price", "150$");
        t.show ();
    }
}
