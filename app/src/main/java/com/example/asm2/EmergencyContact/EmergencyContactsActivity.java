package com.example.asm2.EmergencyContact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asm2.R;

import java.util.ArrayList;
import java.util.List;

public class EmergencyContactsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText searchBar;
    private ContactsAdapter adapter;
    private List<Contact> contactList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        recyclerView = findViewById(R.id.recyclerViewContacts);
        searchBar = findViewById(R.id.searchBar);

        // Initialize RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactsAdapter(contactList, this::makeCall);
        recyclerView.setAdapter(adapter);

        // Load contacts
        loadContacts();

        // Add search functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterContacts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadContacts() {

        contactList.add(new Contact("Bệnh viện Đa khoa Quốc tế Vinmec", "028-3622-1166"));
        contactList.add(new Contact("Bệnh viện Đại học Y Dược TP.HCM", "028-3855-4269"));
        contactList.add(new Contact("Bệnh viện Chợ Rẫy", "028-3855-4137"));
        contactList.add(new Contact("Bệnh viện Đa khoa Tâm Anh", "028-7102-6789"));
        contactList.add(new Contact("Bệnh viện FV", "028-5411-3333"));
        contactList.add(new Contact("Bệnh viện Hoàn Mỹ Sài Gòn", "028-3990-2468"));

        adapter.notifyDataSetChanged();
    }

    private void filterContacts(String query) {
        List<Contact> filteredList = new ArrayList<>();
        for (Contact contact : contactList) {
            if (contact.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(contact);
            }
        }
        adapter.updateList(filteredList);
    }

    private void makeCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    // Contact class
    static class Contact {
        private final String name;
        private final String number;

        public Contact(String name, String number) {
            this.name = name;
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public String getNumber() {
            return number;
        }
    }

    // Adapter for RecyclerView
    static class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

        private List<Contact> contacts;
        private final OnCallClickListener onCallClickListener;

        public ContactsAdapter(List<Contact> contacts, OnCallClickListener onCallClickListener) {
            this.contacts = contacts;
            this.onCallClickListener = onCallClickListener;
        }

        @NonNull
        @Override
        public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
            return new ContactViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
            Contact contact = contacts.get(position);
            holder.contactName.setText(contact.getName());
            holder.contactNumber.setText(contact.getNumber());
            holder.callIcon.setOnClickListener(v -> onCallClickListener.onCallClick(contact.getNumber()));
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

        public void updateList(List<Contact> updatedContacts) {
            contacts = updatedContacts;
            notifyDataSetChanged();
        }

        static class ContactViewHolder extends RecyclerView.ViewHolder {
            TextView contactName, contactNumber;
            ImageView callIcon;

            public ContactViewHolder(@NonNull View itemView) {
                super(itemView);
                contactName = itemView.findViewById(R.id.contactName);
                contactNumber = itemView.findViewById(R.id.contactNumber);
                callIcon = itemView.findViewById(R.id.callIcon);
            }
        }

        interface OnCallClickListener {
            void onCallClick(String phoneNumber);
        }
    }
}