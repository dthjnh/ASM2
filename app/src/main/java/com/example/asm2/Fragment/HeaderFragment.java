package com.example.asm2.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.asm2.R;

import java.util.Calendar;

public class HeaderFragment extends Fragment {
    private TextView greetingTextView;
    private TextView phoneTextView;
    private TextView dateTimeTextView;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_header, container, false);
        greetingTextView = view.findViewById(R.id.greetingTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);
        dateTimeTextView = view.findViewById(R.id.dateTimeTextView);

        updateDateTime();
        return view;
    }

    private void updateDateTime() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                String currentDateTime = DateFormat.format("EEE, dd MMM yyyy HH:mm:ss", calendar).toString();
                if (dateTimeTextView != null) {
                    dateTimeTextView.setText(currentDateTime);
                }
                handler.postDelayed(this, 1000); // Refresh every second
            }
        }, 0);
    }

    public void updateGreetingText(String text) {
        if (greetingTextView != null) {
            greetingTextView.setText(text);
        }
    }

    public void updatePhoneText(String text) {
        if (phoneTextView != null) {
            phoneTextView.setText(text);
        }
    }
}