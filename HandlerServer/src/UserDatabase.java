import java.util.ArrayList;
import java.util.HashMap;

public class UserDatabase {

    private HashMap<String, UserInformation> database= new HashMap<String, UserInformation>();

        public void addUser(UserInformation ui){
            if(this.getUser(ui.getConnectionid())!=null){
                this.deleteUser(ui.getConnectionid());
            }
            this.database.put(ui.getConnectionid(),ui);
        }

        public UserInformation getUser(String connectionId){
            return this.database.get(connectionId);
        }

        public void deleteUser(String connectionId){
            this.database.remove(connectionId);
        }
}
