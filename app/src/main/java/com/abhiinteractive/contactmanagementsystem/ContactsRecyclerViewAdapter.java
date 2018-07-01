package com.abhiinteractive.contactmanagementsystem;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactsRecyclerViewAdapter.ViewHolder> {

    Context mContext;
    ArrayList<Contact> list;

    public ContactsRecyclerViewAdapter(Context mContext, ArrayList<Contact> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.name.setText(list.get(position).getName());

        //Intent to send email on clicking the email icon. Can be handled by email apps.
        holder.email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] addresses = {list.get(position).getEmail()};
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent);
                }
            }
        });

        //Intent to call the contact. Opens the dialer.
        holder.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + list.get(position).getPhone()));
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent);
                }
            }
        });

        //Intent to open the website of the contact.
        holder.website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri webpage = Uri.parse(list.get(position).getWebsite());
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent);
                }
            }
        });

        //If the name is clicked, open a dialog which allows you to edit the contact details or delete it.
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Create new dialog
                final Dialog updateDialog = new Dialog(mContext);
                updateDialog.setContentView(R.layout.edit_contact_dialog);

                //Get the initial text's and set them on the edittexts
                final TextView nameTextView = updateDialog.findViewById(R.id.name_textview);
                nameTextView.setText(list.get(position).getName());
                final String name = nameTextView.getText().toString();
                final EditText emailEditText = updateDialog.findViewById(R.id.update_email_edittext);
                emailEditText.setText(list.get(position).getEmail());
                final EditText phoneEditText = updateDialog.findViewById(R.id.update_phone_edittext);
                phoneEditText.setText(list.get(position).getPhone());
                final EditText websiteEditText = updateDialog.findViewById(R.id.update_website_edittext);
                websiteEditText.setText(list.get(position).getWebsite());

                Button update = updateDialog.findViewById(R.id.update_contact_button);
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DBHelper dbHelper = new DBHelper(mContext);
                        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

                        //Get the edited strings from edit text's and also set them in the Contact entity.
                        String email = emailEditText.getText().toString();
                        list.get(position).setEmail(email);
                        String phone = phoneEditText.getText().toString();
                        list.get(position).setPhone(phone);
                        String website = websiteEditText.getText().toString();
                        list.get(position).setWebsite(website);

                        //Validation, either leave empty or enter something valid.
                        if (email.equals("") || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            Toast.makeText(mContext, "Invalid email", Toast.LENGTH_LONG).show();
                        } else if (phone.equals("") || !Patterns.PHONE.matcher(phone).matches()) {
                            Toast.makeText(mContext, "Invalid phone", Toast.LENGTH_LONG).show();
                        } else if (website.equals("") || !Patterns.WEB_URL.matcher(website).matches()) {
                            Toast.makeText(mContext, "Invalid website", Toast.LENGTH_LONG).show();
                        } else {
                            if (ContactList.checkForNetwork(mContext)) {
                                //TODO: Write class for updating on server
                                dbHelper.updateDatabase(name, email, phone, website, DBContract.SYNC_SUCCESS, sqLiteDatabase);
                            } else {
                                dbHelper.updateDatabase(name, email, phone, website, DBContract.UPDATE_PENDING, sqLiteDatabase);
                            }
                        }

                        updateDialog.dismiss();
                    }
                });

                final Button delete = updateDialog.findViewById(R.id.delete_contact_button);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DBHelper dbHelper = new DBHelper(mContext);
                        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

                        if (ContactList.checkForNetwork(mContext)) {
                            //TODO: Write class for deleting on server
                            dbHelper.deleteRow(name, DBContract.SYNC_SUCCESS, sqLiteDatabase);
                        } else {
                            dbHelper.deleteRow(name, DBContract.DELETE_PENDING, sqLiteDatabase);
                        }

                        list.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());

                        updateDialog.dismiss();
                    }
                });

                updateDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        LinearLayout parent;
        ImageView email, phone, website;

        public ViewHolder(View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.contact_item_parent);
            name = itemView.findViewById(R.id.contact_name);
            email = itemView.findViewById(R.id.contact_email);
            phone = itemView.findViewById(R.id.contact_phone);
            website = itemView.findViewById(R.id.contact_website);
        }
    }
}
