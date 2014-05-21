package database;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import mission.Mission;

import org.orekit.propagation.SpacecraftState;
 
/**
 * A simple class with generic serialize and deserialize method implementations
 *
 * @author pankaj
 *
 */
public class SerializationUtil {
 
    // deserialize to Object from given file
    public static Mission deserialize(byte[] serialized) {
    	try {
    	     //byte b[] = serialized.getBytes(); 
    	     ByteArrayInputStream bi = new ByteArrayInputStream(serialized);
    	     ObjectInputStream si = new ObjectInputStream(bi);
    	     Mission obj = (Mission) si.readObject();
    	     si.close();
    	     return obj;
    	 } catch (Exception e) {
    	     System.out.println(e);
    	     return null;
    	 }
    }
 
    // serialize the given object and save it to file
    public static byte[] serialize(Object obj)//str.getBytes("UTF-8")
            throws IOException {
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(obj);
        oos.flush();
        //String tmp = fos.toString();
        byte[] ba = fos.toByteArray();
        fos.close();
        return ba;
    }
 
}