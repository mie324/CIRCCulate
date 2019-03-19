package com.example.circculate.Adapter;

import android.util.Log;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.concurrent.Callable;
//
//class Foo implements Callable<String> {
////    private String uriValue;
////    private volatile String text;
////    private volatile StringBuilder lin2;
////    Foo(String uriValue){
////        this.uriValue = uriValue;
////    }
////    Foo(){}
//////    @Override
//////    public void run() {
//////        try{
//////            URL newUrl = new URL(uriValue);
//////            HttpURLConnection uc = (HttpURLConnection)newUrl.openConnection();
//////            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
//////            String line;
//////            lin2 = new StringBuilder();
//////            while ((line = br.readLine()) != null)
//////            {
//////                lin2.append(line);
//////            }
//////            text = ""+lin2;
//////            Log.d("texts", text);
////////                            text_content.setText(lin2);
//////
//////        }catch (IOException e){
//////            e.printStackTrace();
//////
//////        }
//////    }
////    public String getText(){
////        Log.d("text2", text);
////        return text;
////    }
////
////    @Override
////    public String call() throws Exception {
////        return null;
////    }
//}
