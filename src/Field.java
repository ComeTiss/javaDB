public class Field {
    /*
        Defines the property of a column, including the name, type and key.
        Only on Field can be set as primary key,
        If so, values will be unique within the column
    */

    private Type type = null;
    private String name;
    private Boolean primaryKey=false;

    Field (String name) {
        this.name = name;
    }


    /* --------------------------- */

    void setType (Type type) {
        this.type = type;
    }

    void setIsKey () {
        this.primaryKey = true;
    }

    Type getType() {
        return this.type;
    }

    String getName() {
        return this.name;
    }

    Boolean isKey() {
        return this.primaryKey;
    }

}
