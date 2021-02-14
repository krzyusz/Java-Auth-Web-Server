import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ConnectionData {
    private String connectionId;
    private String hash;


    public void generateConnId(){
        this.connectionId = generateRandomString().substring(10,25);
        String hashString = Hashing.sha512().hashString(this.connectionId.substring(5,10),StandardCharsets.UTF_8).toString();
        this.hash = hashString;
        System.out.println("Connection Id: " + this.connectionId);
        System.out.println("Hash: " + this.hash);
    }

    private static String generateRandomString(){
        int random = (int) (Math.random() * ((System.currentTimeMillis()) + 1));
        System.out.println("Random number: " + Integer.toString(random));
        String s = Hashing.sha512().hashString(Integer.toString(random), StandardCharsets.UTF_8).toString();
        System.out.println("Hash: " + s);
        return s;
    }

    public String getConnectionId(){
        return this.connectionId;
    }

    public String getHash(){
        return this.hash;
    }
}
