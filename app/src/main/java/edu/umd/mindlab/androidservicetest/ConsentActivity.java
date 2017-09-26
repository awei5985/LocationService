package edu.umd.mindlab.androidservicetest;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

public class ConsentActivity extends AppCompatActivity {

    private Button agreeButton;
    private Button disAgreeButton;
    String pdfFileName;

    public static final String SAMPLE_FILE = "Consent_Smartphone_App.pdf";
    PDFView pdfView;
    Integer pageNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pdfView= (PDFView)findViewById(R.id.pdfView);
        displayFromAsset(SAMPLE_FILE);


        agreeButton = (Button) findViewById(R.id.agreeBtn);

        agreeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if ((pdfView.getCurrentPage()) == (pdfView.getPageCount()-1)) {
                    Intent mainIntent = new Intent(v.getContext(), MainActivity.class);
                    startActivity(mainIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Please scroll down to read the entire consent form",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        disAgreeButton = (Button) findViewById(R.id.disagreeBtn);
        disAgreeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent logIntent = new Intent(v.getContext(), LoginActivity.class);
                logIntent.putExtra("ConsentFailed", false);
                startActivity(logIntent);
            }
        });

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        //.setAction("Action", null).show();
            //}
        //});
    }
    private void displayFromAsset(String assetFileName) {
        pdfFileName = assetFileName;

        pdfView.fromAsset(SAMPLE_FILE)
                .defaultPage(pageNumber)
                .enableSwipe(true)

                .swipeHorizontal(false)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();

    }

}
