/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javausb;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author aditya
 */
public class JavaUsb {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        USBConnection u = new USBConnection();
        int value = u.ReadMessage();
        if(value == -1){
            System.out.println("NoTHING TO do here");
        }
        else{
            System.out.println("Value read" + value);
        }
        
        int out = 2;
        u.WriteMessage(out);
        
    }
}
