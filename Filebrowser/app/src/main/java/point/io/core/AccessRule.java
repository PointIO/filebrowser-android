package point.io.core;

/**
 * Created by dylan on 10/27/14.
 */
public class AccessRule {
    public String arName;
    public String arId;
    public AccessRule(String id,String name){
        arId = id;
        arName = name;
    }

    public String toString(){
        return this.arName;
    }
}
