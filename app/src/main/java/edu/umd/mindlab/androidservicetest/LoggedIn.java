package edu.umd.mindlab.androidservicetest;

/**
 * Created by User on 9/24/2017.
 */

public class LoggedIn {

    private static LoggedIn logged = new LoggedIn();
    private boolean isLoggedIn;
    private String fullName;

    private LoggedIn(){
        isLoggedIn = false;
        fullName = "No name provided";
    }

    public static LoggedIn getLog(){

        return logged;
    }

    public void setLoggedIn(boolean bool){

        isLoggedIn = bool;
    }

    public boolean getLoggedIn(){

        return isLoggedIn;
    }

    public String getName(){

        return fullName;
    }

    public void setName(String fName){

        fullName = fName;
    }

    public void destroyName(){

        fullName = "DESTROYED";
    }

}
