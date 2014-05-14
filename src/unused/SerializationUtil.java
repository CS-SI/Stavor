package unused;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.orekit.propagation.SpacecraftState;
 
/**
 * A simple class with generic serialize and deserialize method implementations
 *
 * @author pankaj
 *
 */
public class SerializationUtil {
 
    // deserialize to Object from given file
    public static SpacecraftState deserialize(String serialized) throws IOException,
            ClassNotFoundException {
    	try {
    	     byte b[] = serialized.getBytes(); 
    	     ByteArrayInputStream bi = new ByteArrayInputStream(b);
    	     ObjectInputStream si = new ObjectInputStream(bi);
    	     SpacecraftState obj = (SpacecraftState) si.readObject();
    	     si.close();
    	     return obj;
    	 } catch (Exception e) {
    	     System.out.println(e);
    	     return null;
    	 }
    }
 
    // serialize the given object and save it to file
    public static String serialize(Object obj)//str.getBytes("UTF-8")
            throws IOException {
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(obj);
        oos.flush();
        String tmp = fos.toString();
        fos.close();
        return tmp;
    }
 
}