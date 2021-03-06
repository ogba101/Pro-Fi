package stabs.com.pro_fi;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by ethan on 2016-12-18.
 */

/**
 * Adapter to manage the recycler view.
 */
public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {
    WifiAdapter context=this;
    public ArrayList<String> profileNames;
    public static TextView wifiTxt;

    public WifiAdapter(ArrayList<String> list, TextView wifiField)
    {
        profileNames = list;
        wifiTxt = wifiField;
    }

    @Override
    public WifiAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wifi_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(WifiAdapter.ViewHolder holder, int position) {
        String profileName = profileNames.get(position);
        holder.wifiCard.setText(profileName);
    }

    @Override
    public int getItemCount() {
        return profileNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView wifiCard;
        public ViewHolder(View v) {
            super(v);
            wifiCard = (TextView)v.findViewById(R.id.name_text_view);
            wifiCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wifiTxt.setText(wifiCard.getText().toString());
                }
            });
        }
    }

}
