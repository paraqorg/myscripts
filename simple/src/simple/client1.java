/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simple;
import java.net.MalformedURLException;
import java.rmi.*;
import static java.rmi.Naming.lookup;
/**
 *
 * @author Lenovo
 */
public class client1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NotBoundException, MalformedURLException, RemoteException {
        
        firstRMI service = (firstRMI) Naming.lookup ("rmi://localhost:5099/hello");
        System.out.println("-----helloe "+ service.echo("hey ser client 1"));
// TODO code application logic here
    }
    
}
