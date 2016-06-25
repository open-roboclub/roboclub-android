package amu.roboclub.ui.adapters;

import amu.roboclub.R;
import amu.roboclub.models.Contact;
import amu.roboclub.utils.CircleTransform;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private Context context;
    private List<Contact> contacts;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CardView root;
        public TextView name, designation;
        public ImageView avatar;

        public ViewHolder(View view) {
            super(view);
            root = (CardView) view.findViewById(R.id.rootView);
            name = (TextView) view.findViewById(R.id.name);
            designation = (TextView) view.findViewById(R.id.designation);
            avatar = (ImageView) view.findViewById(R.id.avatar);
        }
    }

    public ContactAdapter(Context context, List<Contact> contacts){
        this.context = context;
        this.contacts = contacts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Contact contact = contacts.get(position);
        holder.name.setText(contact.name);
        holder.designation.setText(contact.designation);

        Picasso.with(context).load(contact.imgUrl)
                .placeholder(VectorDrawableCompat.create(context.getResources(), R.drawable.ic_avatar, null))
                .transform(new CircleTransform())
                .into(holder.avatar);

        if(contact.links==null)
            return;

        LinearLayout contactPanel = (LinearLayout) holder.root.findViewById(R.id.contactPanel);

        for(String link : contact.links){
            ImageButton im = new ImageButton(context);

            int[] attrs = new int[]{R.attr.selectableItemBackgroundBorderless};
            TypedArray typedArray = context.obtainStyledAttributes(attrs);
            im.setBackgroundResource(typedArray.getResourceId(0, 0));
            im.setPadding(20,20,20,20);

            Intent i = null;

            if(link.contains("facebook")){
                im.setImageResource(R.drawable.ic_facebook);
                DrawableCompat.setTint(im.getDrawable(), ContextCompat.getColor(context, R.color.fb_blue));

                i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(link));
            } else if(link.contains("linkedin")){
                im.setImageResource(R.drawable.ic_linkedin);
                DrawableCompat.setTint(im.getDrawable(), ContextCompat.getColor(context, R.color.li_blue));

                i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(link));
            } else if(link.contains("plus.google")){
                im.setImageResource(R.drawable.ic_gplus);
                DrawableCompat.setTint(im.getDrawable(), ContextCompat.getColor(context, R.color.gp_red));

                i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(link));
            } else if(link.contains("+91")){
                im.setImageResource(R.drawable.ic_phone);
                DrawableCompat.setTint(im.getDrawable(), ContextCompat.getColor(context, R.color.ph_blue));

                i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+link));
            } else if(link.contains("@")){
                im.setImageResource(R.drawable.ic_email);
                DrawableCompat.setTint(im.getDrawable(), ContextCompat.getColor(context, R.color.em_red));

                i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{ link });
                i.putExtra(Intent.EXTRA_SUBJECT, "To Co-Ordinator, AMU RoboClub");
            }

            if(i!=null) {
                contactPanel.addView(im);
                final Intent intent = i;
                im.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            context.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Snackbar.make(holder.root, "No app can handle the request", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

}