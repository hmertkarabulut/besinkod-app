package com.mertkar.besinkod;

import static com.mertkar.besinkod.R.id.soru;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.ads.MobileAds;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView searchEditText;
    private TextView eKoduLabelTextView;
    private TextView eKoduValueTextView;
    private TextView kimyasalAdiLabelTextView;
    private TextView kimyasalAdiValueTextView;
    private TextView aciklamaLabelTextView;
    private TextView aciklamaValueTextView;
    private TextView helalHaramLabelTextView;
    private TextView helalHaramValueTextView;
    private ECodeDataSource dataSource;
    private TextView bilgilendirmetextView;
    private ImageView imageView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));
        }

        bilgilendirmetextView = findViewById(R.id.bilgilendirmetext);
        searchEditText = findViewById(R.id.search_box);
        eKoduLabelTextView = findViewById(R.id.eKoduLabel);
        eKoduValueTextView = findViewById(R.id.eKoduValue);
        kimyasalAdiLabelTextView = findViewById(R.id.kimyasalAdiLabel);
        kimyasalAdiValueTextView = findViewById(R.id.kimyasalAdiValue);
        aciklamaLabelTextView = findViewById(R.id.aciklamaLabel);
        aciklamaValueTextView = findViewById(R.id.aciklamaValue);
        helalHaramLabelTextView = findViewById(R.id.helalHaramLabel);
        helalHaramValueTextView = findViewById(R.id.helalHaramValue);
        TextView textView = findViewById(R.id.ekodbaslik);

        imageView = findViewById(soru);
        imageView.setVisibility(View.VISIBLE); // Başlangıçta görünür yapın

        String text1 = "E-Kodları";
        String text2 = " veya";
        String text3 = "\nİçerikleri Arayın !";

        SpannableString spannable1 = new SpannableString(text1);
        SpannableString spannable2 = new SpannableString(text2);
        SpannableString spannable3 = new SpannableString(text3);

        Typeface font1 = ResourcesCompat.getFont(this, R.font.font);
        Typeface font2 = ResourcesCompat.getFont(this, R.font.font1);

        spannable1.setSpan(new CustomTypefaceSpan("", font2), 0, text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable2.setSpan(new CustomTypefaceSpan("", font1), 0, text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable2.setSpan(new ForegroundColorSpan(Color.parseColor("#4cbf9c")), 0, text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable3.setSpan(new CustomTypefaceSpan("", font2), 0, text3.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(spannable1);
        builder.append(spannable2);
        builder.append(spannable3);

        textView.setText(builder);

        new Thread(() -> MobileAds.initialize(this, initializationStatus -> {})).start();

        dataSource = new ECodeDataSource(this);
        dataSource.open();

        eKoduLabelTextView.setVisibility(View.GONE);
        kimyasalAdiLabelTextView.setVisibility(View.GONE);
        aciklamaLabelTextView.setVisibility(View.GONE);
        helalHaramLabelTextView.setVisibility(View.GONE);
        eKoduValueTextView.setVisibility(View.GONE);
        kimyasalAdiValueTextView.setVisibility(View.GONE);
        aciklamaValueTextView.setVisibility(View.GONE);
        helalHaramValueTextView.setVisibility(View.GONE);

        // Veritabanından tüm kodları ve isimleri çekin
        List<String> codesAndNames = dataSource.getAllCodesAndNames();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, codesAndNames);
        searchEditText.setAdapter(adapter);

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                performSearch();
                return true;
            }
            return false;
        });

        searchEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (searchEditText.getRight() - searchEditText.getCompoundDrawables()[2].getBounds().width())) {
                    performSearch();
                    return true;
                }
            }
            return false;
        });
    }

    private void performSearch() {
        String input = searchEditText.getText().toString().trim();
        String result = dataSource.getDetailsForCodeOrName(input);
        closeKeyboard();

        if (result != null) {
            eKoduLabelTextView.setVisibility(View.VISIBLE);
            eKoduValueTextView.setVisibility(View.VISIBLE);
            bilgilendirmetextView.setVisibility(View.GONE);
            kimyasalAdiLabelTextView.setVisibility(View.VISIBLE);
            kimyasalAdiValueTextView.setVisibility(View.VISIBLE);
            aciklamaLabelTextView.setVisibility(View.VISIBLE);
            aciklamaValueTextView.setVisibility(View.VISIBLE);
            helalHaramLabelTextView.setVisibility(View.VISIBLE);
            helalHaramValueTextView.setVisibility(View.VISIBLE);

            String[] details = result.split("\n");
            if (details.length >= 4) {
                eKoduValueTextView.setText(details[0].split(": ")[1]);
                kimyasalAdiValueTextView.setText(details[1].split(": ")[1]);
                aciklamaValueTextView.setText(details[2].split(": ")[1]);
                helalHaramValueTextView.setText(details[3].split(": ")[1]);
            }

            // Arama yapıldıktan sonra ImageView'i gizleyin
            imageView.setVisibility(View.GONE);
        } else {
            eKoduLabelTextView.setVisibility(View.VISIBLE);
            kimyasalAdiLabelTextView.setVisibility(View.VISIBLE);
            aciklamaLabelTextView.setVisibility(View.VISIBLE);
            helalHaramLabelTextView.setVisibility(View.VISIBLE);

            eKoduValueTextView.setVisibility(View.VISIBLE);
            kimyasalAdiValueTextView.setVisibility(View.VISIBLE);
            aciklamaValueTextView.setVisibility(View.VISIBLE);
            helalHaramValueTextView.setVisibility(View.VISIBLE);

            eKoduValueTextView.setText("Bulunamadı");
            kimyasalAdiValueTextView.setText("Bulunamadı");
            aciklamaValueTextView.setText("Bulunamadı");
            helalHaramValueTextView.setText("Bulunamadı");

            // Arama yapıldıktan sonra ImageView'i gizleyin
            imageView.setVisibility(View.GONE);
        }
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            View view = this.getCurrentFocus();
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }
}
