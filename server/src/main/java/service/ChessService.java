package service;

import dataAccess.DataMemory;
import model.User;

public class ChessService {
    private final DataMemory dataAccess;

    public ChessService(DataMemory dataAccess){
        this.dataAccess = dataAccess;
    }
    public void register(User user) throws Exception{
        if (dataAccess.getUser(user) == null){
            dataAccess.createUser(user);
        }
        throw new Exception("not implemented (service)");
    }
}