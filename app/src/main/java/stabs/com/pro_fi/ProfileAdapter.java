package stabs.com.pro_fi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Adapter to manage the recycler view.
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    //private Fragment mFragment;
    private static final String TAG ="ProfileAdapter";

    private NetworkService networkService;
    private SharedPreferences sharedPrefs;
    private Context context;
    boolean []fade;
    public ArrayList<Profile> profileNames;
    boolean check;

    public ProfileAdapter(ArrayList<Profile> list, Context context)
    {
        profileNames = list;
        networkService = new NetworkService(context);
        sharedPrefs = context.getSharedPreferences("com.profi.xyz", MODE_PRIVATE);
        this.context = context;
    }

    public void delete_Diag(final View view, final int position) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("Delete Profile")
                .setMessage("Are you sure you want to delete this Profile?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //delete From database
                        DBHelper helper= DBHelper.getInstance(view.getContext());
                        helper.deleteProfile(profileNames.get(position));
                        // continue with delete
                        // delete from screen.
                        Profile toRemove = profileNames.get(position);
                        profileNames.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, 1); // update position


                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    public int getItemCount() {
        return profileNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View v;
        public TextView textView;
        public Button button1;
        public ViewHolder(View v)
        {
            super(v);
            this.v=v;
            int size=profileNames.size();
            fade= new boolean[size];
           // for(int i=0;i<size;i++){fade[i]=false;}
            textView = (TextView)v.findViewById(R.id.name_text_view);
             button1 = (Button)v.findViewById(R.id.button);

        }
    }

    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_card, parent, false);
        return new ViewHolder(v);
    }
    public int checkSelect()
    {
        for(int j=0;j<fade.length;j++)
        {
            if(fade[j]) return j;
        }
        return -1;
    }
    public void select(Profile profileName,View v,int pos)
    {Toast.makeText(v.getContext(), profileName.getName() + " Selected", Toast.LENGTH_SHORT).show();
        v.setAlpha((float) .65);
        fade[pos] = true;}
    public void deSelect(Profile profileName,View v,int pos)
    {
        Toast.makeText(v.getContext(), profileName.getName() + " Deselected", Toast.LENGTH_SHORT).show();
        v.setAlpha((float) 1);
        fade[pos]=false;
    }

    @Override
    public void onBindViewHolder(final ProfileAdapter.ViewHolder holder, final int position) {
        final Profile profileName = profileNames.get(position);
        holder.textView.setText(profileName.getName());
        int activeProfile = sharedPrefs.getInt(NetworkService.ACTIVE_PROFILE, -1);
        Log.d(TAG, profileName.getName() + ": " + profileName.getId() + " V Active profile: " + activeProfile);
        holder.itemView.setActivated(profileName.getId() == activeProfile);

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        holder.v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int activeProfile = sharedPrefs.getInt(NetworkService.ACTIVE_PROFILE, -1);
                networkService.activateProfile(profileName);
                Profile profile = DBHelper.getInstance(context).getProfile(activeProfile);
                int index = profileNames.indexOf(profile);
                ProfileAdapter.this.notifyItemChanged(index);
                ProfileAdapter.this.notifyItemChanged(holder.getAdapterPosition());

                Toast.makeText(v.getContext(), profileName.getName() + " Activated", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        holder.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(v.getContext(), holder.button1);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Delete")) {
                            delete_Diag(v, position);

                        } else if (item.getTitle().equals("Edit")) {
                            edit_Profile(v, position);
                        }
//                            Toast.makeText(
//                                    v.getContext(),
//                                    "You Clicked : " + item.getTitle(),
//                                    Toast.LENGTH_SHORT
//                            ).show();
                        return true;
                    }
                });
                popup.setGravity(Gravity.RIGHT);

                popup.show(); //showing popup menu
            }
        });
    }

    //Switch to edit view
    public void edit_Profile(final View view, final int position){
        Intent myIntent = new Intent(view.getContext(), EditProfileActivity.class);
        DBHelper helper = DBHelper.getInstance(view.getContext());
        Profile selectedProfile = helper.getProfile(profileNames.get(position).getId());//profile to be displayed

        myIntent.putExtra("PROFILE_NAME", selectedProfile.getName());
        myIntent.putExtra("PROFILE_WIFI", selectedProfile.getWifi());
        myIntent.putExtra("PROFILE_RINGTONE", Integer.toString(selectedProfile.getRingtone()));
        myIntent.putExtra("PROFILE_MEDIA", Integer.toString(selectedProfile.getMedia()));
        myIntent.putExtra("PROFILE_NOTIFICATIONS", Integer.toString(selectedProfile.getNotification()));
        myIntent.putExtra("PROFILE_SYSTEM", Integer.toString(selectedProfile.getSystem()));
        myIntent.putExtra("PROFILE_ID",selectedProfile.getId());

        view.getContext().startActivity(myIntent);
    }

}
