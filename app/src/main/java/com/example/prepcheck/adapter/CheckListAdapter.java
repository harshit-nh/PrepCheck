package com.example.prepcheck.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prepcheck.CheckList;
import com.example.prepcheck.Constants.MyConstants;
import com.example.prepcheck.DataBase.RoomDB;
import com.example.prepcheck.Models.Items;
import com.example.prepcheck.R;

import java.util.List;

public class CheckListAdapter extends RecyclerView.Adapter<CheckListViewHolder>{

    Context context;
    List<Items> itemsList;
    RoomDB database;
    String show;

    public CheckListAdapter() {
    }

    public CheckListAdapter(Context context, List<Items> itemsList, RoomDB database, String show) {
        this.context = context;
        this.itemsList = itemsList;
        this.database = database;
        this.show = show;

        if(itemsList.isEmpty()){
            Toast.makeText(context.getApplicationContext(), "Nothing to show!", Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public CheckListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.check_list_item,parent,false);
        return new CheckListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckListViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.checkBox.setText(itemsList.get(position).getItemname());
        holder.checkBox.setChecked(itemsList.get(position).getChecked());

        if(MyConstants.FALSE_STRING.equals(show)){
            holder.btnDelete.setVisibility(View.GONE);
            holder.linearLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.border_one));
        }else{
            if(itemsList.get(position).getChecked()){
                holder.linearLayout.setBackgroundColor(Color.parseColor("#8e546f"));
            }else{
                holder.linearLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.border_one));
            }
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean check = holder.checkBox.isChecked();
                database.mainDao().checkUncheck(itemsList.get(position).getID(), check);

                if(MyConstants.FALSE_STRING.equals(show)){
                    itemsList = database.mainDao().getAllSelected(true);
                    notifyDataSetChanged();
                }else{
                    itemsList.get(position).setChecked(check);
                    notifyDataSetChanged();
                    
                    Toast toastMsg = null;
                    if(toastMsg!= null){
                        toastMsg.cancel();
                    }

                    if(itemsList.get(position).getChecked()){
                        toastMsg = Toast.makeText(context,"("+holder.checkBox.getText()+") Packed",Toast.LENGTH_SHORT);
                    }else{
                        toastMsg = Toast.makeText(context,"("+holder.checkBox.getText()+") Un-Packed",Toast.LENGTH_SHORT);
                    }
                    toastMsg.show();
                }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete ( "+itemsList.get(position).getItemname()+" )")
                        .setMessage("Are your sure?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                database.mainDao().delete(itemsList.get(position));
                                itemsList.remove(itemsList.get(position));
                                notifyDataSetChanged();
                            }

                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setIcon(R.drawable.ic_delete_forever)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }
}

class CheckListViewHolder extends RecyclerView.ViewHolder{

    LinearLayout linearLayout;
    CheckBox checkBox;
    Button btnDelete;

    public CheckListViewHolder(@NonNull View itemView) {
        super(itemView);

        linearLayout = itemView.findViewById(R.id.linear_layout3);
        checkBox = itemView.findViewById(R.id.checkBox);
        btnDelete = itemView.findViewById(R.id.btnDelete);

    }
}


