package edu.umd.mindlab.androidservicetest;

/**
 * Created by User on 9/24/2017.
 */

public class LoggedIn {

    private static LoggedIn logged = new LoggedIn();
    private boolean isLoggedIn;

    private LoggedIn(){}

    public static LoggedIn getLog(){

        return logged;
    }

    public void setLoggedIn(boolean bool){

        isLoggedIn = bool;
    }

    public boolean getLoggedIn(){

        return isLoggedIn;
    }

}
