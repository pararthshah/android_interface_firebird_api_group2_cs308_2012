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
public class USBConnection {
    
    public Host host;
    public Bus[] bus;
    public int total_bus;
    public int total_port;
    public int total_interface;
    public Device root;
    public Device device;
    public Configuration config;
    
    public int ReadMessage(){
        int data = -1;
       for (int k=0; k<total_interface; k++)
       {
           try{
               Interface itf = config.getInterface(k, 0);
               int total_ep  = itf.getNumEndpoints();
               for (int l=0; l<total_ep; l++)
                {
               // Access the endpoint, and obtain its I/O type.
                   Endpoint ep = itf.getEndpoint(l);
                   String io_type = ep.getType();
                   if (ep.isInput())
                    {
                        InputStream in;
                        in = ep.getInputStream();
                   // Read in data here
                        data = in.read();
                        in.close();
                    }
                }

           }
           catch(Exception e){
               
           }
        
       }
       return data;
    }
           
           // Traverse through all the endpoints.
           
               // If the endpoint is an input endpoint, obtain its
               // InputStream and read in data.
                              // If the Endpoint is and output Endpoint, obtain its 
               // OutputStream and write out data.
               /*else
               {
                   OutputStream out;
                   out = ep.getOutputStream();
                   // Write out data here.
                   out.close();
               }*/
    
    public void WriteMessage(int data){
       for (int k=0; k<total_interface; k++)
       {
           try{
               Interface itf = config.getInterface(k, 0);
               int total_ep  = itf.getNumEndpoints();
               for (int l=0; l<total_ep; l++)
                {
               // Access the endpoint, and obtain its I/O type.
                   Endpoint ep = itf.getEndpoint(l);
                   String io_type = ep.getType();
                   if (!ep.isInput())
                    {
                        OutputStream out;
                        out = ep.getOutputStream();
                        out.write(data);
                        out.close();
                    }
                }

           }
           catch(Exception e){
               
           }
        
       } 
    }
    
    public USBConnection(){
        try{
            host = HostFactory.getHost();
            bus  = host.getBusses();
            total_bus = bus.length;
            for (int i=0; i<total_bus; i++)
            {
                root = bus[i].getRootHub();
                total_port = root.getNumPorts();
                for (int j=1; j<=total_port; j++)
                {
                    device = root.getChild(j);
                }
            }
            if (device != null)
                    {
                        {
                           config = device.getConfiguration();
                           total_interface = config.getNumInterfaces();

                           for (int k=0; k<total_interface; k++)
                           {
                               // Access the currently Interface and obtain the number of 
                               // endpoints available on the Interface. 
                               Interface itf = config.getInterface(k, 0);
                               int total_ep  = itf.getNumEndpoints();

                               // Traverse through all the endpoints.
                               for (int l=0; l<total_ep; l++)
                               {
                                   
                               }
                           }
                        }
                    }
        }
        catch(Exception e){
            
        }
        
    }
    
}
