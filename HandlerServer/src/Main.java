import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Acl;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Properties;
import java.io.IOException;



public class Main {
    private static UserDatabase db = new UserDatabase();
    public static void main(String[] args) throws IOException {
        Properties p = new Properties();
        p.setProperty("serverKey","**SERVER_KEY**");

        /*
        server AES key generating
         */
        FileInputStream serviceAccount =
                null;
        try {
            serviceAccount = new FileInputStream("C:\\Users\\Krzysiek\\Downloads\\tokenauthapp-firebase-adminsdk-dzmvn-b1c2213763.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        FirebaseOptions options = null;
        try {
            options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://tokenauthapp.firebaseio.com")
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FirebaseApp.initializeApp(options);
        var address = new InetSocketAddress("0.0.0.0", 7070);
        startServer(address);


    }

    public static void startServer(InetSocketAddress address){
        System.out.println("Listening for connection on port 7070 ....");
        try(var server = getServerSocket(address)){
            while (true) {
                try (Socket socket = server.accept()) {
                    Gson g = new Gson();
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String line;
                    while((line = inFromClient.readLine()) != null){
                        if(line.isEmpty()){
                            break;
                        }
                        String clientSentence = line;
                        System.out.println("Received: " + clientSentence+"\n");
                    }

                     StringBuilder payload = new StringBuilder();

                    while(inFromClient.ready()) {
                        payload.append((char) inFromClient.read());
                    }
                    String payloadString = payload.toString();
                    System.out.println("Payload data is: "+payloadString);
                    JsonObject connObj = g.fromJson(payloadString,JsonObject.class);
                    System.out.println("ConnOBJ: "+connObj.toString());
                            switch(connObj.get("type").getAsString()){
                                case "newconnection":
                                    NewClientConnection connData = g.fromJson(payloadString, NewClientConnection.class);
                                    if(connData==null) {
                                        handleWrongRequest(socket,g);
                                        System.out.println("Connection data is empty!");
                                        System.out.println("Expected parameters: type, browserId, hash, userId");
                                    }else {
                                        addFunction(socket, g, connData);
                                        socket.close();
                                    }
                                    break;
                                case "login":
                                    loginUser(socket,payloadString,g);
                                    socket.close();
                                    break;
                                case "register":
                                    if(payloadString.contains("connectionId")){
                                        // user verified
                                        System.out.println("ok");
                                        ResponseInformation resp = g.fromJson(payload.toString(),ResponseInformation.class);
                                        UserInformation ui = db.getUser(resp.getConnectionId());
                                        if(ui!=null){ // check if connected user already exists
                                            ui.addInfoFromResponse(resp);
                                            db.addUser(ui);
                                            System.out.println(ui.getJsonString());
                                            System.out.println("baza:" + db);
                                        }

                                    }
                                    break;
                                case "authorize":
                                    authorizeLogin(g,connObj);
                                    break;
                                case "getLoginData":
                                    returnLoginData(socket,g,connObj);
                                    break;
                                default:
                                    System.out.println("Wrong type of connection type!");
                                    break;
                            }

                /*
                 request validation
                 */


                }

            }
        }catch (Exception e) {
            System.err.println("Could not create socket at " + address);
            e.printStackTrace();
        }


    }

    private static ServerSocket getServerSocket(InetSocketAddress address)
            throws Exception {

        // Backlog is the maximum number of pending connections on the socket,
        // 0 means that an implementation-specific default is used
        int backlog = 0;

        var keyStorePath = Path.of("./keystore.jks");
        char[] keyStorePassword = "Qwerty132#".toCharArray();

        // Bind the socket to the given port and address

        /*var serverSocket = getSslContext(keyStorePath, keyStorePassword)
                .getServerSocketFactory()
                .createServerSocket(address.getPort(), backlog, address.getAddress());*/

        ServerSocket serverSocket = new ServerSocket(7070);
        // We don't need the password anymore → Overwrite it
        Arrays.fill(keyStorePassword, '0');

        return serverSocket;
    }

    private static SSLContext getSslContext(Path keyStorePath, char[] keyStorePass)
            throws Exception {

        var keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream(keyStorePath.toFile()), keyStorePass);

        var keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, keyStorePass);

        var sslContext = SSLContext.getInstance("TLS");
        // Null means using default implementations for TrustManager and SecureRandom
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
        return sslContext;
    }

    public static void addFunction(Socket socket, Gson g, NewClientConnection connData){
        if(connData.validateHash()){ // check if received message wasn't interrupted
            System.out.println("Connection ok");
            System.out.println(connData.getBrowserId());

            //generate server response to client
            ConnectionData cd = new ConnectionData();
            cd.generateConnId();

            //fill userInfo with known data
            UserInformation connectedUser = new UserInformation();
            connectedUser.addInfoFromConnection(cd);
            connectedUser.addInfoFromRequest(connData);

            //add user to connection database
            db.addUser(connectedUser);

            System.out.println("Connected user: " + connectedUser.getJsonString());

            String cdS = g.toJson(cd);
            DataOutputStream os = null;
            try {
                os = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            PrintWriter pw = new PrintWriter(os,true);
            System.out.println("Connection data: " + cdS);

            pw.println("["+cdS+"]");
            //pw.println(g.toJson("asdasdasd"));
            pw.flush();
        }else{
            System.out.println("Incorrect hash");
        }
    }


    public static void handleWrongRequest(Socket socket,Gson g){
        DataOutputStream os = null;
        try {
            os = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }


        PrintWriter pw = new PrintWriter(os);
        pw.println(g.toJson("Wrong request type"));
        pw.flush();
    }

    public static void loginUser(Socket socket, String payloadString,Gson g){
        LoginData loginData = g.fromJson(payloadString,LoginData.class);
        UserInformation ui = db.getUser(loginData.getConnectionId());
        ui.addSite(loginData.getSiteURL());
        db.addUser(ui);
        DataOutputStream os = null;
        try {
            os = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(os,true);
        pw.println("["+g.toJson("logged succesfully")+"]");
        //pw.println(g.toJson("asdasdasd"));
        pw.flush();


        String registrationToken = "**reg_token**";

        Message message = Message.builder()
                .putData("body", "Krzysiek")
                .putData("title", loginData.getSiteURL())
                .putData("connectionId", loginData.getConnectionId())
                .putData("hash",loginData.getHash())
                .setToken(registrationToken)
                .build();

        String response = null;
        try {
            response = FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Successfully sent message: " + response);

        System.out.println("site url: " + loginData.getSiteURL());
        System.out.println("User database: "+ db.toString());
    }

    public static void authorizeLogin(Gson g, JsonObject connObj){
        System.out.println(g.toJson(db.getUser("bd3379ff2abb77b")));
        UserInformation ui = db.getUser(connObj.get("connectionId").getAsString());
        System.out.println("UserDatabase: "+ db.toString());
        System.out.println("UserInformation: " + g.toJson(ui));
        ui.authorize();
        db.addUser(ui);
        System.out.println("user authorized");
    }

    public static void returnLoginData(Socket socket, Gson g, JsonObject connObj){
        if((db.getUser(connObj.get("connectionId").getAsString())==null)){
            return;
        }
        if(db.getUser(connObj.get("connectionId").getAsString()).checkAuth()){
            DataOutputStream os = null;
            try {
                os = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            JsonObject loginData = new JsonObject();
            loginData.addProperty("login","**login**");
            loginData.addProperty("passw","**password**");
            PrintWriter pw = new PrintWriter(os,true);
            pw.println("["+g.toJson(loginData)+"]");
            pw.flush();
            System.out.println("login data sent");
        }else{
            System.out.println("User not authorized for this site");
        }
    }

}
