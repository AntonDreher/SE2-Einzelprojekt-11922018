package com.example.se2_einzelprojekt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button   sendBtn;
    private TextView matNrTextField;
    private TextView responseTextView;
    String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendBtn = (Button) findViewById(R.id.btnSend);
        sendBtn.setOnClickListener(this);

        matNrTextField = findViewById(R.id.matNrTextField);
        responseTextView = findViewById(R.id.responseTextView);
        getResponseFromSocket();
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == sendBtn.getId()){
            getResponseFromSocket();
            responseTextView.setText(response);
        }
    }

    private void getResponseFromSocket() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket clientSocket = new Socket("se2-isys.aau.at", 53212);
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    outToServer.writeBytes(matNrTextField.getText().toString().trim() + "\n");
                    response = inFromServer.readLine();
                    clientSocket.close();
                }catch(IOException e){
                    Toast.makeText(getApplicationContext(), "Failed to connect", Toast.LENGTH_LONG);
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}