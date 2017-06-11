package com.example.user.seatapp;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;



public class MyClientTask extends AsyncTask<Void, Void, Void> {

    private char Querry_type;
    private final String dstAddress = "192.168.1.42";
    private int dstPort = 8888;

    String response_from_server = "-";
    public boolean sent_to_server = false;


    private String Vehicle_type, Vehicle_company, city,Vehicle_number,Current_stop; //  view AND get
    private String NewPassword,NewEmail; // register AND login
    private static String NewUsername;

    public MyClientTask(char qt,String first, String second, String third, String fourth, String fifth) {

        Querry_type = qt;
        switch (Querry_type)
        {
            case 'r':
                NewEmail = third;
            case 'l':
                NewPassword = second;
            case 'E':
                NewUsername = first;
                break;
            case 'g':
                Current_stop = fifth;
            case 'S':
            case 'v':
                Vehicle_number = fourth;
            case 'N':
                Vehicle_type = first;
                Vehicle_company = second;
                city = third;
                break;

        }
    }

    //Execute()
    @Override
    protected Void doInBackground(Void... arg0) {

        try {
            String string_to_send = "";
            String data_from_server;
            switch (Querry_type)
            {
                case 'E':
                    string_to_send = ";" + Querry_type + ";" + String.valueOf(NewUsername.length()) + ";" + NewUsername;
                    break;
                case 'r':
                    string_to_send = ";" + Querry_type + ";" + String.valueOf(NewUsername.length()) + ";" + NewUsername + ";" + String.valueOf(NewPassword.length()) + ";" + NewPassword + ";" + String.valueOf(NewEmail.length()) + ";" + NewEmail;
                    break;
                case 'l':
                    string_to_send = ";" + Querry_type + ";" + String.valueOf(NewUsername.length()) + ";" + NewUsername + ";" + String.valueOf(NewPassword.length()) + ";" + NewPassword;
                    break;
                case 'g':
                    string_to_send = ";" + Querry_type + ";" + String.valueOf(Vehicle_type.length()) + ";" + Vehicle_type + ";" + String.valueOf(Vehicle_company.length()) + ";" + Vehicle_company + ";" + String.valueOf(city.length()) + ";" + city + ";"+String.valueOf(Vehicle_number.length()) + ";" + Vehicle_number+ ";" + String.valueOf(Current_stop.length())+ ";" + Current_stop;
                    break;
                case 'v':
                case 'S':
                    string_to_send = ";" + Querry_type + ";" + String.valueOf(Vehicle_type.length()) + ";" + Vehicle_type + ";" + String.valueOf(Vehicle_company.length()) + ";" + Vehicle_company + ";" + String.valueOf(city.length()) + ";" + city + ";"+String.valueOf(Vehicle_number.length()) + ";" + Vehicle_number;
                    break;
                case 'N':
                    string_to_send = ";" + Querry_type + ";" + String.valueOf(Vehicle_type.length()) + ";" + Vehicle_type + ";" + String.valueOf(Vehicle_company.length()) + ";" + Vehicle_company + ";" + String.valueOf(city.length()) + ";" + city;
                    break;
            }


            Socket socket = new Socket(dstAddress, dstPort);
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.writeUTF(string_to_send); //The msg to the server
            output.flush(); // Send off the data

            //read input stream
            DataInputStream input = new DataInputStream(socket.getInputStream());
            InputStreamReader input_reader = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(input_reader); //create a BufferReader object for input

            if (Querry_type=='E')
                sent_to_server = true;

            else
            {
                data_from_server = br.readLine();
                switch (Querry_type)
                {
                    case 'r':
                    case 'l':
                        if ((data_from_server).contains(Querry_type+";0"))
                            response_from_server = "0";
                        else if ((data_from_server).contains(Querry_type+";1"))
                            response_from_server = "1";
                        break;
                    case 'S':
                    case 'N':
                    case 'v':
                    case 'g':
                        if ((data_from_server).contains(Querry_type + ";"))
                            response_from_server = data_from_server;
                        else
                            response_from_server = "0";
                        break;

                }

            }
            //server.close();

            br.close();
            input.close();
            input_reader.close();
            output.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}


