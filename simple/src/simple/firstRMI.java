/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simple;
import java.rmi.*;
/**
 *
 * @author Lenovo
 */
public interface firstRMI extends Remote{
    public String echo (String input) throws RemoteException;
    
}
