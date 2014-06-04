package database;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import mission.Mission;
 
/**
 * Provides functions to serialize and deserialize the missions that will be stored or loaded from the database
 * @author Xavier Gibert
 *
 */
public class SerializationUtil {
 
	/**
	 * Deserialize from byte array to Mission object
	 * @param serialized
	 * @return
	 */
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
 
    /**
     * Serialize the given object to byte array
     * @param obj
     * @return
     * @throws IOException
     */
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