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
    private Button   calcBtn;
    private TextView matNrTextField;
    private TextView responseTextView;
    private TextView calculatedTextView;
    String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendBtn = (Button) findViewById(R.id.btnSend);
        sendBtn.setOnClickListener(this);
        calcBtn = (Button) findViewById(R.id.btnCalculate);
        calcBtn.setOnClickListener(this);
        matNrTextField = findViewById(R.id.matNrTextField);
        responseTextView = findViewById(R.id.responseTextView);
        calculatedTextView = findViewById(R.id.calcOutputText);
        getResponseFromSocket();
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == sendBtn.getId()){
            getResponseFromSocket();
            responseTextView.setText(response);
        }else if(v.getId() == calcBtn.getId()){
            int checksum = getCheckSum(matNrTextField.getText().toString().trim());
            calculatedTextView.setText(convertToBinary(checksum));
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
                    response = "Error";
                }
            }
        });
        thread.start();
    }

    private int getCheckSum(String numberToSum){
        int sum = 0;

        for(int i=0; i<numberToSum.length(); i++){
            sum += Character.getNumericValue(numberToSum.charAt(i));
        }

        return sum;
    }

    private String convertToBinary(int toConvert){
        int currentValue = 0;
        String calculated = "";
        if (toConvert != 0) {
            while (toConvert > 0) {
                currentValue = toConvert % 2;
                calculated = "" + currentValue + calculated;
                toConvert /= 2;
            }
        }else{
            calculated = "0";
        }
        return calculated;
    }
}