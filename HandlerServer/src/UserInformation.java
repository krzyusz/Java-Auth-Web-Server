import com.google.common.hash.Hashing;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;

public class UserInformation {

    private String id;
    private String connectionId;
    private String hash;
    private String b1;
    private String b2;
    private String token;
    private String browserId;
    private String userId;
    private String site;
    private boolean authorized = false;

    public UserInformation(){
        int random = (int) (Math.random() * ((System.currentTimeMillis()) + 1));
        this.id = Hashing.sha256().hashString(Integer.toString(random), StandardCharsets.UTF_8).toString().substring(0,10);
    }

    public void addInfoFromResponse(ResponseInformation ri){
        this.b1 = ri.getb1();
        this.b2 = ri.getb2();
        this.token = ri.getToken();
    }

    public void addInfoFromConnection(ConnectionData cd){
        this.hash = cd.getHash();
        this.connectionId = cd.getConnectionId();
    }

    public void addInfoFromRequest(NewClientConnection cc){
        this.browserId = cc.getBrowserId();
        this.userId = cc.getUserId();
    }

    public String getId(){
        return this.id;
    }

    public String getJsonString(){
        Gson g = new Gson();
        return g.toJson(this);
    }

    public void addSite(String url){
        this.site = url;
    }

    public void authorize(){
        this.authorized = true;
    }

    public boolean checkAuth(){
        return this.authorized;
    }

    public String getConnectionid(){
        return this.connectionId;
    }

}
