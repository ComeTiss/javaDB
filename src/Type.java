import java.util.ArrayList;
import java.util.Arrays;

public class Type {
    /*
        This class defines the data type of a Field.
    */

    private String type;
    private ArrayList<String> AllowedTypes = new ArrayList<> (Arrays.asList ("String", "Integer", "Boolean"));

    Type (String type) {
        this.type = type;
    }

    String get() {
        return this.type;
    }

    Boolean isValid (String type) {
        /* Check that type given is allowed
        */
        if (this.AllowedTypes.contains (type)) {
            return true;
        }
        return false;
    }
}
