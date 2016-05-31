package com.example.burak.currencyconverter;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, EditText.OnClickListener{

    Spinner spinner;
    Spinner spinner2;
    List<String> list = new ArrayList<String>();
    List<String> allInformation = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setOnItemSelectedListener(this);


        getXmlData();
    }


    private void getXmlData()  {

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String xmlUrl="http://www.tcmb.gov.tr/kurlar/today.xml";

        HttpURLConnection connection=null;

        try {
            URL url=new URL(xmlUrl);

            connection= (HttpURLConnection) url.openConnection();

            int baglanti_durumu=connection.getResponseCode();

            if(baglanti_durumu==HttpURLConnection.HTTP_OK){

                if(Locale.getDefault().getLanguage().equals("tr")){
                    list.add("Turk Lirasi");
                    allInformation.add("1:1:1");
                }else {
                    list.add("Turkish Lira");
                    allInformation.add("1:1:1");
                }

                BufferedInputStream stream= new BufferedInputStream(connection.getInputStream());
                DocumentBuilderFactory documentBuilderFactory=DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder=documentBuilderFactory.newDocumentBuilder();

                Document document=documentBuilder.parse(stream); //iki farklı paket var


                NodeList dovizNodeList=document.getElementsByTagName("Currency");

                for (int i=0 ; i<dovizNodeList.getLength() ; i++){
                    Element element= (Element) dovizNodeList.item(i);

                    NodeList nodeListBirim=element.getElementsByTagName("Unit");
                    NodeList nodeListParaBirimi = element.getElementsByTagName("CurrencyName");

                    if(Locale.getDefault().getLanguage().equals("tr"))
                        nodeListParaBirimi = element.getElementsByTagName("Isim");

                    NodeList nodeListAlis=element.getElementsByTagName("BanknoteBuying");
                    NodeList nodeListSatis=element.getElementsByTagName("BanknoteSelling");

                    String unit=nodeListBirim.item(0).getFirstChild().getNodeValue();
                    String currency=nodeListParaBirimi.item(0).getFirstChild().getNodeValue();
                    String banknoteBuying=nodeListAlis.item(0).getFirstChild().getNodeValue();
                    String banknoteSelling=nodeListSatis.item(0).getFirstChild().getNodeValue();

                    allInformation.add( unit+":"+banknoteBuying+":"+banknoteSelling );
                    list.add(currency.toString());
                }

            }

        }catch (Exception e){

            Log.e("Xml parse hatası", e.getMessage().toString());

        }finally {

            if(connection != null)
                connection.disconnect();
        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        // Specify the layout to use when the list of choices appears
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(dataAdapter);

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        // Specify the layout to use when the list of choices appears
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner2.setAdapter(dataAdapter2);
        spinner2.setSelection(1);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == R.id.spinner)
        {
            String itemId = String.valueOf(parent.getSelectedItemId());
            EditText ed = (EditText) findViewById(R.id.editText2);
            ed.setText(itemId);
        }
        else if(spinner.getId() == R.id.spinner2)
        {
            String itemId = String.valueOf(parent.getSelectedItemId());
            EditText ed2 = (EditText) findViewById(R.id.editText3);
            ed2.setText(itemId);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void calculate(View view){

        EditText ed = (EditText) findViewById(R.id.editText);

        if(ed.getText().toString().equals("")) {
            ed.setText("100");
            if (Locale.getDefault().getLanguage().equals("tr"))
                Toast.makeText(this, "Boş Değer Giremezsiniz", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "You Can Not Enter Empty Value", Toast.LENGTH_SHORT).show();
        }

        try{
            Double.parseDouble(ed.getText().toString());
        }  catch(NumberFormatException e){
            ed.setText("100");
            if(Locale.getDefault().getLanguage().equals("tr"))
                Toast.makeText(this, "Sadece Sayı Değerleri Girebilirsiniz", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "You Can Only Enter Decimal And Integer Values", Toast.LENGTH_SHORT).show();
        }

        if(ed.getText().toString().equals("") || Double.parseDouble(ed.getText().toString()) < 0){
            ed.setText("100");
            if(Locale.getDefault().getLanguage().equals("tr"))
                Toast.makeText(this, "Sıfırdan Küçük Değerleri Giremezsiniz", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Your Input Can not Be Less Than 0", Toast.LENGTH_SHORT).show();
        }else{
            double amount = Double.valueOf(ed.getText().toString());

            EditText selling = (EditText) findViewById(R.id.editText2);
            int getMoneyId = Integer.valueOf(selling.getText().toString());

            EditText buying = (EditText) findViewById(R.id.editText3);
            int getMoneyId2 = Integer.valueOf(buying.getText().toString());

            String[] sellingMoneyInformation = allInformation.get(getMoneyId).split(":");
            double banknoteSelling = Double.parseDouble(sellingMoneyInformation[2]);
            int sellingUnit = Integer.parseInt(sellingMoneyInformation[0]);

            String[] buyingMoneyInformation = allInformation.get(getMoneyId2).split(":");
            double banknoteBuying = Double.parseDouble(buyingMoneyInformation[1]);
            int buyingUnit = Integer.parseInt(buyingMoneyInformation[0]);

            double result = amount*(banknoteSelling/sellingUnit)/(banknoteBuying/buyingUnit);
            EditText showResult = (EditText) findViewById(R.id.editText4);
            showResult.setTextColor(Color.rgb(0, 145, 0));
            showResult.setText(String.valueOf(new DecimalFormat("##.##").format(result)));
        }

    }

    public void resetMoneyUnit(View v){
        EditText ed = (EditText) findViewById(R.id.editText);
        ed.setText("");
    }

    @Override
    public void onClick(View v) {

    }
}
