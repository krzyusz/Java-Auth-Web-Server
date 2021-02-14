import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class NewClientConnection {

    private String type;
    private String browserId;
    private String hash;
    private String userid;


    public String getType(){
        return this.type;
    }

    public String getBrowserId(){
        return this.browserId;
    }

    public String getHash(){
        return this.hash;
    }

    public String getUserId(){
        return this.userid;
    }

    public boolean validateHash(){
        String h = Hashing.sha256().hashString(this.browserId, StandardCharsets.UTF_8).toString();
        System.out.println("browserid hash: " + h);
        System.out.println("json hash: " + this.getHash());
        if(h.equals(this.hash)){
            return true;
        }else{
            return false;
        }
    }
}
