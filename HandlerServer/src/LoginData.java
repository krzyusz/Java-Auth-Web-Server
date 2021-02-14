public class LoginData {
    private String connectionId;
    private String hash;
    private String siteURL;

    public String getSiteURL(){
        return this.siteURL;
    }

    public String getHash(){
        return this.hash;
    }

    public String getConnectionId(){
        return this.connectionId;
    }
}
