public class Field {

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
