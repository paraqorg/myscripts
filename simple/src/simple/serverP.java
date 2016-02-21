/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simple;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Lenovo
 */
public class serverP extends UnicastRemoteObject implements firstRMI{

    public serverP() throws RemoteException {
        super();
    }
    
    
    
    @Override
    public String echo(String input) throws RemoteException {
        return "from server" + input; //To change body of generated methods, choose Tools | Templates.
    }
    
}
