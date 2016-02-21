/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simple;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author Lenovo
 */
public class applicationserver {
    public static void main(String[] args) throws RemoteException {
        Registry reg = LocateRegistry.createRegistry(5099);
        reg.rebind("hello", new serverP());
    }
            
    
}
