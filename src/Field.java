public class Field {

    private Type type = null;
    private String name;
    private Boolean primaryKey=false;
    private Field ForeignKey=null;

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
