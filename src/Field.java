public class Field {

    private Type type;
    private String name;
    private Boolean primaryKey=false;
    private Field ForeignKey=null;

    /* --------------------------- */

    void setType (String type) {
        this.type = new Type (type);
    }

    void setName (String name) {
        this.name = name;
    }

    void setIsKey () {
        this.primaryKey = true;
    }

    void setForeignKey (Field field) {
        this.ForeignKey = field;
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

    Field getForeignKey() {
        return this.ForeignKey;
    }
}
