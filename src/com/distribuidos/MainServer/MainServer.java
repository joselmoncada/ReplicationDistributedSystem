package com.distribuidos.MainServer;

import com.distribuidos.Request.FillJarRequest;
import com.distribuidos.Request.GetMovementsRequest;
import com.distribuidos.Request.ProductRequest;
import com.distribuidos.Response.GetMovementsResponse;
import com.distribuidos.Response.ProductResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {

    private ServerSocket serverSocket = null;

    public static void main(String[] args) {

        new MainServer().startServer(4444);
    }

    public void startServer(int port) {

        try {
            System.out.println("SERVIDOR ESPERANDO CLIENTES EN PUERTO: "+port);
            serverSocket = new ServerSocket(port);
            while (true){
                System.out.println("Acepta nuevo cliente "+port);
                Socket clientSocket = serverSocket.accept();
                MainServerClientHandler clientHandler= new MainServerClientHandler(clientSocket);
                clientHandler.start();


            }

        } catch (IOException e) {
            System.out.println("Error: "+e);
            e.printStackTrace();
        }

    }

    private class MainServerClientHandler extends Thread {
        private Socket clientSocket;
        public ObjectInputStream in;
        public ObjectOutputStream out;

        public MainServerClientHandler(Socket socket) {
            this.clientSocket = socket;

        }

        public void run() {



            try {
                System.out.println("Server accepted new client, Thread: "+Thread.currentThread().getId());
                in = new ObjectInputStream(clientSocket.getInputStream());
                out = new ObjectOutputStream(clientSocket.getOutputStream());


                Object request =  in.readObject(); //Receives a request
                System.out.println("REQUEST NAME: "+ request.getClass().getSimpleName());
                synchronized (this) { //Only one action happens at the same time
                    switch (request.getClass().getSimpleName()) {

                        case "GetMovementsRequest": //The consumer request the transactions log of the Jar
                            GetMovementsRequest movementsRequest = (GetMovementsRequest) request;
                            System.out.println("Movements Request received: " + movementsRequest);
                            //todo logica para lectura de JSON y responder con los movimientos

                            GetMovementsResponse getMovementsResponse = null;
                            out.writeObject(getMovementsResponse); //Sending response to client
                            break;

                        case "ProductRequest": //The consumer request an item from the Jar

                            ProductRequest productRequest = (ProductRequest) request;
                            System.out.println("Ingredient Request received: " + productRequest);
                            //log("Ingredient Request received: " + ProductRequest);
                            ProductResponse productResponse =null;
                            //**todo logica para solicitar ingrediente al Jar mediante RMI**
                            //response = askForItem(ProductRequest.productType, ProductRequest.quantity );
//                            if ( response!= null && response.requestedIngredient != null) {
//                                System.out.println("Item found, sending as response: " + response);
//                            } else {
//                                System.out.println("Sorry Item: " + ProductRequest.requestedIngredient.itemName + " not found at " + ProductRequest.storeId + ", response: " + response);
//                            }
                            out.writeObject(productResponse); //Sending response to client
                            //log("Ingredient Request response: " + response);


                            break;
                        case "FillJarRequest": //The Producer is filling the jar
                            /**Producer Fill jar**/
                            FillJarRequest fillJarRequest = (FillJarRequest) request;
                            System.out.println("Filling request received: " + fillJarRequest);
                            //log("Filling request received: " + fillJarRequest +" origin: Smoker#"+ fillJarRequest.sender);

                            //**todo logica de llenar el tarro mediante RMI***

                            //System.out.println("Filling request fulfilled: " + storeList);
                            // log("Filling request fulfilled: " + storeList);
                            break;
                        case "SaveStateRequest": //The producer request to save current status

                            //todo manejar logica para coordinar los commit y hacer el backup
                            break;
                        case "RestoreLastStatusRequest": //The producer request to restore last status
                            //todo manejar logica de restauracion del estado.
                            break;
                        default:
                            System.out.println("Request not recognized, request received: " + request.getClass().getSimpleName());
                            //log("Server received an unrecognized Request : " + request);
                            break;
                    }

                    in.close();
                    out.close();
                    clientSocket.close();
                    System.out.println("Server closed socket for request:"+request+", Thread: "+Thread.currentThread().getId());
                   // log("Server closed socket for request:"+request+", Thread: "+Thread.currentThread().getId());
                }




            } catch (IOException
                    | ClassNotFoundException
                    e) {
                e.printStackTrace();
            }
        }
    }


}
