package service;

import dataAccess.DataAccess;
import model.User;
import spark.Request;

public class ChessService {

    public static void register(User user) throws Exception{
        DataAccess.register(user);
        throw new Exception("not implemented (service)");
    }
    public static void addUser() throws Exception{
        throw new Exception("not implemented (service)");
    }
}