package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import websocket.messages.ServerMessage;

public class SessionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();
    //Have second map of session to gameID?

    public void add(int gameID, Session session){ //could add synchronize keyword to all these critical functions
        try{
            //situation is which game already exists
            connections.get(gameID).add(session);
        }catch(NullPointerException e) { //change to normal exception?
            //situation in which game doesn't exist
            Set<Session> mySessions = new HashSet<>();
            mySessions.add(session);
            connections.put(gameID, mySessions);
        }
    }

    public void remove(int gameID){connections.remove(gameID);}

    public void remove(Session session){
        Integer id = null;
        for(var entry : connections.entrySet()){
            Set<Session> sessions = entry.getValue();
            for(Session ses : sessions){
                if (ses.equals(session)) {
                    id = entry.getKey();
                    break;
                }
            }
        }
        var current = connections.get(id);
        current.remove(session);
    }

    public Set<Session> getSessions(int gameID){return connections.get(gameID);}
}
